package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.FAILURE;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import client.Main;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import messages.DestructibleDamageReceiveMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class Attack extends NodeAction {

    private long lastAttackedTimestamp = System.currentTimeMillis();
    private float WEAPON_COOLDOWN_SECONDS = 2.5f;
    private float TEST_GUN_COOLDOWN = WEAPON_COOLDOWN_SECONDS*1000;
    private float TEST_GUN_RANGE = 2;
    private float TEST_DAMAGE = 3.75f;
    private HumanMob human;
    private Destructible target;

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (SimpleHumanMobContext) context;
        human = (HumanMob) context.getBlackboard().get(SimpleHumanMobContext.STEERED_HUMAN);
        target = (Destructible) context.getBlackboard().get(SimpleHumanMobContext.TARGET_DESTRUCTIBLE);
//        System.out.println("current gun cooldown before for "+human+" is "+currentGunCooldown);

//        System.out.println("current gun cooldown for "+human+" is "+currentGunCooldown);
        if (hc.getCurrentUpdateTimestamp() - lastAttackedTimestamp < TEST_GUN_COOLDOWN) {
            return FAILURE;
        }

        if (target == null) {
            return FAILURE;
        }

        var distance = human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation());
        if (distance > TEST_GUN_RANGE) {
            return NodeCompletionStatus.FAILURE;
        }
        
        System.out.println(human+" dealing damage "+ (hc.getCurrentUpdateTimestamp() - lastAttackedTimestamp));
        Main.getInstance().enqueue(() -> {
            var emsg = new DestructibleDamageReceiveMessage(target.getId(), human.getId(), TEST_DAMAGE);
            emsg.applyDestructibleDamageAndNotifyClients(target, ServerMain.getInstance());
        });
        lastAttackedTimestamp = hc.getCurrentUpdateTimestamp();
        System.out.println("timestamp now is "+hc.getCurrentUpdateTimestamp());
        System.out.println("cooldown is now "+(hc.getCurrentUpdateTimestamp() - lastAttackedTimestamp ));
        System.out.println("this "+this);
//        System.out.println("Attacking!");
        return NodeCompletionStatus.SUCCESS;
    }
}
