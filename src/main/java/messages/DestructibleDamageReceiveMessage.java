/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@Serializable
@Getter
public class DestructibleDamageReceiveMessage extends AbstractMessage {
    
    protected int targetId;
    protected float damage;

    public DestructibleDamageReceiveMessage() {
    }

    public DestructibleDamageReceiveMessage(int targetId,float damage) {
        this.targetId = targetId;
        this.damage = damage;
    }


}
