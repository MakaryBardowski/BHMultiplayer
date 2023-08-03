/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import game.entities.mobs.MobType;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author 48793
 */
@Serializable
public class PlayerJoinedMessage extends AbstractMessage {

    private int id;
private float x;
private float y;
private float z;

    public PlayerJoinedMessage() {
    }

    public PlayerJoinedMessage(int id, Vector3f pos) {
        this.id = id;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public int getId(){
    return id;
    }

    public Vector3f getPos(){
    return new Vector3f(x,y,z);
    }

}
