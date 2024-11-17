/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statusEffects;

import game.entities.Destructible;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;
import messages.DestructibleDamageReceiveMessage;
import messages.DestructibleHealReceiveMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class HealOverTimeEffect extends RepeatedTemporaryEffect {

    @Getter
    @Setter
    private Destructible target;

    @Getter
    private final float heal;

    public HealOverTimeEffect(String name, EffectProcType procType, int maxTicks, int activationsOverTime, float heal) {
        super(name, procType, maxTicks, activationsOverTime);
        this.heal = heal;
        unique = true;
        source = EffectSource.REGENERATION;
    }

    @Override
    public boolean updateClient() {
        ticks++;
        if (ticks > maxTicks) {
            boolean readyToRemove = true;
            return readyToRemove;
        } else if (ticks % (ticksPerProc) == 0) {
//            System.out.println("The " + target + " bleeds!");
        }
        return false;
    }

    @Override
    public boolean updateServer() {
        ticks++;
        
        if (maxTicks > 0 && ticks > maxTicks) {
            boolean readyToRemove = true;
            return readyToRemove;
        } else if (ticks % (ticksPerProc) == 0) {
            if(maxTicks <= 0) // if infinite effect, loop
                ticks = 0;
            
            var healingDone = Math.min(heal, target.getMaxHealth() - target.getHealth());
            target.setHealth(target.getHealth() + healingDone);

            DestructibleHealReceiveMessage hpUpd = new DestructibleHealReceiveMessage(target.getId(), healingDone);
            hpUpd.setReliable(true);
            ServerMain.getInstance().getServer().broadcast(hpUpd);
        }
        return false;
    }

}
