/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statusEffects;

/**
 *
 * @author 48793
 */
public class TemporaryEffect extends StatusEffect {

    protected int ticks = 0;
    protected int maxTicks;

    public TemporaryEffect(String name, EffectProcType procType,int maxTicks) {
        super(name, procType);
        this.maxTicks = maxTicks;
    }

}
