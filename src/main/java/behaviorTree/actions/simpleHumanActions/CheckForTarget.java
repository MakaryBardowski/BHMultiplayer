package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import client.ClientGameAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import game.entities.mobs.player.Player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class CheckForTarget extends NodeAction {

    private Random r = new Random();
    private List<Destructible> targetsSeen = new ArrayList<>();
    private List<Destructible> targetsHeard = new ArrayList<>();
    private float TEST_SIGHT_RANGE = 20;
    private float CONE_WIDTH_DEG = 75;

    private float shortVisionRadiusSquared = 4; // vision radius in a circle ("hearing")

    private Spatial debugArrow;

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = ((SimpleHumanMobContext) context);

        var human = (HumanMob) context.getBlackboard().get(SimpleHumanMobContext.STEERED_HUMAN);

        /*  if cannot SWITCH to new target 
            && has target 
            && sees target -> return success
         */
        if (!hc.canAcquireNewTarget() && hc.getCurrentTarget() == null) {
            hc.allowAcquireNewTarget();
        }

        /* else check for closest target
            because we allow to search for target when:
            cannot get new target && has no target -> search for new target immediately
         */
        if (!targetsSeen.isEmpty()) {
            targetsSeen.clear();
        }
        if (!targetsHeard.isEmpty()) {
            targetsHeard.clear();
        }

        var mobsClose = ServerMain.getInstance().getGrid().getNearbyInRadius(human.getNode().getWorldTranslation(), TEST_SIGHT_RANGE);
        for (var mob : mobsClose) {
            if (mob instanceof Destructible d && (mob instanceof MudBeetle || mob instanceof Player)) {
                if ((humanSeesMob(human, d))) {
                    targetsSeen.add(d);
                } else if ((humanHearsMob(human, d))) {
                    targetsHeard.add(d);
                }
            }
        }

        Comparator<Destructible> distanceComparator = (a, b) -> Float.compare(human.getNode().getWorldTranslation().distanceSquared(a.getNode().getWorldTranslation()), human.getNode().getWorldTranslation().distanceSquared(b.getNode().getWorldTranslation()));
        Destructible newTarget = findTargetFromEnemies(human, targetsSeen, distanceComparator);

        if (newTarget == null) {
            var heardTarget = findTargetFromEnemies(human, targetsHeard, distanceComparator);
            if (heardTarget != null) {
                hc.setDesiredLookDirection(
                        heardTarget.getNode().getWorldTranslation().subtract(
                                human.getNode().getWorldTranslation()
                        )
                );
            }
            queuePathfindingToTargetLastPos(hc);
            hc.setTargetDestructible(null);
            context.getBlackboard().put(SimpleHumanMobContext.INTERRUPT_WALKING, false);

            return NodeCompletionStatus.FAILURE;
        }

        hc.setTargetDestructible(newTarget);

        context.getBlackboard().put(SimpleHumanMobContext.INTERRUPT_WALKING, false);

        hc.putLookInRandomDirectionOnCooldown();
        hc.putPathfindingOnCooldown();
        hc.setDesiredLookDirection(
                hc.getCurrentTarget().getNode().getWorldTranslation().subtract(
                        human.getNode().getWorldTranslation()
                )
        );
        return NodeCompletionStatus.SUCCESS;
    }

    private void queuePathfindingToTargetLastPos(SimpleHumanMobContext hc) {
        if (hc.getCurrentTarget() != null && hc.getCurrentTarget().getHealth() > 0) {
            hc.resetPath();
            var lastSeenTargetPos = hc.getCurrentTarget().getNode().getWorldTranslation();
            hc.setLastKnownTargetPosition(lastSeenTargetPos);
        }
    }

    private CollisionResults checkRayBetweenTarget(Destructible caster, Destructible target, float distance) {
        CollisionResults results = new CollisionResults();

        var eyesHeight = 2f;
        var rayStart = caster.getNode().getWorldTranslation().add(0, eyesHeight, 0);
        //                                                                     + 0.1f so it doesnt hit ground accidentaly, can be buggy
        var rayDestination = target.getNode().getWorldTranslation().add(0, eyesHeight + 0.1f, 0).subtractLocal(rayStart).normalizeLocal();
        Ray ray = new Ray(rayStart, rayDestination);
        ray.setLimit(distance);
        ClientGameAppState.getInstance().getMapNode().collideWith(ray, results);
        return results;
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
        return isInCone(CONE_WIDTH_DEG, human, mob);
    }

    private boolean humanHearsMob(HumanMob human, Destructible mob) {
        return mob.getNode().getWorldTranslation().distanceSquared(human.getNode().getWorldTranslation()) <= shortVisionRadiusSquared;
    }

    private Destructible findTargetFromEnemies(HumanMob human, List<Destructible> enemies, Comparator<Destructible> distanceComparator) {
        enemies.sort(distanceComparator);

        for (var viableTarget : enemies) {
            boolean viable = true;
            // offset from the ground
            var distance = human.getNode().getWorldTranslation().distance(viableTarget.getNode().getWorldTranslation().add(0, 0.1f, 0));

            for (var result : checkRayBetweenTarget(human, viableTarget, distance)) {
                var name = result.getGeometry().getName();
//                    System.out.println(human + " eyesight collided with " + name);

                if (name != viableTarget.getNode().getName() && name != human.getNode().getName() && result.getDistance() <= distance) {
                    viable = false;
                    break;
                }
            }

            if (viable) {
                return viableTarget;
            }
        }
        return null;
    }
}
