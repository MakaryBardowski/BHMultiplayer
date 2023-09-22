/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.Chest;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@Serializable
public class NewChestMessage extends AbstractMessage {

    @Getter
    private int id;
    @Getter
    private float health;
    private float x;
    private float y;
    private float z;

    public NewChestMessage() {
    }

    public NewChestMessage(Chest chest, Vector3f pos) {
        this.id = chest.getId();
        this.health = chest.getHealth();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

}
