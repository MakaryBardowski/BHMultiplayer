package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class IsPathfindingNeeded extends NodeAction {

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (SimpleHumanMobContext) context;
        hc.setFindRandomPathTimer(hc.getFindRandomPathTimer() - ServerMain.getTimePerFrame());
        if (shouldPathfind(hc)) {
            hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionCooldown());
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
