/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author 48793
 */
@Serializable
public class MobRotUpdateMessage extends MobUpdateMessage {

    private float w;
    private float x;
    private float y;
    private float z;

    public MobRotUpdateMessage() {
    }

    public MobRotUpdateMessage(int id, Quaternion rot) {
        super(id);
        this.w = rot.getW();
        this.x = rot.getX();
        this.y = rot.getY();
        this.z = rot.getZ();
    }

    public Quaternion getRot() {
        return new Quaternion(x,y,z,w);
    }

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id=" + id + " ,rot=" + getRot() + '}';
    }

}
