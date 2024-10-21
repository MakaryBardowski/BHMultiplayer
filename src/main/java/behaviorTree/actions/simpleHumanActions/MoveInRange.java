package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import client.Main;
import com.jme3.math.Vector3f;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class MoveInRange extends NodeAction {

    private float TEST_WEAPON_RANGE = 1.5f-0.1f;
    private Vector3f moveVec;
    private Destructible target;
    private HumanMob human;
    private SimpleHumanMobContext hc;
    private Vector3f direction;

    @Override
    public NodeCompletionStatus execute(Context context) {
        hc = (SimpleHumanMobContext) context;
        human = hc.getHumanMob();

        if (human.isDead()) {
            return STOP_TREE_EXECUTION;
        }

        target = hc.getCurrentTarget();
        direction = target.getNode().getWorldTranslation()
                .subtract(human.getNode().getWorldTranslation());
        var moveDistance = human.getCachedSpeed() * ServerMain.getTimePerFrame();

        if (moveDistance > direction.length() - TEST_WEAPON_RANGE) {
            moveDistance = direction.length() - TEST_WEAPON_RANGE;
            //
                moveDistance = moveDistance < 0 ? 0 : moveDistance;
            //
        }

        moveVec = direction.normalizeLocal();
        moveVec.multLocal(moveDistance);

        Main.getInstance().enqueue(() -> {
            human.moveServer(moveVec);
        });

        if (human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation()) <= TEST_WEAPON_RANGE) {
            return NodeCompletionStatus.SUCCESS;
        }

        return NodeCompletionStatus.FAILURE;
    }

}
