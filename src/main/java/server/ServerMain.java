package server;

import game.entities.mobs.Mob;
import game.entities.mobFactories.PlayerFactory;
import game.entities.mobs.Player;
import messages.messageListeners.ServerMessageListener;
import messages.DestructibleHealthUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.MobsInGameMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;

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
import com.jme3.scene.Node;
import game.entities.Chest;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.ChestsInGameMessage;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class ServerMain extends SimpleApplication implements ConnectionListener, MessageListener<HostedConnection> {

    private static final String SERVER_IP = "localhost";
    private Server server;
    private final HashMap<Integer, InteractiveEntity> mobs = new HashMap<>();
    private float tickTimer;
    private final float TIME_PER_TICK = 0.033f;
    private int currentMaxId = 0;

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

        for (int row = 5; row < 8; row++) {
            for (int x = 5; x < 8; x++) {
                registerPlayer().getNode().move(10 + row * 2, 0, 10 + (x - 5) * 4);
            }
        }

        for (int i = 0; i < 10; i++) {
            registerRandomChest();
        }

    }

    @Override
    public void simpleUpdate(float tpf) {
        //glowna petla serwera, 30dw111w tickow (wiadomosci od serwera) na sekunde, co 0.033s kazda
        tickTimer += tpf;
        if (tickTimer >= TIME_PER_TICK) {
            tickTimer = 0;
            mobs.entrySet().stream().forEach(i -> {
                //to be seriously optimized
                if (i.getValue() instanceof Mob x) {
                    server.broadcast(new MobPosUpdateMessage(x.getId(), x.getNode().getWorldTranslation()));
                    server.broadcast(new MobRotUpdateMessage(x.getId(), x.getNode().getLocalRotation()));
                    server.broadcast(new DestructibleHealthUpdateMessage(x.getId(), x.getHealth()));
                }
            }
            );

        }

    }

    @Override
    public void connectionAdded(Server server, HostedConnection hc) {
        mobs.entrySet().forEach(x -> {
            if (x.getValue() instanceof Mob mob) {
                MobsInGameMessage m = new MobsInGameMessage(mob.getId(), mob.getNode().getWorldTranslation());
                m.setReliable(true);
                server.broadcast(Filters.in(hc), m);
            } else if (x.getValue() instanceof Chest chest) {
                ChestsInGameMessage m = new ChestsInGameMessage(chest.getId(), chest.getNode().getWorldTranslation());
                m.setReliable(true);
                server.broadcast(Filters.in(hc), m);
            }
        });

        Mob newPlayer = registerPlayer();
        SetPlayerMessage messageToNewPlayer = new SetPlayerMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        messageToNewPlayer.setReliable(true);
        server.broadcast(Filters.in(hc), messageToNewPlayer);

        PlayerJoinedMessage msg = new PlayerJoinedMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        msg.setReliable(true);
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

    public HashMap<Integer, InteractiveEntity> getMobs() {
        return mobs;
    }

    public Player registerPlayer() {
        Player player = new PlayerFactory(currentMaxId++, assetManager, rootNode, renderManager).createServerSide();
        this.mobs.put(player.getId(), player);
        System.out.println("adding player " + player.getId());
        return player;
    }

    public Chest registerRandomChest() {
        Random r = new Random();
        Vector3f offset = new Vector3f(r.nextFloat() * 30*4, 4, r.nextFloat() * 30*4);
        Chest chest = Chest.createRandomChestServer(currentMaxId++, rootNode, offset, assetManager);
        this.mobs.put(chest.getId(), chest);
        System.out.println("adding chest " + chest.getId());

        return chest;
    }

}
