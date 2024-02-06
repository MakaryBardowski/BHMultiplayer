/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package behaviorTree.actions.humanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.FAILURE;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import com.jme3.math.Vector3f;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import pathfinding.AStar;

/**
 *
 * @author 48793
 */
public class IsPathfindingNeededAction extends NodeAction {

    float tpf = 0.016f;

    @Override
    public NodeCompletionStatus execute(Context context) {
//        System.out.println("path try");
        var hc = (MudBeetleContext) context;
        hc.setFindRandomPathTimer(hc.getFindRandomPathTimer() - tpf);
        if (shouldPathfind(hc)) {
//            System.out.println("try pathfind");
            hc.setFindRandomPathTimer(hc.getFindRandomPathCooldown());
            return SUCCESS;
        }
        return FAILURE;
    }

    private boolean shouldPathfind(MudBeetleContext hc) {
        
        return (hc.getCurrentPath() == null 
                && hc.getFindRandomPathTimer() < 0 
                && hc.getCurrentTarget() == null)
                
                || hc.getLastKnownTargetPosition() != null /*
                always pathfind.
                */
                ;
    }

}
