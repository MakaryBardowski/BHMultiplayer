package behaviorTree.composite;

import behaviorTree.BehaviorNode;
import behaviorTree.NodeCompletionStatus;

import java.util.List;
import java.util.Map;

public class ParallelNode extends CompositeNode{
    public ParallelNode(List<BehaviorNode> children) {
        super(children);
    }

    @Override
    public NodeCompletionStatus execute(behaviorTree.context.Context context) {
        int successCount = 0;
        int failureCount = 0;
        boolean anyChildRunning = false;
        for (BehaviorNode child : children) {
            NodeCompletionStatus childStatus = child.execute(context);
            
            if(childStatus == NodeCompletionStatus.STOP_TREE_EXECUTION)
                return NodeCompletionStatus.STOP_TREE_EXECUTION;
            
            if (childStatus == NodeCompletionStatus.FAILURE) {
                failureCount++;
            } else if (childStatus == NodeCompletionStatus.SUCCESS) {
                successCount++;
            }  else if (childStatus == NodeCompletionStatus.RUNNING) {
                anyChildRunning = true;
            }
            // You may also handle RUNNING status if needed
        }

        // Decide the overall status of the parallel node
        if(anyChildRunning){
            return NodeCompletionStatus.RUNNING;
        } else if (successCount > failureCount) {
            return NodeCompletionStatus.SUCCESS;
        } else {
            return NodeCompletionStatus.FAILURE;
        } 
    }
}
