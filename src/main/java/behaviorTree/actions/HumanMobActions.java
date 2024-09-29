/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package behaviorTree.actions;

import Utils.GridUtils;
import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.FAILURE;
import static behaviorTree.NodeCompletionStatus.RUNNING;
import static behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import client.ClientGameAppState;
import client.Main;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import debugging.DebugUtils;
import game.entities.Collidable;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import game.entities.mobs.Player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.DestructibleDamageReceiveMessage;
import pathfinding.AStar;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class HumanMobActions {

    public static class AttackAction extends NodeAction {

        private float currentGunCooldown;
        private float TEST_GUN_COOLDOWN = 1.45f;
        private float TEST_GUN_RANGE = 2;
        private float TEST_DAMAGE = 3.75f;
        private HumanMob human;
        private Destructible target;

        @Override
        public NodeCompletionStatus execute(Context context) {
            human = (HumanMob) context.getBlackboard().get(SimpleHumanMobContext.STEERED_HUMAN);
            target = (Destructible) context.getBlackboard().get(SimpleHumanMobContext.TARGET_DESTRUCTIBLE);

            currentGunCooldown -= ServerMain.getTimePerFrame();
            if (currentGunCooldown < 0) {
                if (target != null) {

                    var distance = human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation());
                    if (distance > TEST_GUN_RANGE) {
                        return NodeCompletionStatus.FAILURE;
                    }

                    Main.getInstance().enqueue(() -> {
                        var emsg = new DestructibleDamageReceiveMessage(target.getId(), human.getId(), TEST_DAMAGE);
                        emsg.applyDestructibleDamageAndNotifyClients(target, ServerMain.getInstance());
//                    var bullet = SceneUtils.createBullet(destructibleTarget.getNode().getWorldTranslation().clone());
//                    Main.debugNode.attachChild(bullet);
//                    
//                    bullet.move(beetle.getNode().getWorldTranslation());
                    });
//                System.out.println("attacking!" + new Random().nextInt(2));
                    currentGunCooldown = TEST_GUN_COOLDOWN;

                }
            }
//        System.out.println("Attacking!");
            return NodeCompletionStatus.SUCCESS;
        }
    }

    public static class CheckForTargetAction extends NodeAction {

        private Random r = new Random();
        private List<Destructible> viableTargets = new ArrayList<>();
        private float TEST_SIGHT_RANGE = 20;
        private float CONE_WIDTH_DEG = 70;

        private float shortVisionRadiusSquared = 4; // vision radius in a circle ("hearing")

        @Override
        public NodeCompletionStatus execute(Context context) {
            var hc = ((SimpleHumanMobContext) context);

            var human = (HumanMob) context.getBlackboard().get(SimpleHumanMobContext.STEERED_HUMAN);

            /*  if cannot SWITCH to new target 
            && has target 
            && sees target -> return success
             */
            if (!hc.canAcquireNewTarget() && hc.getCurrentTarget() != null && humanSeesMob(human, hc.getCurrentTarget())) {
                hc.setDesiredLookDirection(
                        hc.getCurrentTarget().getNode().getWorldTranslation().subtract(
                                human.getNode().getWorldTranslation()
                        )
                );
                return SUCCESS;
            }

            /* else check for closest target
            because we allow to search for target when:
            cannot get new target && has no target -> search for new target immediately
             */
            if (!viableTargets.isEmpty()) {
                viableTargets.clear();
            }

            var mobsClose = ServerMain.getInstance().getGrid().getNearbyInRadius(human.getNode().getWorldTranslation(), TEST_SIGHT_RANGE);
            for (var mob : mobsClose) {
                if (mob instanceof Destructible d
                        && (mob instanceof MudBeetle || mob instanceof Player)
                        && (humanSeesMob(human, d))) {
                    viableTargets.add(d);
                }
            }

            Destructible newTarget = null;

            Comparator<Destructible> distanceComparator = (a, b) -> Float.compare(human.getNode().getWorldTranslation().distance(a.getNode().getWorldTranslation()), human.getNode().getWorldTranslation().distance(b.getNode().getWorldTranslation()));
            viableTargets.sort(distanceComparator);

            for (var viableTarget : viableTargets) {
                boolean viable = true;
                var distance = human.getNode().getWorldTranslation().distance(viableTarget.getNode().getWorldTranslation());

                for (var result : checkRayBetweenTarget(human, viableTarget, distance)) {
                    var name = result.getGeometry().getName();
                    System.out.println(human + " eyesight collided with " + name);
                    if (name != viableTarget.getNode().getName() && name != human.getNode().getName()) {
                        viable = false;
                        break;
                    }
                }

                if (viable) {
                    newTarget = viableTarget;
                    break;
                }
            }

            if (newTarget == null) {
                if (hc.getCurrentTarget() != null && hc.getCurrentTarget().getHealth() > 0) {
                    queuePathfindingToTargetLastPos(hc);
                }
                hc.setTargetDestructible(null);
                context.getBlackboard().put(SimpleHumanMobContext.INTERRUPT_WALKING, false);

                return NodeCompletionStatus.FAILURE;
            }

            hc.setTargetDestructible(newTarget);
            context.getBlackboard().put(SimpleHumanMobContext.INTERRUPT_WALKING, false);

            setRandomPathfindCooldown(hc);
            hc.setDesiredLookDirection(
                    hc.getCurrentTarget().getNode().getWorldTranslation().subtract(
                            human.getNode().getWorldTranslation()
                    )
            );
            return NodeCompletionStatus.SUCCESS;
        }

        private void queuePathfindingToTargetLastPos(SimpleHumanMobContext hc) {
            if (hc.getCurrentTarget() != null) {
                hc.resetPath();
                var lastSeenTargetPos = hc.getCurrentTarget().getNode().getWorldTranslation();
                hc.setLastKnownTargetPosition(lastSeenTargetPos);
            }
        }

        private CollisionResults checkRayBetweenTarget(Destructible caster, Destructible target, float distance) {
            CollisionResults results = new CollisionResults();

            var eyesHeight = 2f;
            var rayStart = caster.getNode().getWorldTranslation().add(0, eyesHeight, 0);
            var rayDestination = target.getNode().getWorldTranslation().add(0, eyesHeight, 0).subtractLocal(rayStart).normalizeLocal();
            Ray ray = new Ray(rayStart, rayDestination);
            ray.setLimit(distance);
            ClientGameAppState.getInstance().getMapNode().collideWith(ray, results);
            return results;
        }

        private void setRandomPathfindCooldown(SimpleHumanMobContext hc) {
            // -0.1f because lower bound has to be smaller than upperbound
            var pathfindCdLowerBound = Math.max(hc.getFindRandomPathTimer() - 0.1f, 2);
            var pathfindCd = r.nextFloat(
                    pathfindCdLowerBound,
                    hc.getFindRandomPathCooldown()
            );
            hc.setFindRandomPathTimer(pathfindCd);
        }

        public boolean isInCone(float coneAngle, Destructible thisMob, Destructible mob) {  // coneAngle goes both ways, so 45 is 90* cone
            var node = thisMob.getNode();
            Vector3f casterRot = node.getWorldRotation().getRotationColumn(2).normalize();
            Vector3f casterPos = node.getWorldTranslation();
            Vector3f targetPos;
            targetPos = mob.getNode().getWorldTranslation();
            targetPos = casterPos.subtract(targetPos).mult(-1).normalizeLocal();
            double dotProd = casterRot.getX() * targetPos.getX() + casterRot.getY() * targetPos.getY() + casterRot.getZ() * targetPos.getZ();
            double magA = Math.sqrt(casterRot.getX() * casterRot.getX() + casterRot.getY() * casterRot.getY() + casterRot.getZ() * casterRot.getZ());
            double magB = Math.sqrt(targetPos.getX() * targetPos.getX() + targetPos.getY() * targetPos.getY() + targetPos.getZ() * targetPos.getZ());
            double angle = Math.toDegrees(Math.acos(dotProd / (magA * magB)));
            if (Double.isNaN(angle)) {
                if (dotProd / (magA * magB) < 0) {
                    angle = coneAngle + 1;
                } else {
                    angle = 0;
                }
            }
            return angle <= coneAngle && node.getWorldTranslation().distance(mob.getNode().getWorldTranslation()) <= TEST_SIGHT_RANGE;
        }

        private boolean humanSeesMob(HumanMob human, Destructible mob) {
            return mob.getNode().getWorldTranslation().distanceSquared(human.getNode().getWorldTranslation()) <= shortVisionRadiusSquared || isInCone(CONE_WIDTH_DEG, human, mob);
        }
    }

    public static class IsPathfindingNeededAction extends NodeAction {

        @Override
        public NodeCompletionStatus execute(Context context) {
            var hc = (SimpleHumanMobContext) context;
            hc.setFindRandomPathTimer(hc.getFindRandomPathTimer() - ServerMain.getTimePerFrame());
            if (shouldPathfind(hc)) {
                hc.setFindRandomPathTimer(hc.getFindRandomPathCooldown());
                return NodeCompletionStatus.SUCCESS;
            }
            return NodeCompletionStatus.FAILURE;
        }

        private boolean shouldPathfind(SimpleHumanMobContext hc) {
            return (hc.getCurrentPath() == null
                    && hc.getFindRandomPathTimer() < 0
                    && hc.getCurrentTarget() == null)
                    || hc.getLastKnownTargetPosition() != null /*
                always pathfind.
                     */;
        }

    }

    public static class MoveInRangeAction extends NodeAction {

        private float TEST_WEAPON_RANGE = 3;
        private Vector3f moveVec;
        private Destructible target;
        private HumanMob human;
        private SimpleHumanMobContext hc;

        @Override
        public NodeCompletionStatus execute(Context context) {
            hc = (SimpleHumanMobContext) context;
            human = hc.getHumanMob();

            if (human.isDead()) {
                return STOP_TREE_EXECUTION;
            }

            target = hc.getCurrentTarget();
            moveVec = target.getNode().getWorldTranslation()
                    .subtract(human.getNode().getWorldTranslation())
                    .normalizeLocal();
            moveVec.multLocal(human.getCachedSpeed() * ServerMain.getTimePerFrame());

            Main.getInstance().enqueue(() -> {
                human.moveServer(moveVec);
            });

            if (human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation()) <= TEST_WEAPON_RANGE) {
                return NodeCompletionStatus.SUCCESS;
            }

            return NodeCompletionStatus.FAILURE;
        }

    }

    public static class PathfindAction extends NodeAction {

        private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();

        private final Random random = new Random();

        @Override
        public NodeCompletionStatus execute(Context context) {
            var hc = (SimpleHumanMobContext) context;
            if (hc.getPathfindingFuture() == null) {
                decidePathfinding(hc);
            } else if (isPathReady(hc)) {
                startFollowingPath(hc);
                return SUCCESS;
            }
            return RUNNING;
        }

        public void pathfindClose(SimpleHumanMobContext context) {
            Callable<List<Vector3f>> c = () -> {
                var mobPos = context.getHumanMob().getNode().getWorldTranslation();

                var x = new Random().nextInt(-30, 31);
                var y = 0;
                var z = new Random().nextInt(-30, 31);

                var path = AStar.findPath(mobPos, mobPos.add(x, y, z));
                return setRandomDestinationFromCellCenter(path);
            };

            Future pf = ServerMain.pathfindingExecutor.submit(c);
//            Future pf = Main.getInstance().enqueue(c);

            context.setPathfindingFuture(pf);

        }

        public void pathfindToLastKnownTargetLocation(SimpleHumanMobContext context) {
            var pathfindPos = context.getLastKnownTargetPosition();
            context.setLastKnownTargetPosition(null);
            Callable<List<Vector3f>> c = () -> {
                var mobPos = context.getHumanMob().getNode().getWorldTranslation();
                var path = AStar.findPath(mobPos, pathfindPos);
                return path;
            };
            Future pf = ServerMain.pathfindingExecutor.submit(c);
//            Future pf = Main.getInstance().enqueue(c);

            context.setPathfindingFuture(pf);

        }

        private boolean isPathReady(SimpleHumanMobContext hc) {
            return hc.getPathfindingFuture() != null && hc.getPathfindingFuture().isDone();
        }

        private void startFollowingPath(SimpleHumanMobContext hc) {
            try {
                System.err.println("for mob " + hc.getHumanMob());
                var future = hc.getPathfindingFuture();
                System.err.println("future done/cancelled? " + future.isDone() + "/" + future.isCancelled());
                System.err.println("future " + future);
                var futurePath = future.get();
                System.out.println("future get:" + futurePath);

                List<Vector3f> path = null;
                if (futurePath != null) {
                    path = (List<Vector3f>) futurePath;
                }
                hc.setCurrentPath(path);
                hc.setPathfindingFuture(null);

            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(WalkAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.err.println("ending for mob " + hc.getHumanMob());

        }

        private void decidePathfinding(SimpleHumanMobContext hc) {
            if (hc.getLastKnownTargetPosition() == null) {
                pathfindClose(hc);
            }
            if (hc.getLastKnownTargetPosition() != null) {
                pathfindToLastKnownTargetLocation(hc);
//            System.out.println("pathing to last known...");
            }
        }

        private List<Vector3f> setRandomDestinationFromCellCenter(List<Vector3f> path) {
            if (path == null) {
                return null;
            }
            var cellCenter = path.get(path.size() - 1);
            var hitboxSize = 0.5f;
            cellCenter.addLocal(random.nextFloat(-(BLOCK_SIZE / 2 - hitboxSize), (BLOCK_SIZE / 2 - hitboxSize)), 0, random.nextFloat(-(BLOCK_SIZE / 2 - hitboxSize), (BLOCK_SIZE / 2 - hitboxSize)));
            return path;
        }

    }

    public static class ResetPathAction extends NodeAction {

        @Override
        public NodeCompletionStatus execute(Context context) {
            ((SimpleHumanMobContext) context).resetPath();
            return SUCCESS;
        }

    }

    public static class WalkAction extends NodeAction {

        private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();
        private Vector3f currentNode;

        @Override
        public NodeCompletionStatus execute(Context context) {

            var hc = (SimpleHumanMobContext) context;
            var beetle = hc.getHumanMob();

            if (beetle.isDead()) {
                return STOP_TREE_EXECUTION;
            }

            var interrupt = (boolean) context.getBlackboard().get(SimpleHumanMobContext.INTERRUPT_WALKING);
            if (interrupt) {
                return NodeCompletionStatus.FAILURE;
            }
            var isFollowingFinished = followPath(beetle, hc);

            if (isFollowingFinished) {
                return NodeCompletionStatus.SUCCESS;
            } else if (hc.getCurrentPath() == null) {
                return NodeCompletionStatus.FAILURE;
            } else {
                return NodeCompletionStatus.RUNNING;
            }

        }

        private boolean followPath(HumanMob human, SimpleHumanMobContext hc) {
            if (hc.getCurrentPath() == null) {
                return false;
            }
            boolean achievedDestination = false;
            var currentNodeIndex = hc.getCurrentNodeIndex();
            currentNode = hc.getCurrentPath().get(currentNodeIndex).mult(BLOCK_SIZE);
            var distance = human.getNode().getWorldTranslation().distance(currentNode);

            hc.setDesiredLookDirection(currentNode.subtract(human.getNode().getWorldTranslation()));

            if (distance < human.getCachedSpeed() * ServerMain.getTimePerFrame() * 2 || canMarkCurrentNodeAsVisited(hc, currentNodeIndex, currentNode, human.getNode().getWorldTranslation())) {
                hc.setCurrentNodeIndex(++currentNodeIndex);
                if (currentNodeIndex >= hc.getCurrentPath().size()) {
                    achievedDestination = true;
                    return achievedDestination;
                }
            } else {

                var moveVec = currentNode.subtract(human.getNode().getWorldTranslation()).normalizeLocal();
                moveVec.multLocal(human.getCachedSpeed() * ServerMain.getTimePerFrame());
                Main.getInstance().enqueue(() -> {
//                // mobs pushing out of the way
//                float avoidanceForce = 5 * tpf;
//                for (HumanMob otherMob : Main.mobs) {
//                    if (otherMob != human) {
//                        float d = human.getNode().getWorldTranslation().distance(otherMob.getNode().getWorldTranslation());
//                        if (d < BLOCK_SIZE / 2) {
//                            // Assume Vector3f has necessary methods like add, scale, negate, normalize 
//                            Vector3f avoidanceVector = human.getNode().getWorldTranslation().add(otherMob.getNode().getWorldTranslation().negate()).normalize();
//                            // Assume Vector3f has necessary methods like add, scale 
//                            avoidanceVector.normalizeLocal().multLocal(avoidanceForce);
//                            moveVec.addLocal(avoidanceVector);
//
//                            if (canPushTEST(otherMob)) {
////                                otherMob.getSpatial().move(avoidanceVector.negate());
//                            }
//
//                        }
//                    }
//                }

                    human.moveServer(moveVec);
                });
            }
            return false;

        }

        private boolean canMarkCurrentNodeAsVisited(SimpleHumanMobContext hc, int currentNodeIndex, Vector3f currentNode, Vector3f mobPos) {

            int mobPosX = (int) (Math.floor(mobPos.x / BLOCK_SIZE));
            int mobPosY = (int) (Math.floor(mobPos.y / BLOCK_SIZE));
            int mobPosZ = (int) (Math.floor(mobPos.z / BLOCK_SIZE));
            int currentNodeX = (int) (Math.floor(currentNode.x / BLOCK_SIZE / 2));
            int currentNodeY = (int) (Math.floor(currentNode.y / BLOCK_SIZE / 2));
            int currentNodeZ = (int) (Math.floor(currentNode.z / BLOCK_SIZE / 2));

            return mobPosX == currentNodeX && mobPosY == currentNodeY && mobPosZ == currentNodeZ && currentNodeIndex < hc.getCurrentPath().size(); //-1;
            /* all nodes but the last can be flagged as achieved when entering the tile. only the last
        node has to be reached perfeclty
             */
        }

    }

    public static class GetCurrentTimestampAction extends NodeAction {

        @Override
        public NodeCompletionStatus execute(Context context) {
            ((SimpleHumanMobContext) context).setCurrentUpdateTimestamp(System.currentTimeMillis());
            return SUCCESS;
        }

    }

    public static class RotateToDesiredRotationAction extends NodeAction {

        private float dotProduct;
        private Vector3f currentDir;
        private Vector3f desiredDir;
        private Vector3f rotationAxis;
        private Quaternion stepRotation;
        private Quaternion newRotation;
        private final float rotationSpeedPerSecond = FastMath.TWO_PI;

        @Override
        public NodeCompletionStatus execute(Context context) {
            var hc = (SimpleHumanMobContext) context;

            if (hc.getDesiredLookDirection() != null) {
                currentDir = hc.getHumanMob().getNode().getLocalRotation().getRotationColumn(2).normalize();
                desiredDir = hc.getDesiredLookDirection().normalizeLocal();

                dotProduct = currentDir.dot(desiredDir);

                if (dotProduct < 0.999f) { // 2.5 degrees difference

                    float angleDiff = FastMath.acos(dotProduct); 
                    float rotationStep = Math.min(rotationSpeedPerSecond*ServerMain.getTimePerFrame(), angleDiff);
                    rotationAxis = currentDir.cross(desiredDir).normalize();
                    stepRotation = new Quaternion().fromAngleAxis(rotationStep, rotationAxis);

                    newRotation = stepRotation.multLocal(hc.getHumanMob().getNode().getLocalRotation());
                    hc.getHumanMob().getNode().setLocalRotation(newRotation);
                }
            }

            return SUCCESS;
        }

    }

}
