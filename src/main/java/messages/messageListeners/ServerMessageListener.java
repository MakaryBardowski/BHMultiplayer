/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.messageListeners;

import messages.SystemHealthUpdateMessage;
import messages.MobUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import client.ClientGameAppState;
import server.ServerMain;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Collidable;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.items.Item;
import game.map.collision.MovementCollisionUtils;
import game.map.collision.WorldGrid;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import messages.DestructibleDamageReceiveMessage;
import messages.HitscanTrailMessage;
import messages.InstantEntityPosCorrectionMessage;
import messages.PlayerPosUpdateRequest;
import messages.items.MobItemInteractionMessage;
import messages.items.MobItemInteractionMessage.ItemInteractionType;

/**
 *
 * @author 48793
 */
@Serializable
public class ServerMessageListener implements MessageListener<HostedConnection> {

    private ServerMain serverApp;

    public ServerMessageListener() {
    }

    ;
    public ServerMessageListener(ServerMain s) {
        this.serverApp = s;
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
        if (msg instanceof MobRotUpdateMessage nmsg) {
            if (mobExists(nmsg.getId())) {
                serverApp.getMobs().get(nmsg.getId()).getNode().setLocalRotation(nmsg.getRot());
            }

        } else if (msg instanceof PlayerPosUpdateRequest nmsg) {
            if (mobExists(nmsg.getId())) {
                Player p = (Player) serverApp.getMobs().get(nmsg.getId());

//                if (p.getId() != ClientGameAppState.getInstance().getPlayer().getId()) {

                    if (MovementCollisionUtils.isValidMobMovement(p, nmsg.getPos(), serverApp.getGrid())) {
                        WorldGrid grid = serverApp.getGrid();
                        grid.remove(p);
                        p.getNode().setLocalTranslation(nmsg.getPos());
                        grid.insert(p);
                    } else {
                        InstantEntityPosCorrectionMessage corrMsg = new InstantEntityPosCorrectionMessage(p, p.getNode().getWorldTranslation());
                        corrMsg.setReliable(true);
                        serverApp.getServer().broadcast(Filters.in(getHostedConnectionByPlayer(p)), corrMsg);
                    }

//                } else {
//                    WorldGrid grid = serverApp.getGrid();
//                    grid.remove(p);
//                    p.getNode().setLocalTranslation(nmsg.getPos());
//                    grid.insert(p);
//
//                }

            }

        } else if (msg instanceof DestructibleDamageReceiveMessage hmsg) {
            InteractiveEntity i = serverApp.getMobs().get(hmsg.getTargetId());
            if (i != null) { // if the mob doesnt exist, it means the 
                // info was sent from a lagged user - dont forward it to others
                Destructible d = ((Destructible) i);

                d.setHealth(d.getHealth() - d.calculateDamage(hmsg.getDamage()));
                hmsg.setReliable(true);
                serverApp.getServer().broadcast(hmsg);

                if (d.getHealth() <= 0) {
                    WorldGrid grid = serverApp.getGrid();
                    grid.remove(d);
                    serverApp.getMobs().remove(d.getId());
                }
            }
        } else if (msg instanceof HitscanTrailMessage hmsg) {

            HostedConnection hc = serverApp.getServer().getConnection(hmsg.getClientId());
            serverApp.getServer().broadcast(Filters.notIn(hc), hmsg);

        } else if (msg instanceof MobItemInteractionMessage imsg) {
            handleItemInteraction(imsg);
        }
    }

    public void handleItemInteraction(MobItemInteractionMessage imsg) {
        if (imsg.getInteractionType() == ItemInteractionType.PICK_UP) {
//            getMobById(imsg.getMobId()).addToEquipment(getItemById(imsg.getItemId()));
            serverApp.getServer().broadcast(imsg);
        } else if (imsg.getInteractionType() == ItemInteractionType.EQUIP) {
            getMobById(imsg.getMobId()).equipServer(getItemById(imsg.getItemId()));
            serverApp.getServer().broadcast(imsg);
        } else if (imsg.getInteractionType() == ItemInteractionType.UNEQUIP) {
//            getMobById(imsg.getMobId()).unequip(getItemById(imsg.getItemId()));
            serverApp.getServer().broadcast(imsg);
        } else if (imsg.getInteractionType() == ItemInteractionType.DROP) {

        }
    }

    private Mob getMobById(int id) {
        return ((Mob) serverApp.getMobs().get(id));
    }

    private Item getItemById(int id) {
        return ((Item) serverApp.getMobs().get(id));
    }

    private Destructible getDestructibleById(int id) {
        return ((Destructible) serverApp.getMobs().get(id));
    }

    private boolean mobExists(int id) {
        return serverApp.getMobs().keySet().contains(id);
    }

    private HostedConnection getHostedConnectionByPlayer(Player p) {
        return serverApp.getConnectionsById().get(p.getId());
    }

}
