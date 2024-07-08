/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.Getter;
import lombok.Setter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public abstract class EntityUpdateMessage extends TwoWayMessage {
@Setter
    @Getter
    protected int id;

    public EntityUpdateMessage() {
    }

    public EntityUpdateMessage(int id) {
        this.id = id;
    }

    

}
