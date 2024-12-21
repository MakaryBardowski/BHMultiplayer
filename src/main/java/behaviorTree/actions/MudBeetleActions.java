/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package behaviorTree.actions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.RUNNING;
import static behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import client.ClientGameAppState;
import client.Main;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
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
public class MudBeetleActions {

    public static class AttackAction extends NodeAction {

        private float currentGunCooldown;
        private float TEST_GUN_COOLDOWN = 1;
        private float TEST_GUN_RANGE = 2;
        private float TEST_DAMAGE = 2;

        @Override
        public NodeCompletionStatus execute(Context context) {
            var beetle = (MudBeetle) context.getBlackboard().get(MudBeetleContext.STEERED_MUD_BEETLE);
            var target = context.getBlackboard().get(MudBeetleContext.TARGET_DESTRUCTIBLE);
//if(target != null)
//    System.out.println("target " + target + new Random().nextInt());
            currentGunCooldown -= ServerMain.getTimePerFrame();
            if (currentGunCooldown < 0) {
                if (target != null) {

                    var destructibleTarget = (Destructible) target;

                    var distance = beetle.getNode().getWorldTranslation().distance(destructibleTarget.getNode().getWorldTranslation());
                    if (distance > TEST_GUN_RANGE) {
                        return NodeCompletionStatus.FAILURE;
                    }

                    Main.getInstance().enqueue(() -> {
                        var emsg = new DestructibleDamageReceiveMessage(destructibleTarget.getId(),beetle.getId(), TEST_DAMAGE);
                        emsg.applyDestructibleDamageAndNotifyClients(destructibleTarget, ServerMain.getInstance());
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
        private static List<Destructible> viableTargets = new ArrayList<>();
        private static float TEST_SIGHT_RANGE = 16;
        private static float CONE_WIDTH_DEG = 45;

        private Vector3f previousTargetPosition = null;

        @Override
        public NodeCompletionStatus execute(Context context) {
            if (viableTargets.size() > 0) {
                viableTargets.clear();
            }
            var human = (MudBeetle) context.getBlackboard().get(MudBeetleContext.STEERED_MUD_BEETLE);

            var hc = ((MudBeetleContext) context);
//        var mobsClose = new HashSet<Collidable>();
            var mobsClose = ServerMain.getInstance().getGrid().getNearbyInRadius(human.getNode().getWorldTranslation(), TEST_SIGHT_RANGE);
            for (var mob : mobsClose) {
                if (mob instanceof Destructible d
                        && mob instanceof HumanMob
                        && isInCone(CONE_WIDTH_DEG, human, d)) {
                    viableTargets.add(d);
                }
            }

            Destructible currentTarget = null;

            Comparator<Destructible> distanceComparator = (a, b) -> Float.compare(human.getNode().getWorldTranslation().distance(a.getNode().getWorldTranslation()), human.getNode().getWorldTranslation().distance(b.getNode().getWorldTranslation()));
            viableTargets.sort(distanceComparator);

            for (var viableTarget : viableTargets) {
                boolean viable = true;
                var distance = human.getNode().getWorldTranslation().distance(viableTarget.getNode().getWorldTranslation());

                for (var result : checkRayBetweenTarget(human, viableTarget, distance)) {
                    var name = result.getGeometry().getName();
                    if (name != viableTarget.getNode().getName() && name != human.getNode().getName()) {
                        viable = false;
                        break;
                    }
                }

                if (viable) {
                    currentTarget = viableTarget;
                    break;
                }
            }

            if (currentTarget != null) {

                if ((previousTargetPosition != null && hc.getTargetDestructible() == currentTarget
                        && currentTarget.getNode().getWorldTranslation().distanceSquared(previousTargetPosition) > 0.16f) || previousTargetPosition == null) {

                    var currentTargetPos = currentTarget.getNode().getWorldTranslation();
                    Main.getInstance().enqueue(() -> {
                        human.getNode().lookAt(currentTargetPos, Vector3f.UNIT_Y);
                        human.getPositionChangedOnServer().set(true);

                    });
                }

                hc.setTargetDestructible(currentTarget);
                previousTargetPosition = null;
                context.getBlackboard().put(MudBeetleContext.INTERRUPT_WALKING, false);

                setRandomPathfindCooldown(hc);
                return NodeCompletionStatus.SUCCESS;
            }

            queuePathfindingToTargetLastPos(hc);
            hc.setTargetDestructible(null);
            context.getBlackboard().put(MudBeetleContext.INTERRUPT_WALKING, false);

//        System.out.println("NOT FOUND");
            return NodeCompletionStatus.FAILURE;
        }

        private void queuePathfindingToTargetLastPos(MudBeetleContext hc) {
            if (hc.getCurrentTarget() != null) {
                hc.resetPath();
                var lastSeenTargetPos = hc.getCurrentTarget().getNode().getWorldTranslation();
                hc.setLastKnownTargetPosition(lastSeenTargetPos);
            }
        }

        private CollisionResults checkRayBetweenTarget(Destructible caster, Destructible target, float distance) {
            CollisionResults results = new CollisionResults();

            Main.getInstance().enqueue(() -> { // if not performed by the main thread
                // the rendering glitches out if its not fast enough 
                var eyesHeight = 1f;
                var rayStart = caster.getNode().getWorldTranslation().add(0, eyesHeight, 0);
                var rayDestination = target.getNode().getWorldTranslation().add(0, eyesHeight, 0).subtractLocal(rayStart).normalizeLocal();
                Ray ray = new Ray(rayStart, rayDestination);
                ray.setLimit(distance);
                ClientGameAppState.getInstance().getMapNode().collideWith(ray, results);
            }
            );
            return results;
        }

        private void setRandomPathfindCooldown(MudBeetleContext hc) {
            // -0.1f because lower bound has to be smaller than upperbound
            var pathfindCdLowerBound = Math.max(hc.getFindRandomPathTimer() - 0.1f, 2);
            var pathfindCd = r.nextFloat(
                    pathfindCdLowerBound,
                    hc.getFindRandomPathCooldown()
            );
            hc.setFindRandomPathTimer(pathfindCd);
        }

        public static boolean isInCone(float coneAngle, Destructible thisMob, Destructible mob) {  // coneAngle goes both ways, so 45 is 90* cone
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
    }

    public static class IsPathfindingNeededAction extends NodeAction {

        @Override
        public NodeCompletionStatus execute(Context context) {
//        System.out.println("path try");
            var hc = (MudBeetleContext) context;
            hc.setFindRandomPathTimer(hc.getFindRandomPathTimer() - ServerMain.getTimePerFrame());
            if (shouldPathfind(hc)) {
//            System.out.println("try pathfind");
                hc.setFindRandomPathTimer(hc.getFindRandomPathCooldown());
                return NodeCompletionStatus.SUCCESS;
            }
            return NodeCompletionStatus.FAILURE;
        }

        private boolean shouldPathfind(MudBeetleContext hc) {

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

        @Override
        public NodeCompletionStatus execute(Context context) {
            var hc = (MudBeetleContext) context;
            var beetle = hc.getMudBeetle();

            if (beetle.isDead()) {
                return STOP_TREE_EXECUTION;
            }

            var target = hc.getCurrentTarget();
            var moveVec = target.getNode().getWorldTranslation()
                    .subtract(beetle.getNode().getWorldTranslation())
                    .normalizeLocal();
            moveVec.multLocal(beetle.getCachedSpeed() * ServerMain.getTimePerFrame());

            Main.getInstance().enqueue(() -> {
                beetle.moveServer(moveVec);
            });

            if (beetle.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation()) <= TEST_WEAPON_RANGE) {
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
            var hc = (MudBeetleContext) context;
            if (hc.getPathfindingFuture() == null) {
//            System.out.println("pahfind");
                decidePathfinding(hc);
            } else if (isPathReady(hc)) {
                startFollowingPath(hc);
                return SUCCESS;
            }
            return RUNNING;
        }

        public void pathfindClose(MudBeetleContext context) {
            Callable<List<Vector3f>> c = () -> {
                var mobPos = context.getMudBeetle().getNode().getWorldTranslation();
                int x = new Random().nextInt(28) - 14;
                int z = new Random().nextInt(28) - 14;

                var path = AStar.findPath(mobPos, mobPos.add(x, 0, z));
//                return path;
                return setRandomDestinationFromCellCenter(path);
//return null;
            };

            Future pf = ServerMain.pathfindingExecutor.submit(c);

            context.setPathfindingFuture(pf);

        }

        public void pathfindToLastKnownTargetLocation(MudBeetleContext context) {
            Callable<List<Vector3f>> c = () -> {
                var mobPos = context.getMudBeetle().getNode().getWorldTranslation();
                var destination = context.getLastKnownTargetPosition();
                var path = AStar.findPath(mobPos, destination);
                context.setLastKnownTargetPosition(null);
                return path;
            };

            Future pf = ServerMain.pathfindingExecutor.submit(c);

            context.setPathfindingFuture(pf);

        }

        private boolean isPathReady(MudBeetleContext hc) {
            return hc.getPathfindingFuture() != null && hc.getPathfindingFuture().isDone();
        }

        private void startFollowingPath(MudBeetleContext hc) {
            try {
                var future = hc.getPathfindingFuture();
                var futurePath = future.get();
                List<Vector3f> path = null;
                if (futurePath != null) {
                    path = (List<Vector3f>) future.get();
                }
                hc.setCurrentPath(path);
                hc.setPathfindingFuture(null);

            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(WalkAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void decidePathfinding(MudBeetleContext hc) {
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
            var hc = (MudBeetleContext) context;
            hc.resetPath();
            return SUCCESS;
        }

    }

    public static class WalkAction extends NodeAction {

        private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();

        @Override
        public NodeCompletionStatus execute(Context context) {

            var hc = (MudBeetleContext) context;
            var beetle = hc.getMudBeetle();

            if (beetle.isDead()) {
                return STOP_TREE_EXECUTION;
            }

            var interrupt = (boolean) context.getBlackboard().get(MudBeetleContext.INTERRUPT_WALKING);
            if (interrupt) {
                resetPath(hc);
                return NodeCompletionStatus.FAILURE;
            }
            var isFollowingFinished = followPath(beetle, hc);

            if (isFollowingFinished) {
//            resetPath(hc);
                return NodeCompletionStatus.SUCCESS;
            } else if (hc.getCurrentPath() == null) {
                return NodeCompletionStatus.FAILURE;
            } else {
                return NodeCompletionStatus.RUNNING;
            }

        }

        private void resetPath(MudBeetleContext c) {
            c.setCurrentNodeIndex(0);
            c.setCurrentPath(null);
            c.setPathfindingFuture(null);
        }

        private boolean followPath(MudBeetle human, MudBeetleContext hc) {
            if (hc.getCurrentPath() == null) {
                return false;
            }
            boolean achievedDestination = false;
            var currentNodeIndex = hc.getCurrentNodeIndex();
            Vector3f currentNode = hc.getCurrentPath().get(currentNodeIndex).mult(BLOCK_SIZE);
            var distance = human.getNode().getWorldTranslation().distance(currentNode);

            if (!hc.hasLookedAtCurrentNode()) {
                hc.setLookedAtCurrentNode(true);

                Main.getInstance().enqueue(() -> {
                    human.getNode().lookAt(currentNode, Vector3f.UNIT_Y);
                    human.getPositionChangedOnServer().set(true);
                });
            }

            if (distance < human.getCachedSpeed() * ServerMain.getTimePerFrame() * 2 || canMarkCurrentNodeAsVisited(hc, currentNodeIndex, currentNode, human.getNode().getWorldTranslation())) {
                hc.setCurrentNodeIndex(++currentNodeIndex);
                hc.setLookedAtCurrentNode(false);
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

        private boolean canMarkCurrentNodeAsVisited(MudBeetleContext hc, int currentNodeIndex, Vector3f currentNode, Vector3f mobPos) {

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

        private boolean canPushTEST(HumanMob otherMob) {
            var mobPos1 = otherMob.getNode().getWorldTranslation().add(0.5f, 0, 0.5f);
            var mobPos2 = otherMob.getNode().getWorldTranslation().add(-0.5f, 0, -0.5f);
            var mobPos3 = otherMob.getNode().getWorldTranslation().add(-0.5f, 0, 0.5f);
            var mobPos4 = otherMob.getNode().getWorldTranslation().add(0.5f, 0, -0.5f);

            var poses = List.of(mobPos1, mobPos2, mobPos3, mobPos4);

            for (var mobPos : poses) {
                int mobPosX = (int) (Math.floor(mobPos.x / BLOCK_SIZE));
                int mobPosY = (int) (Math.floor(mobPos.y / BLOCK_SIZE));
                int mobPosZ = (int) (Math.floor(mobPos.z / BLOCK_SIZE));
                if (ServerMain.getInstance().getMap().isPositionNotEmpty(mobPosX,mobPosY,mobPosZ)) {
                    return false;
                }

            }

            return true;
        }

    }

}
