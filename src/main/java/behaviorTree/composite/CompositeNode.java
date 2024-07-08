package behaviorTree.composite;

import behaviorTree.BehaviorNode;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeNode extends BehaviorNode {
    protected int lastExecutedRunningChildIndex = -1;

    protected List<BehaviorNode> children = new ArrayList<>();

    public CompositeNode(List<BehaviorNode> children){
        this.children = children;
    }
}
