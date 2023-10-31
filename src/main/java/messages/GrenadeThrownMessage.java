/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Serializable
public class GrenadeThrownMessage extends AbstractMessage {
@Getter
@Setter
    private int id;
    private float posX;
    private float posY;
    private float posZ;

    private float dirX;
    private float dirY;
    private float dirZ;

    public GrenadeThrownMessage() {
    }

    // if sent  client -> server it contains the data about the grenade item the thrown grenade originates from
    // if sent  server -> client it contains the data about the new thrown grenade and its position
    public GrenadeThrownMessage(int id, Vector3f pos, Vector3f throwDirection) {
        this.id = id;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();

        this.dirX = throwDirection.getX();
        this.dirY = throwDirection.getY();
        this.dirZ = throwDirection.getZ();

    }


    public Vector3f getPos() {
        return new Vector3f(posX, posY, posZ);
    }

    public Vector3f getDirection() {
        return new Vector3f(dirX, dirY, dirZ);
    }
}
