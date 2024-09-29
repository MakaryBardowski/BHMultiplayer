/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

import com.jme3.math.Vector3f;
import data.DamageReceiveData;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
public class DamageReceivedEvent extends GameEvent {
    @Getter
    @Setter
    private DamageReceiveData damageData;
    public DamageReceivedEvent(DamageReceiveData damageData){
        this.damageData = damageData;
    }
}
