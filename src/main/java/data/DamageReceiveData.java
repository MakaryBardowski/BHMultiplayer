/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import com.jme3.math.Vector3f;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public class DamageReceiveData {
    private int victimId;
    private int attackerId;
    private float rawDamage;
    public DamageReceiveData(int victimId,int attackerId,float rawDamage){
        this.victimId = victimId;
        this.attackerId = attackerId;
        this.rawDamage = rawDamage;
    }
}
