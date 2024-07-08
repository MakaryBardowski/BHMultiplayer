/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class SystemHealthUpdateMessage extends TwoWayMessage {

    protected int id;
    protected float health;

    public SystemHealthUpdateMessage() {
    }

    public SystemHealthUpdateMessage(int id, float health) {
        this.id = id;
        this.health = health;
    }

    public int getId() {
        return id;
    }

    public float getHealth() {
        return health;
    }

    @Override
    public void handleServer(ServerMain server) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        updateEntityHealth(this);
    }

    private void updateEntityHealth(SystemHealthUpdateMessage hmsg) {
        if (entityExistsLocallyClient(hmsg.getId())) {
            getDestructibleByIdClient(hmsg.getId()).setHealth(hmsg.getHealth());
        }
    }

}
