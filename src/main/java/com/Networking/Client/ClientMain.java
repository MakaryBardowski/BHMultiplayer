package com.Networking.Client;

import Game.CameraAndInput.PlayerCameraControlAppState;
import Game.Map.Map;
import Game.Map.MapGenerator;
import Game.Map.MapType;
import Game.Mobs.Mob;
import Game.Mobs.Player;
import Messages.MessageListeners.ClientMessageListener;
import static com.Networking.Client.ClientNetworkingUtils.interpolateMobPosition;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.Networking.NetworkingInitialization;
import com.jme3.input.FlyByCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.AmbientLight;
import com.jme3.network.ClientStateListener;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
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
public class ClientMain extends SimpleApplication implements ClientStateListener {

    private final int BLOCK_SIZE = 5;
    private final int CHUNK_SIZE = 16;
    private final int MAP_SIZE = 48;
    
    private final Node worldNode = new Node("WORLD NODE");
    private final Node mapNode = new Node("MAP NODE");
    private final Node debugNode = new Node("DEBUG NODE");
    private final Node pickableNode = new Node("PICKABLE NODE");

    // obiekt Client - bezposrednio z biblioteki do multi z Jmonkey
    private Client client;
    // kolejka komunikatow od serwera (zeby klient mogl je przerobic jak nie nadaza na biezaco)
    private ConcurrentLinkedQueue<AbstractMessage> messageQueue;
    // wszystkie moby, przechowywane wg. swojego ID
    private HashMap<Integer, Mob> mobs = new HashMap<>();
    // gracz ktorym steruje ten konkretny klient
    private Player player;
    // ustawienia, potrzebne do okreselnia fov kamery (wyciagam z nich rozdzielczosc)
    private AppSettings applicationSettings;
    // action listener - obsluguje input klawiatury/myszki
    private ActionListener actionListener;
    // mapa uzyskana z map generator
    private Map map;
    // nifty obsluguje GUI
    private Nifty nifty;

    public static void main(String[] args) {
        // rejestrujemy klasy serializowalne (nie musicie rozumiec, architektura klient-serwer)
        NetworkingInitialization.initializeSerializables();

        ClientMain app = new ClientMain();
        AppSettings settings1 = new AppSettings(true);
        settings1.setResolution(1920, 1080);
        settings1.setFullscreen(true);
//        settings1.setCenterWindow(false);
//        if (new Random().nextInt(2) == 0) {
//            settings1.setWindowXPosition(0);
//        } else {
//            settings1.setWindowXPosition(1000);
//        }
//        app.setPauseOnLostFocus(false);
        app.setSettings(settings1);

        /* ustawwiamy, ze wszystko co robimy ma byc renderowane (pokaze sie okno)
        w przeciwienstwie do tego, serwer nie wyswietla obrazu wiec ma Type.Headless
         */
        app.start(JmeContext.Type.Display);

        app.applicationSettings = app.settings;
    }

    @Override
    public void simpleInitApp() {

        /* ustawiamy strukture naszego swiata
        debugNode - tutaj beda dolaczone  particle itp
        pickableNode - modele i obiekty z ktorymi mozemy wejsc w interakcje przez klikniecie (moby, przyciski etc)
        mapNode - Wszystko zwiazane z mapa (klocki, jakies krzesla itp)
         */
        worldNode.attachChild(debugNode);
        worldNode.attachChild(pickableNode);
        worldNode.attachChild(mapNode);
        rootNode.attachChild(worldNode);

        //dodajemy state który obsluguje kamere (wygodne rozwiazanie bo mozna go pozniej odpiac np. w menu)
        stateManager.attach(new PlayerCameraControlAppState(this));

        // probujemy podlaczyc sie do serwera (poki co na localhoscie - czyli ip = 127.0.0.1)
        try {
            client = Network.connectToServer("localhost", NetworkingInitialization.PORT);
            client.addClientStateListener(this);
            client.start();

        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        //kolejka rozkazów od serwera (w przypadku slabego kompa pomaga)
        messageQueue = new ConcurrentLinkedQueue();

        //dodajemy listener nas³uchuj¹cy pakietów od serwera (CTRL + Lewy przycisk zeby wejsc w klase)
        client.addMessageListener(new ClientMessageListener(this));

        // kolor tla na niebieski
        viewPort.setBackgroundColor(ColorRGBA.Cyan);

        // predkosc ruszania sie kamery (nie zmienia nic bo kamera jest przymocowana do gracza)
        flyCam.setMoveSpeed(70);

        // generujemy mape
        MapGenerator mg = new MapGenerator();
        map = mg.generateMap(MapType.BOSS, BLOCK_SIZE, CHUNK_SIZE, MAP_SIZE, assetManager, mapNode);

        //to be moved to mapGenerator class
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.7f));
        worldNode.addLight(al);
        
        

    }

    @Override
    public void simpleUpdate(float tpf) {
        /*glowna petla gry, wykonujaca sie 1x na ka¿d¹ klatkê,
        wszystko co sie dzieje w petli (ruszanie sie) wykonywane jest tutaj
        tpf - time per frame (czas na wyrenderowanie klatki) potrzebny jest
        zeby np. predkosc ruchu nie zalezala od fpsow 
        
         */

        if (player != null) {
            player.move(tpf, this);
        }

        /* wrzuce to do metody, ale interpolujemy (plynnie zmieniamy) 
        przemieszczenie mobow i graczy 
         */
        mobs.values().forEach(x -> {
            if (x != player) {
                interpolateMobPosition(x, tpf);
            }
        }
        );

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
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

    public Player registerPlayer(Integer id) { // rejestrujemy gracza

        Player p = Player.spawnPlayer(id, assetManager, pickableNode);
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

    public FlyByCamera getFlyCam() {
        return flyCam;
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

    
}
