package behaviorTree;

import behaviorTree.context.Context;
import java.util.Map;

public abstract class NodeAction {
   public abstract NodeCompletionStatus execute(Context context);
}
