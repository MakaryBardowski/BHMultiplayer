package behaviorTree.actions.humanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;

public class ResetPathAction extends NodeAction {



    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        hc.resetPath();
        return SUCCESS;
    }



}
