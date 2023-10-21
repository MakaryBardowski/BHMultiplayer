package client;

import game.cameraAndInput.PlayerCameraControlAppState;
import game.map.Map;
import game.map.MapGenerator;
import game.map.MapType;
import game.entities.mobs.Mob;
import game.entities.factories.PlayerFactory;
import game.entities.mobs.Player;
import messages.messageListeners.ClientMessageListener;
import static client.ClientSynchronizationUtils.interpolateMobPosition;
import static client.ClientSynchronizationUtils.interpolateMobRotation;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import networkingUtils.NetworkingInitialization;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.light.AmbientLight;
import com.jme3.network.ClientStateListener;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import de.lessvoid.nifty.Nifty;
import game.entities.Collidable;
import game.entities.InteractiveEntity;
import game.entities.mobs.HumanMob;
import game.map.collision.WorldGrid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class ClientGameAppState extends AbstractAppState implements ClientStateListener {

    @Getter
    private static ClientGameAppState instance;

    @Getter
    private final int BLOCK_SIZE = 4;

    @Getter
    private final int COLLISION_GRID_CELL_SIZE = 16;

    @Getter
    private final int CHUNK_SIZE = 16;

    @Getter
    private final int MAP_SIZE = 39;

    @Getter
    private final Node rootNode = new Node("ROOT NODE");

    @Getter
    private final Node worldNode = new Node("WORLD NODE");

    @Getter
    private final Node mapNode = new Node("MAP NODE");

    @Getter
    private final Node debugNode = new Node("DEBUG NODE");

    @Getter
    private final Node destructibleNode = new Node("DESTRUCTIBLE NODE");

    @Getter
    private final Node entityNode = new Node("ENTITY NODE");

    @Getter
    private final Node pickableNode = new Node("PICKABLE NODE");

    private final SimpleApplication app;

    @Getter
    private final AssetManager assetManager;

    @Getter
    private final InputManager inputManager;

    private final ConcurrentLinkedQueue<AbstractMessage> messageQueue = new ConcurrentLinkedQueue<>();

    @Getter
    private final ConcurrentHashMap<Integer, InteractiveEntity> mobs = new ConcurrentHashMap<>();

    private final AppSettings applicationSettings;

    @Getter
    private Client client;

    @Getter
    @Setter
    private Player player;

    @Getter
    @Setter
    private ActionListener actionListener;

    @Getter
    @Setter
    private AnalogListener analogListener;

    @Getter
    private game.map.Map map;

    @Getter
    private WorldGrid grid;

    @Getter
    @Setter
    private Nifty nifty;

    @Getter
    private String serverIp;
    
    @Getter
    private boolean debug;

    public ClientGameAppState(Main app, String serverIp) {
        instance = this;
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.applicationSettings = app.getAppSettings();
        app.getRootNode().attachChild(rootNode);
        this.serverIp = serverIp;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {        // rejestrujemy klasy serializowalne (nie musicie rozumiec, architektura klient-serwer)
              Picture crosshair = new Picture("crosshair");
        crosshair.setImage(assetManager, "Textures/GUI/crosshair.png", true);
        crosshair.setWidth(applicationSettings.getHeight() * 0.04f);
        crosshair.setHeight(applicationSettings.getHeight() * 0.04f); //0.04f
        crosshair.setPosition((applicationSettings.getWidth() / 2) - applicationSettings.getHeight() * 0.04f / 2, applicationSettings.getHeight() / 2 - applicationSettings.getHeight() * 0.04f / 2);
        Main.getInstance().getGuiNode().attachChild(crosshair);
//        NetworkingInitialization.initializeSerializables();
        worldNode.attachChild(debugNode);
        worldNode.attachChild(entityNode);
        pickableNode.attachChild(destructibleNode);
        entityNode.attachChild(pickableNode);
        worldNode.attachChild(mapNode);
        rootNode.attachChild(worldNode);

        stateManager.attach(new PlayerCameraControlAppState(this));

        try {
            client = Network.connectToServer(serverIp, NetworkingInitialization.PORT);
            client.addClientStateListener(this);
            client.start();

        } catch (IOException ex) {
            Logger.getLogger(ClientGameAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        client.addMessageListener(new ClientMessageListener(this));
        app.getViewPort().setBackgroundColor(ColorRGBA.Cyan);

        grid = new WorldGrid(MAP_SIZE, BLOCK_SIZE, COLLISION_GRID_CELL_SIZE);

        MapGenerator mg = new MapGenerator();
        map = mg.generateMap(MapType.BOSS, BLOCK_SIZE, CHUNK_SIZE, MAP_SIZE, assetManager, mapNode);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.7f));
        worldNode.addLight(al);

    }

    @Override
    public void update(float tpf) {

        if (player != null) {
            player.move(tpf, this);
            player.updateTemporaryEffectsClient();

        }

        mobs.values().forEach(x -> {
            if (x instanceof Mob m && x != player) {
//                System.out.println(m.getNode().getWorldTranslation());
                if (!m.getNode().getWorldTranslation().equals(m.getServerLocation())) {
                    grid.remove(m);
                    interpolateMobPosition(m, tpf);
                    grid.insert(m);
                }
                interpolateMobRotation(m, tpf);
            }
        }
        );

//
//        
//        System.out.println("\n\n\n");
//                mobs.values().stream()
//                    .filter(x -> x instanceof HumanMob)
//                    .map(x -> (HumanMob) x)
//                    .forEach((x) -> {
//                        System.err.println("----CLIENT----Player " + x.getId());
//                        System.err.println("equipped helmet =" + x.getHelmet() + " (default " + x.getDefaultHelmet());
//                        System.err.println("equipped vest =" + x.getVest() + " (default " + x.getDefaultVest());
//                        System.err.println("equipped gloves =" + x.getGloves() + " (default " + x.getDefaultGloves());
//                        System.err.println("equipped boots =" + x.getBoots() + " (default " + x.getDefaultBoots());
//                        System.err.println("equipment = "+Arrays.toString(x.getEquipment()));
//                    });
    }

    public ConcurrentLinkedQueue<AbstractMessage> getMessageQueue() {
        return messageQueue;
    }

    @Override
    public void clientConnected(Client client) {
    }

    @Override
    public void clientDisconnected(Client client, DisconnectInfo di) {

    }

    public Player registerPlayer(Integer id, boolean setAsPlayer) {
        Player p = new PlayerFactory(id, destructibleNode, getCamera(), setAsPlayer).createClientSide();
        this.mobs.put(id, p);
        return p;
    }

    public AppSettings getSettings() {
        return applicationSettings;
    }

    public Camera getCamera() {
        return app.getCamera();
    }

    public FlyByCamera getFlyCam() {
        return app.getFlyByCamera();
    }

    public AppStateManager getStateManager() {
        return app.getStateManager();
    }

    public <T extends InteractiveEntity> T registerEntity(T entity) {
        this.mobs.put(entity.getId(), entity);
        return entity;
    }

}
