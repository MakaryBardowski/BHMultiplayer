package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.FAILURE;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import java.util.Random;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class ShouldLookIntoRandomDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (SimpleHumanMobContext) context;
        hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionTimer() - ServerMain.getTimePerFrame());
        if (hc.getCurrentPath() == null && hc.getCurrentTarget() == null && hc.getLookInRandomDirectionTimer() <= 0) {
            return SUCCESS;
        }
        return FAILURE;
    }
}
