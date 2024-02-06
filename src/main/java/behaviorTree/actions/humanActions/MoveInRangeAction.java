package behaviorTree.actions.humanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.FAILURE;
import static behaviorTree.NodeCompletionStatus.RUNNING;
import static behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import client.Main;
import server.ServerMain;

public class MoveInRangeAction extends NodeAction {

    float tpf;

    private float TEST_WEAPON_RANGE = 3;

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        var beetle = hc.getMudBeetle();

        if (beetle.isDead()) {
            return STOP_TREE_EXECUTION;
        }
        
        tpf = ServerMain.getTimePerFrame();
        var target = hc.getCurrentTarget();
        var moveVec = target.getNode().getWorldTranslation()
                .subtract(beetle.getNode().getWorldTranslation())
                .normalizeLocal();
        moveVec.multLocal(beetle.getCachedSpeed() * tpf);

        Main.getInstance().enqueue(() -> {
            beetle.moveServer(moveVec);
        });

        if (beetle.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation()) <= TEST_WEAPON_RANGE) {
            return SUCCESS;
        }

        return FAILURE;
    }

}
