package behaviorTree.actions.humanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.FAILURE;
import static behaviorTree.NodeCompletionStatus.RUNNING;
import static behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import client.ClientGameAppState;
import client.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import game.entities.Movable;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import pathfinding.AStar;
import pathfinding.Node;
import server.ServerMain;

public class WalkAction extends NodeAction {

    private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();

    float tpf;

    @Override
    public NodeCompletionStatus execute(Context context) {

        var hc = (MudBeetleContext) context;
        var beetle = hc.getMudBeetle();
        
        if(beetle.isDead())
            return STOP_TREE_EXECUTION;

        tpf = ServerMain.getTimePerFrame();
        var interrupt = (boolean) context.getBlackboard().get(MudBeetleContext.INTERRUPT_WALKING);
        if (interrupt) {
            resetPath(hc);
            return FAILURE;
        }
        var isFollowingFinished = followPath(beetle, hc);

        if (isFollowingFinished) {
//            resetPath(hc);
            return SUCCESS;
        } else if (hc.getCurrentPath() == null) {
            return FAILURE;
        } else {
            return RUNNING;
        }

    }

    private void resetPath(MudBeetleContext c) {
        c.setCurrentNodeIndex(0);
        c.setCurrentPath(null);
        c.setPathfindingFuture(null);
    }

    private boolean followPath(MudBeetle human, MudBeetleContext hc) {
        if (hc.getCurrentPath() == null) {
            return false;
        }
        boolean achievedDestination = false;
        var currentNodeIndex = hc.getCurrentNodeIndex();
        Vector3f currentNode = hc.getCurrentPath().get(currentNodeIndex).mult(BLOCK_SIZE);
        var distance = human.getNode().getWorldTranslation().distance(currentNode);

        if (!hc.hasLookedAtCurrentNode()) {
            hc.setLookedAtCurrentNode(true);

            Main.getInstance().enqueue(() -> {
                human.getNode().lookAt(currentNode, Vector3f.UNIT_Y);
            });
        }

        if (distance < human.getCachedSpeed() * tpf * 2 || canMarkCurrentNodeAsVisited(hc, currentNodeIndex, currentNode, human.getNode().getWorldTranslation())) {
            hc.setCurrentNodeIndex(++currentNodeIndex);
            hc.setLookedAtCurrentNode(false);
            if (currentNodeIndex >= hc.getCurrentPath().size()) {
                achievedDestination = true;
                return achievedDestination;
            }
        } else {

            var moveVec = currentNode.subtract(human.getNode().getWorldTranslation()).normalizeLocal();
            moveVec.multLocal(human.getCachedSpeed() * tpf);
            Main.getInstance().enqueue(() -> {
//                // mobs pushing out of the way
//                float avoidanceForce = 5 * tpf;
//                for (HumanMob otherMob : Main.mobs) {
//                    if (otherMob != human) {
//                        float d = human.getNode().getWorldTranslation().distance(otherMob.getNode().getWorldTranslation());
//                        if (d < BLOCK_SIZE / 2) {
//                            // Assume Vector3f has necessary methods like add, scale, negate, normalize 
//                            Vector3f avoidanceVector = human.getNode().getWorldTranslation().add(otherMob.getNode().getWorldTranslation().negate()).normalize();
//                            // Assume Vector3f has necessary methods like add, scale 
//                            avoidanceVector.normalizeLocal().multLocal(avoidanceForce);
//                            moveVec.addLocal(avoidanceVector);
//
//                            if (canPushTEST(otherMob)) {
////                                otherMob.getSpatial().move(avoidanceVector.negate());
//                            }
//
//                        }
//                    }
//                }

                human.moveServer(moveVec);
            });
        }
        return false;

    }

    private boolean canMarkCurrentNodeAsVisited(MudBeetleContext hc, int currentNodeIndex, Vector3f currentNode, Vector3f mobPos) {

        int mobPosX = (int) (Math.floor(mobPos.x / BLOCK_SIZE));
        int mobPosY = (int) (Math.floor(mobPos.y / BLOCK_SIZE));
        int mobPosZ = (int) (Math.floor(mobPos.z / BLOCK_SIZE));
        int currentNodeX = (int) (Math.floor(currentNode.x / BLOCK_SIZE / 2));
        int currentNodeY = (int) (Math.floor(currentNode.y / BLOCK_SIZE / 2));
        int currentNodeZ = (int) (Math.floor(currentNode.z / BLOCK_SIZE / 2));

        return mobPosX == currentNodeX && mobPosY == currentNodeY && mobPosZ == currentNodeZ && currentNodeIndex < hc.getCurrentPath().size(); //-1;
        /* all nodes but the last can be flagged as achieved when entering the tile. only the last
        node has to be reached perfeclty
         */

    }

    private boolean canPushTEST(HumanMob otherMob) {
        var mobPos1 = otherMob.getNode().getWorldTranslation().add(0.5f, 0, 0.5f);
        var mobPos2 = otherMob.getNode().getWorldTranslation().add(-0.5f, 0, -0.5f);
        var mobPos3 = otherMob.getNode().getWorldTranslation().add(-0.5f, 0, 0.5f);
        var mobPos4 = otherMob.getNode().getWorldTranslation().add(0.5f, 0, -0.5f);

        var poses = List.of(mobPos1, mobPos2, mobPos3, mobPos4);

        for (var mobPos : poses) {
            int mobPosX = (int) (Math.floor(mobPos.x / BLOCK_SIZE));
            int mobPosY = (int) (Math.floor(mobPos.y / BLOCK_SIZE));
            int mobPosZ = (int) (Math.floor(mobPos.z / BLOCK_SIZE));
            if (ServerMain.getInstance().getMap()[mobPosX][mobPosY][mobPosZ] != 0) {
                return false;
            }

        }

        return true;
    }

}
