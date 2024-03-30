package behaviorTree.actions.humanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import client.ClientGameAppState;
import client.Main;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import game.entities.Collidable;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import messages.DestructibleDamageReceiveMessage;
import server.ServerMain;

public class CheckForTargetAction extends NodeAction {

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
