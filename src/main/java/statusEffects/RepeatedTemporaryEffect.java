/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statusEffects;

/**
 *
 * @author 48793
 */
public abstract class RepeatedTemporaryEffect extends TemporaryEffect{
    protected int ticksPerProc;
    
    public RepeatedTemporaryEffect(String name, EffectProcType procType,int maxTicks, int ticksPerProc) {
        super(name, procType,maxTicks);
        this.ticksPerProc = ticksPerProc;
    }
}
