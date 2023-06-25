package com.Networking.Server;

import Game.Mobs.Mob;
import Game.Mobs.Player;
import Messages.MessageListeners.ClientMessageListener;
import Messages.MessageListeners.ServerMessageListener;
import Messages.MobUpdatePosRotMessage;
import Messages.PlayerJoined;

import com.jme3.app.SimpleApplication;

import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.scene.Geometry;
import com.jme3.system.JmeContext;
import com.Networking.NetworkingInitialization;
import com.jme3.network.AbstractMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class ServerMain extends SimpleApplication implements ConnectionListener, MessageListener<HostedConnection> {

    private static String serverIP = "localhost";
    private Server server;
    private HashMap<Integer, Mob> mobs = new HashMap<>();
    private float tickTimer;
    private float timePerTick = 0.033f;
//    private float timePerTick = 3f;

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
        if (tickTimer >= timePerTick) {
            tickTimer = 0;
            mobs.entrySet().stream().forEach(
                    x -> {
                        server.broadcast(new MobUpdatePosRotMessage(x.getValue().getId(), x.getValue().getNode().getWorldTranslation().getX(), x.getValue().getNode().getWorldTranslation().getY(), x.getValue().getNode().getWorldTranslation().getZ(), x.getValue().getNode().getLocalRotation()));
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
       
        Mob newPlayer = registerPlayer(hc.getId());
        PlayerJoined messageToNewPlayer = new PlayerJoined(newPlayer.getId(), newPlayer.getNode().getWorldTranslation().getX(), newPlayer.getNode().getWorldTranslation().getY(), newPlayer.getNode().getWorldTranslation().getZ());
        server.broadcast(messageToNewPlayer);
        mobs.values().stream().toList().forEach(x -> {
                    if (x != newPlayer) {
                        PlayerJoined msg = new PlayerJoined(x.getId(), x.getNode().getWorldTranslation().getX(), x.getNode().getWorldTranslation().getY(), x.getNode().getWorldTranslation().getZ());
                        server.broadcast(msg);
                    }
                }
        );
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
        Player player = Player.spawnPlayer(id, assetManager, rootNode);
        this.mobs.put(id, player);
        return player;
    }

}
