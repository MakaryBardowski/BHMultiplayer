/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statusEffects;

import lombok.Getter;

/**
 *
 * @author 48793
 */
@Getter
public abstract class StatusEffect {
    
    protected String name;
    protected EffectProcType procType;

    public StatusEffect(String name,EffectProcType procType){
    this.name = name;
    this.procType = procType;
    }

    
}
