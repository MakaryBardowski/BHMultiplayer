/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.scene.Node;
import java.util.ArrayList;
import statusEffects.EffectProcType;
import statusEffects.StatusEffect;

/**
 *
 * @author 48793
 */
public abstract class StatusEffectContainer extends Destructible {
    
    protected ArrayList<StatusEffect> onHitDealEffects = new ArrayList<>(10);
    protected ArrayList<StatusEffect> onHitReceiveEffects = new ArrayList<>(10);
    protected ArrayList<StatusEffect> periodicalEffects = new ArrayList<>(10);

    public StatusEffectContainer(int id, String name, Node node) {
        super(id, name, node);
    }
    
    public void addEffect(StatusEffect effect){
        switch (effect.getProcType()) {
            case PERIODICAL:
                periodicalEffects.add(effect);
            case ON_HIT:
                onHitDealEffects.add(effect);
            case ON_DAMAGED:
                onHitReceiveEffects.add(effect);
            default:
                break;
        }
    }
}
