/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.bulkMessages;

import messages.*;
import client.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.DestructibleUtils;
import game.entities.factories.MobSpawnType;
import game.entities.mobs.Mob;
import game.map.blocks.VoxelLighting;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class BulkMessage extends TwoWayMessage {
    private List<TwoWayMessage> messages;
    public BulkMessage(List<TwoWayMessage> messages) {
        this.messages = messages;
        setReliable(true);
    }

    public BulkMessage() {
    }

    @Override
    public void handleServer(ServerMain server) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        System.out.println("received bulk msg.");
        for(var msg : messages){
        msg.handleClient(client);
        }
    }


}
