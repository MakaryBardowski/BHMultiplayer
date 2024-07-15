/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import client.Main;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Filters;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.Player;
import game.items.consumable.Medpack;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class DeleteEntityMessage extends EntityUpdateMessage {

    public DeleteEntityMessage() {
    }

    public DeleteEntityMessage(int id) {
        super(id);
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server) {
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = getEntityByIdClient(id);
            if (entity != null) {
                entity.destroyClient();
            }
    }

}
