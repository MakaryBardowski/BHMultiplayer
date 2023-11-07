/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.messageListeners;

import client.Main;
import messages.MobRotUpdateMessage;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import server.ServerMain;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Collidable;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.Player;
import game.map.collision.MovementCollisionUtils;
import game.map.collision.WorldGrid;
import java.util.ArrayList;
import messages.DestructibleDamageReceiveMessage;
import messages.HitscanTrailMessage;
import messages.InstantEntityPosCorrectionMessage;
import messages.PlayerPosUpdateRequest;
import messages.TwoWayMessage;

/**
 *
 * @author 48793
 */
@Serializable
public class ServerMessageListener implements MessageListener<HostedConnection> {

    private ServerMain serverApp;
    private static final Main mainApp = Main.getInstance();

    public ServerMessageListener() {
    }

    public ServerMessageListener(ServerMain s) {
        this.serverApp = s;
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
        if (msg instanceof TwoWayMessage tm) {
            tm.handleServer(serverApp);
        }

        if (msg instanceof MobRotUpdateMessage nmsg) {
            if (mobExists(nmsg.getId())) {
                serverApp.getMobs().get(nmsg.getId()).getNode().setLocalRotation(nmsg.getRot());
            }

        } else if (msg instanceof PlayerPosUpdateRequest nmsg) {
            if (mobExists(nmsg.getId())) {
                Player p = (Player) serverApp.getMobs().get(nmsg.getId());

                var allCollidables = serverApp.getGrid().getNearbyAtPosition(p, nmsg.getPos());
                var solid = new ArrayList<Collidable>();
                var passable = new ArrayList<Collidable>();
                MovementCollisionUtils.sortByPassability(allCollidables, solid, passable);

                if (MovementCollisionUtils.isValidMobMovement(p, nmsg.getPos(), serverApp.getGrid(), solid)) {
                    WorldGrid grid = serverApp.getGrid();
                    grid.remove(p);
                    p.getNode().setLocalTranslation(nmsg.getPos());
                    grid.insert(p);

                    MovementCollisionUtils.checkPassableCollisions(p, grid, passable);
                } else {
                    InstantEntityPosCorrectionMessage corrMsg = new InstantEntityPosCorrectionMessage(p, p.getNode().getWorldTranslation());
                    corrMsg.setReliable(true);
                    serverApp.getServer().broadcast(Filters.in(getHostedConnectionByPlayer(p)), corrMsg);
                }

            }

        } else if (msg instanceof DestructibleDamageReceiveMessage hmsg) {
            InteractiveEntity i = serverApp.getMobs().get(hmsg.getTargetId());
            if (i != null) { // if the mob doesnt exist, it means the 
                // info was sent from a lagged user - dont forward it to others
                Destructible d = ((Destructible) i);
                handleDestructibleDamageReceive(d, hmsg, serverApp);

            }
        } else if (msg instanceof HitscanTrailMessage hmsg) {

            HostedConnection hc = serverApp.getServer().getConnection(hmsg.getClientId());
            serverApp.getServer().broadcast(Filters.notIn(hc), hmsg);

        } 
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

    public static void checkAndManageDestructibleDeath(Destructible d, ServerMain serverApp) {
        if (d.getHealth() <= 0) {
            WorldGrid grid = serverApp.getGrid();
            grid.remove(d);
            serverApp.getMobs().remove(d.getId());
            d.onDeathServer();
        }
    }

    public static void applyDestructibleDamageAndNotifyClients(Destructible d, DestructibleDamageReceiveMessage hmsg, ServerMain serverApp) {
        d.setHealth(d.getHealth() - d.calculateDamage(hmsg.getDamage()));
        hmsg.setReliable(true);
        serverApp.getServer().broadcast(hmsg);
    }

    public static void handleDestructibleDamageReceive(Destructible d, DestructibleDamageReceiveMessage hmsg, ServerMain serverApp) {
        applyDestructibleDamageAndNotifyClients(d, hmsg, serverApp);
        checkAndManageDestructibleDeath(d, serverApp);
    }



    public static void enqueueExecutionServer(Runnable runnable) {
        mainApp.enqueue(runnable);
    }



}
