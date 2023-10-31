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
public class ThrownGrenadeExplodedMessage extends AbstractMessage {

    @Getter
    @Setter
    private int id;
    private float posX;
    private float posY;
    private float posZ;

    public ThrownGrenadeExplodedMessage() {
    }

    // if sent  client -> server it contains the data about the grenade item the thrown grenade originates from
    // if sent  server -> client it contains the data about the new thrown grenade and its position
    public ThrownGrenadeExplodedMessage(int id, Vector3f pos) {
        this.id = id;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();
    }

    public Vector3f getPos() {
        return new Vector3f(posX, posY, posZ);
    }


}
