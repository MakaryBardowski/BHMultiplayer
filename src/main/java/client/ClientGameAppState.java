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
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class ClientGameAppState extends AbstractAppState implements ClientStateListener {

    private static ClientGameAppState instance;
    private final int BLOCK_SIZE = 4;
    private final int CHUNK_SIZE = 16;
    private final int MAP_SIZE = 39;

    private final Node rootNode = new Node("ROOT NODE");
    private final Node worldNode = new Node("WORLD NODE");
    private final Node mapNode = new Node("MAP NODE");
    private final Node debugNode = new Node("DEBUG NODE");
    private final Node mobsNode = new Node("ENTITY NODE");
    private final Node pickableNode = new Node("PICKABLE NODE");

    private final SimpleApplication app;
    private final AssetManager assetManager;
    private final ConcurrentLinkedQueue<AbstractMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final HashMap<Integer, Mob> mobs = new HashMap<>();
    private final AppSettings applicationSettings;

    private Client client;
    private Player player;
    private ActionListener actionListener;
    private AnalogListener analogListener;
    private Map map;
    private Nifty nifty;

    public ClientGameAppState(Main app) {
        instance = this;
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.applicationSettings = app.getAppSettings();
        app.getRootNode().attachChild(rootNode);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {        // rejestrujemy klasy serializowalne (nie musicie rozumiec, architektura klient-serwer)
        NetworkingInitialization.initializeSerializables();
        worldNode.attachChild(debugNode);
        worldNode.attachChild(pickableNode);
        pickableNode.attachChild(mobsNode);
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

    public Client getClient() {
        return client;
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
        Player p = new PlayerFactory(id, mobsNode, getCamera(), setAsPlayer).createClientSide();
        this.mobs.put(id, p);
        return p;
    }


    public HashMap<Integer, Mob> getMobs() {
        return mobs;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public AppSettings getSettings() {
        return applicationSettings;
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public Node getWorldNode() {
        return worldNode;
    }

    public Node getMapNode() {
        return mapNode;
    }

    public Node getDebugNode() {
        return debugNode;
    }

    public Node getPickableNode() {
        return pickableNode;
    }

    public Map getMap() {
        return map;
    }

    public Camera getCamera() {
        return app.getCamera();
    }

    public FlyByCamera getFlyCam() {
        return app.getFlyByCamera();
    }

    public Nifty getNifty() {
        return nifty;
    }

    public void setNifty(Nifty nifty) {
        this.nifty = nifty;
    }

    public int getBLOCK_SIZE() {
        return BLOCK_SIZE;
    }

    public int getCHUNK_SIZE() {
        return CHUNK_SIZE;
    }

    public int getMAP_SIZE() {
        return MAP_SIZE;
    }

    public Node getMobsNode() {
        return mobsNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public AssetManager getAssetManager() {
        return app.getAssetManager();
    }

    public InputManager getInputManager() {
        return app.getInputManager();
    }

    public AppStateManager getStateManager() {
        return app.getStateManager();
    }

    public static ClientGameAppState getInstance() {
        return instance;
    }

    public AnalogListener getAnalogListener() {
        return analogListener;
    }

    public void setAnalogListener(AnalogListener analogListener) {
        this.analogListener = analogListener;
    }

}
