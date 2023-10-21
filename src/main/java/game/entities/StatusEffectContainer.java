/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashSet;
import lombok.Getter;
import statusEffects.EffectProcType;
import statusEffects.StatusEffect;
import statusEffects.TemporaryEffect;

/**
 *
 * @author 48793
 */
@Getter
public abstract class StatusEffectContainer extends Destructible {

    protected HashSet<StatusEffect> onHitDealEffects = new HashSet<>(10);
    protected HashSet<StatusEffect> onHitReceiveEffects = new HashSet<>(10);
    protected HashSet<StatusEffect> temporaryEffects = new HashSet<>(10);

    public StatusEffectContainer(int id, String name, Node node) {
        super(id, name, node);
    }

    public void addEffect(StatusEffect effect) {
        switch (effect.getProcType()) {
            case PERIODICAL:
                if (!effect.isUnique() || (effect.isUnique() && temporaryEffectsNotContain(effect))) {
                    temporaryEffects.add(effect);
                }
            case ON_HIT:
                onHitDealEffects.add(effect);
            case ON_DAMAGED:
                onHitReceiveEffects.add(effect);
            default:
                break;
        }
    }

    public void updateTemporaryEffectsServer() {
        var it = temporaryEffects.iterator();
        while (it.hasNext()) {
            var e = it.next();
            if (e.updateServer()) {
                it.remove();
            }
        }
    }

    public void updateTemporaryEffectsClient() {
        var it = temporaryEffects.iterator();
        while (it.hasNext()) {
            var e = it.next();
            if (e.updateClient()) {
                it.remove();
            }
        }
    }

    private boolean temporaryEffectsNotContain(StatusEffect effect) {
        for (StatusEffect e : temporaryEffects) {
            if (e.getEffectId() == effect.getEffectId()) {
                return false;
            }
        }

        return true;
    }
}
