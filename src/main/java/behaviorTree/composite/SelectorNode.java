package behaviorTree.composite;

import behaviorTree.BehaviorNode;
import behaviorTree.NodeCompletionStatus;

import java.util.List;
import java.util.Map;

public class SelectorNode extends CompositeNode {

    private String name;

    public SelectorNode(List<BehaviorNode> children) {
        super(children);
    }

    public SelectorNode(String name, List<BehaviorNode> children) {
        super(children);
        this.name = name;
    }

    @Override
    public NodeCompletionStatus execute(behaviorTree.context.Context context) {
//                    System.out.println("children "+children);

        for (int i = lastExecutedRunningChildIndex == -1 ? 0 : lastExecutedRunningChildIndex; i < children.size(); i++) {
//            System.out.println("i "+i);
            var child = children.get(i);
//            System.out.println("executing child "+child );
            var childStatus = child.execute(context);
            if (childStatus == NodeCompletionStatus.SUCCESS || childStatus == NodeCompletionStatus.STOP_TREE_EXECUTION) {
                lastExecutedRunningChildIndex = -1;
                return childStatus;
            } else if (childStatus == NodeCompletionStatus.RUNNING) {
                lastExecutedRunningChildIndex = i;
                return NodeCompletionStatus.RUNNING;
            }
        }
        lastExecutedRunningChildIndex = -1;
        return NodeCompletionStatus.FAILURE;
    }

    @Override
    public String toString() {
        return "SelectorNode{" + "name=" + name + '}';
    }

}
