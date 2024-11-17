package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import client.Main;
import com.jme3.math.Vector3f;
import game.entities.mobs.HumanMob;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class WalkAction extends NodeAction {

    private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();
    private Vector3f currentNode;

    @Override
    public NodeCompletionStatus execute(Context context) {

        var hc = (SimpleHumanMobContext) context;
        var beetle = hc.getHumanMob();

        if (beetle.isDead()) {
            return STOP_TREE_EXECUTION;
        }

        var interrupt = (boolean) context.getBlackboard().get(SimpleHumanMobContext.INTERRUPT_WALKING);
        if (interrupt) {
            return NodeCompletionStatus.FAILURE;
        }
        var isFollowingFinished = followPath(beetle, hc);

        if (isFollowingFinished) {
            return NodeCompletionStatus.SUCCESS;
        } else if (hc.getCurrentPath() == null) {
            return NodeCompletionStatus.FAILURE;
        } else {
            return NodeCompletionStatus.RUNNING;
        }

    }

    private boolean followPath(HumanMob human, SimpleHumanMobContext hc) {
        if (hc.getCurrentPath() == null) {
            return false;
        }
        boolean achievedDestination = false;
        var currentNodeIndex = hc.getCurrentNodeIndex();
        currentNode = hc.getCurrentPath().get(currentNodeIndex).mult(BLOCK_SIZE);
        var distance = human.getNode().getWorldTranslation().distance(currentNode);

        hc.setDesiredLookDirection(currentNode.subtract(human.getNode().getWorldTranslation()));

        if (distance < human.getCachedSpeed() * ServerMain.getTimePerFrame() * 2 || canMarkCurrentNodeAsVisited(hc, currentNodeIndex, currentNode, human.getNode().getWorldTranslation())) {
            hc.setCurrentNodeIndex(++currentNodeIndex);
            if (currentNodeIndex >= hc.getCurrentPath().size()) {
                achievedDestination = true;
                return achievedDestination;
            }
        } else {

            var moveVec = currentNode.subtract(human.getNode().getWorldTranslation()).normalizeLocal();
            moveVec.multLocal(human.getCachedSpeed() * ServerMain.getTimePerFrame());
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

    private boolean canMarkCurrentNodeAsVisited(SimpleHumanMobContext hc, int currentNodeIndex, Vector3f currentNode, Vector3f mobPos) {

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

}
