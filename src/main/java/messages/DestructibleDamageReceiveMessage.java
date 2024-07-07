/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
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
public class DestructibleDamageReceiveMessage extends EntityUpdateMessage {

    protected float damage;

    public DestructibleDamageReceiveMessage() {
    }

    public DestructibleDamageReceiveMessage(int targetId, float damage) {
        super(targetId);
        this.damage = damage;
    }

    @Override
    public void handleServer(ServerMain server) {
        InteractiveEntity i = getEntityByIdServer(id);
        if (i != null) { // if the mob doesnt exist, it means the 
            // info was sent from a lagged user - dont forward it to others
            Destructible d = ((Destructible) i);
            handleDestructibleDamageReceive(d, ServerMain.getInstance());

        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        enqueueExecution(() -> {
            if (entityExistsLocallyClient(id)) {

                Destructible d = (Destructible) getEntityByIdClient(id);
                d.receiveDamage(damage);
                
                
                if (d.getHealth() <= 0) {
                    ClientGameAppState.getInstance().getMobs().remove(d.getId());
                }
                
            }
        }
        );
    }

    private void checkAndManageDestructibleDeath(Destructible d, ServerMain serverApp) {
        if (d.getHealth() <= 0) {
            WorldGrid grid = serverApp.getGrid();
            grid.remove(d);
            serverApp.getLevelManagerMobs().remove(d.getId());
            d.onDeathServer();
        }
    }

    private void applyDestructibleDamageAndNotifyClients(Destructible d, ServerMain serverApp) {
        d.setHealth(d.getHealth() - d.calculateDamage(damage));
        this.setReliable(true);
        serverApp.getServer().broadcast(this);
    }

    public void handleDestructibleDamageReceive(Destructible d, ServerMain serverApp) {
        applyDestructibleDamageAndNotifyClients(d, serverApp);
        checkAndManageDestructibleDeath(d, serverApp);
    }

}
