package behaviorTree;

import java.util.Map;
import java.util.function.Function;

public class LeafNode extends BehaviorNode{
    private NodeAction nodeAction;
    public LeafNode(NodeAction nodeAction){
    this.nodeAction = nodeAction;
    }

    @Override
    public NodeCompletionStatus execute(behaviorTree.context.Context context) {
        return nodeAction.execute(context);
    }

    public NodeAction getNodeAction() {
        return nodeAction;
    }

    @Override
    public String toString() {
        return "LeafNode<" + "nodeAction=" + nodeAction + '>';
    }
    
    
}
