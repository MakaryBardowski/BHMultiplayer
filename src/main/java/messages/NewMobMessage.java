/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import game.entities.mobs.MobType;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.Mob;

/**
 *
 * @author 48793
 */
@Serializable
public class NewMobMessage extends AbstractMessage {

    private int id;
    private float health;
    private float x;
    private float y;
    private float z;

    public NewMobMessage() {
    }

    public NewMobMessage(Mob mob, Vector3f pos) {
        this.id = mob.getId();
        this.health = mob.getHealth();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public int getId() {
        return id;
    }
    
    public float getHealth(){
    return health;
    }

    public Vector3f getPos(){
    return new Vector3f(x,y,z);
    }

}