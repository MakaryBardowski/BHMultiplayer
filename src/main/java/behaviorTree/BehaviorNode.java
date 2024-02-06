package behaviorTree;

import behaviorTree.context.Context;

public abstract class BehaviorNode {
    public abstract NodeCompletionStatus execute(Context context);
}
