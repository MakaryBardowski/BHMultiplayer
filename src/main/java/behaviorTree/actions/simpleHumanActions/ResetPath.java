package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;

/**
 *
 * @author 48793
 */
public class ResetPath extends NodeAction{
    
        @Override
        public NodeCompletionStatus execute(Context context) {
            ((SimpleHumanMobContext) context).resetPath();
            return SUCCESS;
        }

}
