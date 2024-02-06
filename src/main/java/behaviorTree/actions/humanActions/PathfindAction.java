/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package behaviorTree.actions.humanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.RUNNING;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import com.jme3.math.Vector3f;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import pathfinding.AStar;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class PathfindAction extends NodeAction {
    private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();

    private final Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        if (hc.getPathfindingFuture() == null) {
//            System.out.println("pahfind");
            decidePathfinding(hc);
        } else if (isPathReady(hc)) {
            startFollowingPath(hc);
            return SUCCESS;
        }
        return RUNNING;
    }

    public void pathfindClose(MudBeetleContext context) {
            Callable<List<Vector3f>> c = () -> {
                var mobPos = context.getMudBeetle().getNode().getWorldTranslation();
                int x = new Random().nextInt(28) - 14;
                int z = new Random().nextInt(28) - 14;

                var path = AStar.findPath(mobPos, mobPos.add(x, 0, z));
//                return path;
                return setRandomDestinationFromCellCenter(path);
//return null;
            };

            Future pf = ServerMain.pathfindingExecutor.submit(c);

            context.setPathfindingFuture(pf);
        
    }

    public void pathfindToLastKnownTargetLocation(MudBeetleContext context) {
        Callable<List<Vector3f>> c = () -> {
            var mobPos = context.getMudBeetle().getNode().getWorldTranslation();
            var destination = context.getLastKnownTargetPosition();
            var path = AStar.findPath(mobPos, destination);
            context.setLastKnownTargetPosition(null);
            return path;
        };

        Future pf = ServerMain.pathfindingExecutor.submit(c);

        context.setPathfindingFuture(pf);

    }

    private boolean isPathReady(MudBeetleContext hc) {
        return hc.getPathfindingFuture() != null && hc.getPathfindingFuture().isDone();
    }

    private void startFollowingPath(MudBeetleContext hc) {
        try {
            var future = hc.getPathfindingFuture();
            var futurePath = future.get();
            List<Vector3f> path = null;
            if (futurePath != null) {
                 path = (List<Vector3f>) future.get();
            }
            hc.setCurrentPath(path);
            hc.setPathfindingFuture(null);

        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(WalkAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void decidePathfinding(MudBeetleContext hc) {
        if (hc.getLastKnownTargetPosition() == null) {
            pathfindClose(hc);
        }
        if (hc.getLastKnownTargetPosition() != null) {
            pathfindToLastKnownTargetLocation(hc);
//            System.out.println("pathing to last known...");
        }
    }

    private List<Vector3f> setRandomDestinationFromCellCenter(List<Vector3f> path) {
        if (path == null) {
            return null;
        }
        var cellCenter = path.get(path.size() - 1);
        var hitboxSize = 0.5f;
        cellCenter.addLocal(random.nextFloat(-(BLOCK_SIZE / 2 - hitboxSize), (BLOCK_SIZE / 2 - hitboxSize)), 0, random.nextFloat(-(BLOCK_SIZE / 2 - hitboxSize), (BLOCK_SIZE / 2 - hitboxSize)));
        return path;
    }

}
