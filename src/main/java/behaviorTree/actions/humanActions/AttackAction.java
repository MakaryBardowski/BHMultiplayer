package behaviorTree.actions.humanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import client.Main;
import com.jme3.math.Vector3f;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;

import java.util.Map;
import java.util.Random;
import messages.DestructibleDamageReceiveMessage;
import server.ServerMain;


public class AttackAction extends NodeAction {

    private float currentGunCooldown;
    private float TEST_GUN_COOLDOWN = 1;
    private float TEST_GUN_RANGE = 2;
    private float TEST_DAMAGE = 2; 

    @Override
    public NodeCompletionStatus execute(Context context) {
        var beetle = (MudBeetle) context.getBlackboard().get(MudBeetleContext.STEERED_MUD_BEETLE);
        var target = context.getBlackboard().get(MudBeetleContext.TARGET_DESTRUCTIBLE);
//if(target != null)
//    System.out.println("target " + target + new Random().nextInt());
        currentGunCooldown -= ServerMain.getTimePerFrame();
        if (currentGunCooldown < 0) {
            if (target != null) {
                
                
                var destructibleTarget = (Destructible) target;

                var distance = beetle.getNode().getWorldTranslation().distance(destructibleTarget.getNode().getWorldTranslation());
                if (distance > TEST_GUN_RANGE) {
                    return NodeCompletionStatus.FAILURE;
                }

                Main.getInstance().enqueue(() -> {
                    var emsg = new DestructibleDamageReceiveMessage(destructibleTarget.getId(), TEST_DAMAGE);
                    emsg.handleDestructibleDamageReceive(destructibleTarget, ServerMain.getInstance());
//                    var bullet = SceneUtils.createBullet(destructibleTarget.getNode().getWorldTranslation().clone());
//                    Main.debugNode.attachChild(bullet);
//                    
//                    bullet.move(beetle.getNode().getWorldTranslation());
                });
//                System.out.println("attacking!" + new Random().nextInt(2));
                currentGunCooldown = TEST_GUN_COOLDOWN;

            }
        }
//        System.out.println("Attacking!");
        return NodeCompletionStatus.SUCCESS;
    }
}
