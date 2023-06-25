/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author 48793
 */
@Serializable
public class MobHealthUpdateMessage extends AbstractMessage {

    protected int id;
    protected float health;

    public MobHealthUpdateMessage() {
    }

    public MobHealthUpdateMessage(int id,float health) {
        this.id = id;
        this.health = health;
    }

    public int getId() {
        return id;
    }
    
    public float getHealth(){
    return health;
    }

}
