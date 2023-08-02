package client;

import game.cameraAndInput.PlayerCameraControlAppState;
import game.map.Map;
import game.map.MapGenerator;
import game.map.MapType;
import game.mobs.Mob;
import game.mobs.factories.PlayerFactory;
import game.mobs.Player;
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
import de.lessvoid.nifty.Nifty;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final Node pickableNode = new Node("PICKABLE NODE");
    
    private final SimpleApplication app;
    
    @Getter
    private final AssetManager assetManager;
    
    @Getter
    private final InputManager inputManager;
    
    private final ConcurrentLinkedQueue<AbstractMessage> messageQueue = new ConcurrentLinkedQueue<>();
   
    @Getter
    private final HashMap<Integer, Mob> mobs = new HashMap<>();
    
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
    private Map map;

    @Getter
    @Setter
    private Nifty nifty;

    public ClientGameAppState(Main app) {
        instance = this;
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.applicationSettings = app.getAppSettings();
        app.getRootNode().attachChild(rootNode);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {        // rejestrujemy klasy serializowalne (nie musicie rozumiec, architektura klient-serwer)
        NetworkingInitialization.initializeSerializables();
        worldNode.attachChild(debugNode);
        worldNode.attachChild(pickableNode);
        pickableNode.attachChild(destructibleNode);
        worldNode.attachChild(mapNode);
        rootNode.attachChild(worldNode);

        stateManager.attach(new PlayerCameraControlAppState(this));

        try {
            client = Network.connectToServer("localhost", NetworkingInitialization.PORT);
            client.addClientStateListener(this);
            client.start();

        } catch (IOException ex) {
            Logger.getLogger(ClientGameAppState.class.getName()).log(Level.SEVERE, null, ex);
        }

        client.addMessageListener(new ClientMessageListener(this));
        app.getViewPort().setBackgroundColor(ColorRGBA.Cyan);

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
        }

        mobs.values().forEach(x -> {
            if (x != player) {
                interpolateMobPosition(x, tpf);
                interpolateMobRotation(x, tpf);
            }
        }
        );

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

}
