/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statusEffects;

import game.entities.Destructible;
import game.entities.mobs.Mob;
import game.map.collision.WorldGrid;
import messages.DestructibleDamageReceiveMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class TimedSlowEffect extends TemporaryEffect {

    private final Mob target;
    private final float speedBefore;

    public TimedSlowEffect(String name, EffectProcType procType, int maxTicks, Mob target,float slowStrength) {
        super(name, procType, maxTicks);
        this.target = target;
        speedBefore = target.getSpeed();
        unique = true;
        source = EffectSource.SLOW;
        target.setSpeed(calculateSpeedAfterSlow(slowStrength));
        
    }

    @Override
    public boolean updateServer() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean updateClient() {
        ticks++;
        if (ticks > maxTicks) {
            boolean readyToRemove = true;
            target.setSpeed(speedBefore);
            return readyToRemove;
        }
        return false;
    }
    
    private float calculateSpeedAfterSlow(float slowStrength){
    return target.getSpeed()-( target.getSpeed() * 0.01f*slowStrength);
    }

}
