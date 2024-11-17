package server;

import client.Main;
import com.jme3.app.Application;
import game.entities.mobs.Mob;
import messages.messageListeners.ServerMessageListener;
import messages.MobPosRotUpdateMessage;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;

import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import networkingUtils.NetworkingInitialization;
import com.jme3.renderer.RenderManager;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.StatusEffectContainer;
import game.entities.grenades.ThrownGrenade;
import game.entities.mobs.AiSteerable;
import game.entities.mobs.player.Player;
import game.items.Item;
import game.map.collision.WorldGrid;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import messages.GrenadePosUpdateMessage;
import messages.lobby.GameStartedMessage;

public class ServerMain extends AbstractAppState implements ConnectionListener {

    public static final byte MAX_PLAYERS = 4;

    // server variables
    @Getter
    private Server server;

    @Getter
    private final HashMap<Integer, HostedConnection> hostsByPlayerId = new HashMap<>(MAX_PLAYERS);

    @Getter
    private static ServerMain instance;
    private final float TIME_PER_TICK = 0.0156f;
    private float tickTimer;

    @Getter
    private final int BLOCK_SIZE = 3; //4

    @Getter
    private final int COLLISION_GRID_CELL_SIZE = 18; //16

    @Getter
    private final int MAP_SIZE = 39;

    @Getter
    public static ScheduledThreadPoolExecutor pathfindingExecutor = new ScheduledThreadPoolExecutor(1);

    @Getter
    private static float timePerFrame;
    private boolean serverTick = false;
    private boolean serverPaused = true;

    @Getter
    private final ServerGameManager currentGamemode;

    public ServerMain(AssetManager assetManager, RenderManager renderManager) {

        instance = this;
        currentGamemode = new ServerStoryGameManager();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        startServer();

    }

    @Override // the whole update method should be on another thread

    public void update(float tpf) {
        timePerFrame = tpf;

        if (!serverPaused) {
            tickTimer += timePerFrame;
            serverTick = tickTimer >= TIME_PER_TICK;
            for (var i : getLevelManagerMobs().values()) {
                if (i instanceof AiSteerable agent) {
                    agent.updateAi();
                }

                if (serverTick) {
                    if (i instanceof Destructible d) {
                        if (d instanceof StatusEffectContainer c) {
                            c.updateTemporaryEffectsServer();
                        }
                        if (d instanceof Mob x) {
                            if (x.getRotationChangedOnServer().get() == true || x.getPositionChangedOnServer().get() == true) {
                                server.broadcast(new MobPosRotUpdateMessage(x.getId(), x.getNode().getWorldTranslation(), x.getNode().getLocalRotation()));
                                x.getPositionChangedOnServer().set(false);
                                x.getRotationChangedOnServer().set(false);
                            }
                        }

                    } else if (i instanceof ThrownGrenade x) {
                        server.broadcast(new GrenadePosUpdateMessage(x.getId(), x.getNode().getWorldTranslation()));
                    }

                }

            }

            if (serverTick) {
                tickTimer = 0;
            }

        } else {
            System.out.println("server is PAUSED");
        }
    }

    public void startGame() {
        currentGamemode.startGame();

        // notify players about game starting
        server.getConnections().stream().forEach(hc -> currentGamemode.levelManager.addPlayerToGame(hc));

        var gameStartedMsg = new GameStartedMessage();
        server.broadcast(gameStartedMsg);
        serverPaused = false;
    }

    @Override
    public void connectionAdded(Server server, HostedConnection hc) {

    }

    @Override
    public void connectionRemoved(Server server, HostedConnection hc) {
    }

    private void startServer() {
        try {
            server = Network.createServer(NetworkingInitialization.PORT);
            server.addConnectionListener(this);
            server.addMessageListener(new ServerMessageListener(this));
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getAndIncreaseNextEntityId() {
        return currentGamemode.getLevelManager().getAndIncreaseNextEntityId();
    }

    public int getNextEntityId() {
        return currentGamemode.getLevelManager().getNextEntityId();
    }

    public static void removeEntityByIdServer(int id) {
        instance.getLevelManagerMobs().remove(id);
    }

    public static void removeItemFromMobEquipmentServer(int mobId, int itemId) {
        var mob = (Mob) instance.getLevelManagerMobs().get(mobId);
        var item = (Item) instance.getLevelManagerMobs().get(itemId);
        var mobEquipment = mob.getEquipment();
        mobEquipment.removeItem(item);
    }

    public ConcurrentHashMap<Integer, InteractiveEntity> getLevelManagerMobs() {
        return currentGamemode.getLevelManager().getMobs();
    }

    public WorldGrid getGrid() {
        return currentGamemode.getLevelManager().getGrid();
    }

    public byte[][][] getMap() {
        return currentGamemode.getLevelManager().getMap();
    }

    public boolean containsEntityWithId(int id) {
        return currentGamemode.getLevelManager().getMobs().get(id) != null;
    }

}
