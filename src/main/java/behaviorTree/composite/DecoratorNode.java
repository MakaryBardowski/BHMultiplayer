package behaviorTree.composite;

import behaviorTree.BehaviorNode;
import behaviorTree.NodeCompletionStatus;

import java.util.List;
import java.util.Map;

public class DecoratorNode extends CompositeNode{
    public DecoratorNode(List<BehaviorNode> children) {
        super(children);
    }

    @Override
    public NodeCompletionStatus execute(behaviorTree.context.Context context) {
        return null;
    }
}
