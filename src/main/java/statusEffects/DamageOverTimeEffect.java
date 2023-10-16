/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statusEffects;

/**
 *
 * @author 48793
 */
public class DamageOverTimeEffect extends RepeatedTemporaryEffect {

    public DamageOverTimeEffect(String name, EffectProcType procType,int maxTicks,int activationsOverTime) {
        super(name, procType,maxTicks,activationsOverTime);
    }

}
