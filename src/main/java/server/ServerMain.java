package server;

import game.mobs.Mob;
import game.mobs.factories.PlayerFactory;
import game.mobs.Player;
import messages.messageListeners.ServerMessageListener;
import messages.MobHealthUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.MobsInGameMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;

import com.jme3.app.SimpleApplication;

import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.system.JmeContext;
import networkingUtils.NetworkingInitialization;
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

        int i = 5;
        for (int row = 5; row < 6; row++) {
            for (int x = 5; x < 6; x++) {
                registerPlayer(i++).getNode().move(10 + row * 2, 0, 10 + (x - 5) * 4);
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //glowna petla serwera, 30dw111w tickow (wiadomosci od serwera) na sekunde, co 0.033s kazda
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

    @Override
    public void connectionAdded(Server server, HostedConnection hc) {
        mobs.entrySet().forEach(x -> {
            Mob mob = x.getValue();
            MobsInGameMessage m = new MobsInGameMessage(mob.getId(), mob.getNode().getWorldTranslation());
            server.broadcast(Filters.in(hc), m);
        });

        Mob newPlayer = registerPlayer(hc.getId());
        SetPlayerMessage messageToNewPlayer = new SetPlayerMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        server.broadcast(Filters.in(hc), messageToNewPlayer);

        PlayerJoinedMessage msg = new PlayerJoinedMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        server.broadcast(Filters.notEqualTo(hc), msg);

    }

    @Override
    public void connectionRemoved(Server server, HostedConnection hc) {
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
    }

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

    public Player registerPlayer(Integer id) {
        Player player = new PlayerFactory(id, assetManager, rootNode, renderManager).createServerSide();
        this.mobs.put(id, player);
        System.out.println("adding player " + id);
        return player;
    }

}
