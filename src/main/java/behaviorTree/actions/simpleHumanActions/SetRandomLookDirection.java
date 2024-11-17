package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.Random;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class SetRandomLookDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (SimpleHumanMobContext) context;
        hc.putLookInRandomDirectionOnCooldown();
        hc.setDesiredLookDirection(new Vector3f(
                random.nextFloat(-1, 1), 0, random.nextFloat(-1, 1)
        )
        );

        return SUCCESS;
    }
}
