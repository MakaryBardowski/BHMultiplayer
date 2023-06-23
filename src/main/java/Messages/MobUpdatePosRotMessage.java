/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author 48793
 */
@Serializable
public class MobUpdatePosRotMessage extends MobUpdateMessage {

    private float x;
    private float y;
    private float z;

    private Quaternion rot;

    public MobUpdatePosRotMessage() {
    }

    public MobUpdatePosRotMessage(int id, float x, float y, float z, Quaternion rot) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
        this.rot = rot;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id="+id + " x=" + x + ", y=" + y + ", z=" + z + ", rot=" + rot + '}';
    }

}
