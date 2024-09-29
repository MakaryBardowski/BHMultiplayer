package behaviorTree.composite;

import behaviorTree.BehaviorNode;
import behaviorTree.LeafNode;
import behaviorTree.NodeCompletionStatus;

import java.util.List;
import java.util.Map;

public class SequenceNode extends CompositeNode {
    private String name;
    public SequenceNode(List<BehaviorNode> children) {
        super(children);
    }
    public SequenceNode(String name,List<BehaviorNode> children) {
        super(children);
        this.name = name;
    }
    @Override
    public NodeCompletionStatus execute(behaviorTree.context.Context context) {

        for (int i = lastExecutedRunningChildIndex == -1 ? 0 : lastExecutedRunningChildIndex; i < children.size(); i++) {
            var child = children.get(i);
            var childStatus = child.execute(context);
            if (childStatus == NodeCompletionStatus.FAILURE || childStatus == NodeCompletionStatus.STOP_TREE_EXECUTION) {
                lastExecutedRunningChildIndex = -1;
//        debug("FAIL SEQ");

                return childStatus;
            }
            if (childStatus == NodeCompletionStatus.RUNNING) {
                lastExecutedRunningChildIndex = i;
//        debug("RUNNING SEQ");

                return NodeCompletionStatus.RUNNING;
            }
        }
        lastExecutedRunningChildIndex = -1;
//        debug("SUCCESS SEQ");
        return NodeCompletionStatus.SUCCESS;
    }

    @Override
    public String toString() {
        return "SequenceNode{" + "name=" + name + '}';
    }

   
}
