package com.Networking.Server;

import Game.Mobs.Mob;
import Game.Mobs.Player;
import Messages.MessageListeners.ServerMessageListener;
import Messages.MobHealthUpdateMessage;
import Messages.MobPosUpdateMessage;
import Messages.MobRotUpdateMessage;
import Messages.MobsInGameMessage;
import Messages.PlayerJoinedMessage;
import Messages.SetPlayerMessage;

import com.jme3.app.SimpleApplication;

import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.system.JmeContext;
import com.Networking.NetworkingInitialization;
import com.jme3.network.Filters;
import com.jme3.renderer.RenderManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class ServerMain extends SimpleApplication implements ConnectionListener, MessageListener<HostedConnection> {

    private static final String SERVER_IP = "localhost";
    private Server server;
    private final HashMap<Integer, Mob> mobs = new HashMap<>();
    private float tickTimer;
    private final float TIME_PER_TICK = 0.033f;
//    private final float TIME_PER_TICK = 0.15f;

    public static void main(String[] args) {
        NetworkingInitialization.initializeSerializables();
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(NetworkingInitialization.PORT);
            server.addConnectionListener(this);
            server.addMessageListener(new ServerMessageListener(this));

            server.start();

        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void simpleUpdate(float tpf) {
        //glowna petla serwera, 30 tickow (wiadomosci od serwera) na sekunde, co 0.033s kazda
        tickTimer += tpf;
        if (tickTimer >= TIME_PER_TICK) {
            tickTimer = 0;
            mobs.entrySet().stream().forEach(x -> {
                //to be seriously optimized
                server.broadcast(new MobPosUpdateMessage(x.getValue().getId(), x.getValue().getNode().getWorldTranslation()));
                server.broadcast(new MobRotUpdateMessage(x.getValue().getId(), x.getValue().getNode().getLocalRotation()));

                server.broadcast(new MobHealthUpdateMessage(x.getValue().getId(), x.getValue().getHealth()));
            }
            );

        }

    }

    /* wykonywanie przy polaczeniu sie gracza do serwera
    tworzy nowego Player# , wysyla graczowi ktory sie dolaczyl ze bedzie sterowal Player#
    oraz informuje go o mobach (w tym innych graczach) ktore sa juz w grze.
    
    Innych graczy (polaczonych wczesniej) informuje o nowym graczu.
    
    informuje - przesyla pozycje, rotacje (pozniej informacje takie jak itemy, hp, etc.)
     */
    @Override
    public void connectionAdded(Server server, HostedConnection hc) {
        // inform the new player about all mobs (including players) currently in the game
        mobs.entrySet().forEach(x -> {
            Mob mob = x.getValue();
            MobsInGameMessage m = new MobsInGameMessage(mob.getId(), mob.getNode().getWorldTranslation().getX(), mob.getNode().getWorldTranslation().getY(), mob.getNode().getWorldTranslation().getZ());
            server.broadcast(Filters.in(hc), m);
            
        });

        Mob newPlayer = registerPlayer(hc.getId());
        // set newly created player as the player new client will control
        SetPlayerMessage messageToNewPlayer = new SetPlayerMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation().getX(), newPlayer.getNode().getWorldTranslation().getY(), newPlayer.getNode().getWorldTranslation().getZ());
        server.broadcast(Filters.in(hc), messageToNewPlayer);

        // inform all other clients about new player and his position
        PlayerJoinedMessage msg = new PlayerJoinedMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation().getX(), newPlayer.getNode().getWorldTranslation().getY(), newPlayer.getNode().getWorldTranslation().getZ());
        server.broadcast(Filters.notEqualTo(hc), msg);

    }

    @Override
    public void connectionRemoved(Server server, HostedConnection hc) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // przy zamknieciu serwera
    @Override
    public void destroy() {
        for (HostedConnection h : server.getConnections()) {
            h.close("server shutdown");
        }
        server.close();
        super.destroy();
    }

    public Server getServer() {
        return server;
    }

    public HashMap<Integer, Mob> getMobs() {
        return mobs;
    }

    // tworzy nowego gracza
    public Player registerPlayer(Integer id) {
        // the server doesnt need to render the player, hence cull hint = always
        Player player = Player.spawnPlayer(id, assetManager, rootNode,cam, renderManager,false);
        this.mobs.put(id, player);
        return player;
    }

}
