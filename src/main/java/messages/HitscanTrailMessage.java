/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@Serializable
public class HitscanTrailMessage extends EntityUpdateMessage {
    @Getter
    protected int clientId;
    protected float targetX;
    protected float targetY;
    protected float targetZ;
    
    public HitscanTrailMessage() {
    }

    public HitscanTrailMessage(int id, Vector3f shotPos, int hostedConnectionId) {
        super(id);
        this.targetX = shotPos.getX();
        this.targetY = shotPos.getY();
        this.targetZ = shotPos.getZ();
        this.clientId = hostedConnectionId;
    }

    public Vector3f getShotPos(){
    return new Vector3f(targetX,targetY,targetZ);
    }
    

}
