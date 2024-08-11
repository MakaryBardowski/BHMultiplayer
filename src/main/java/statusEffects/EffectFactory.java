/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statusEffects;

import game.entities.StatusEffectContainer;

/**
 *
 * @author 48793
 */
public class EffectFactory {

    public static DamageOverTimeEffect createBleedEffect(StatusEffectContainer target, float damage, int maxTicks, int procsEvery) {
        var dot = new DamageOverTimeEffect("Bleed", EffectProcType.PERIODICAL, maxTicks, procsEvery, damage);
        dot.setTarget(target);
        return dot;
    }

    public static HealOverTimeEffect createRegenerationEffect(StatusEffectContainer target, float heal, int procsEvery) {
        var dot = new HealOverTimeEffect("Bleed", EffectProcType.PERIODICAL, -1, procsEvery, heal);
        dot.setTarget(target);
        return dot;
    }

}
