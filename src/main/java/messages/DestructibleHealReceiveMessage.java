/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import client.Main;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.map.collision.WorldGrid;
import java.util.Random;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
@Getter
public class DestructibleHealReceiveMessage extends EntityUpdateMessage {

    protected float heal;

    public DestructibleHealReceiveMessage() {
    }

    public DestructibleHealReceiveMessage(int targetId, float heal) {
        super(targetId);
        this.heal = heal;
    }

    @Override
    public void handleServer(ServerMain server) {
        throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        enqueueExecution(() -> {
            if (entityExistsLocallyClient(id)) {

                Destructible d = (Destructible) getEntityByIdClient(id);
                d.receiveHeal(heal);
            }
        }
        );
    }

    private void applyDestructibleHealAndNotifyClients(Destructible d, ServerMain serverApp) {
        d.setHealth(d.getHealth() + heal);
        this.setReliable(true);
        serverApp.getServer().broadcast(this);
    }

    public void handleDestructibleHealReceive(Destructible d, ServerMain serverApp) {
        applyDestructibleHealAndNotifyClients(d, serverApp);
    }

}
