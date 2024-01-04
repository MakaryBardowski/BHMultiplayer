/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@Serializable
public class PlayerJoinedMessage extends AbstractMessage {

    @Getter
    private int id;
    private float x;
    private float y;
    private float z;
    @Getter
    private String name;

    public PlayerJoinedMessage() {
    }

    public PlayerJoinedMessage(int id, Vector3f pos, String name) {
        this.id = id;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.name = name;
    }


    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

}
