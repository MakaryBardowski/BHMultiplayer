/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import LemurGUI.LemurPlayerHealthbar;
import static client.ClientSynchronizationUtils.interpolateGrenadePosition;
import static client.ClientSynchronizationUtils.interpolateMobPosition;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.network.Client;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import debugging.DebugUtils;
import game.entities.InteractiveEntity;
import game.entities.factories.AllMobFactory;
import game.entities.factories.MobSpawnType;
import game.entities.factories.PlayerFactory;
import game.entities.grenades.ThrownGrenade;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.map.MapGenerator;
import game.map.MapType;
import game.map.collision.WorldGrid;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import server.LevelManager;

/**
 *
 * @author 48793
 */
public class ClientLevelManager extends LevelManager {

    private static final Main MAIN_INSTANCE = Main.getInstance();
    private final ClientGameAppState GAME_APP_STATE = ClientGameAppState.getInstance();
    @Getter
    @Setter
    private Client client;

    @Getter
    private final AssetManager assetManager;

    @Getter
    private final RenderManager renderManager;

    @Getter
    private final InputManager inputManager;

    @Getter
    @Setter
    private Player player;

    @Getter
    private final ConcurrentHashMap<Integer, InteractiveEntity> mobs = new ConcurrentHashMap<>();

    @Getter
    private WorldGrid grid;

    @Getter
    private game.map.Level map;

    @Getter
    private final int BLOCK_SIZE = 3;

    @Getter
    private final int COLLISION_GRID_CELL_SIZE = 18;

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

    public ClientLevelManager() {
        this.assetManager = MAIN_INSTANCE.getAssetManager();
        this.renderManager = MAIN_INSTANCE.getRenderManager();
        this.inputManager = MAIN_INSTANCE.getInputManager();
    }

    public void initialize() {
        var trueRootNode = Main.getInstance().getRootNode();
        trueRootNode.attachChild(rootNode);

        worldNode.attachChild(debugNode);
        worldNode.attachChild(entityNode);
        pickableNode.attachChild(destructibleNode);
        entityNode.attachChild(pickableNode);
        worldNode.attachChild(mapNode);
        rootNode.attachChild(worldNode);

        float renderDistance = 700f;
        MAIN_INSTANCE.getCamera().setFrustumPerspective(70, (float) GAME_APP_STATE.getSettings().getWidth() / GAME_APP_STATE.getSettings().getHeight(), 0.01f, renderDistance);

        MAIN_INSTANCE.getViewPort().setBackgroundColor(ColorRGBA.Cyan.clone());
        MAIN_INSTANCE.getViewPort().setClearColor(true);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.7f));
        worldNode.addLight(al);

        grid = new WorldGrid(MAP_SIZE, BLOCK_SIZE, COLLISION_GRID_CELL_SIZE);
//        DebugUtils.drawGrid();

    }

    @Override
    public void jumpToLevel(int levelIndex) {

        currentLevelIndex = levelIndex;

        var levelSeed = levelSeeds[levelIndex];
        var levelType = levelTypes[levelIndex];
//        System.out.println("CLIENT: level seed "+levelSeed);
//        System.out.println("CLIENT: generating map of type "+levelType);
        MapGenerator mg = new MapGenerator(levelSeed, levelType,MAP_SIZE);

        MAIN_INSTANCE.enqueue(() -> {
            if (map == null) {
                map = mg.generateMap(
                        BLOCK_SIZE,
                        CHUNK_SIZE,
                        MAP_SIZE,
                        assetManager,
                        mapNode);
            } else {
                mg.generateOnExistingMapNoMemoryAllocation(map,
                        BLOCK_SIZE,
                        CHUNK_SIZE,
                        MAP_SIZE,
                        assetManager,
                        mapNode);
            }
        });

    }

    public Mob registerMob(Integer id, MobSpawnType spawnType) {
        var mobFactory = new AllMobFactory(id,
                assetManager,
                destructibleNode);

        var p = mobFactory.createClientSide(spawnType);
        this.mobs.put(id, p);
        return p;
    }

    public void updateLoop(float tpf) {
        if (player != null) {
            if(player.getPlayerHud() != null){
                player.getPlayerHud().updateHealthbar(tpf);
            }
            player.move(tpf);
            player.updateTemporaryEffectsClient();
            if (player.isHoldsTrigger() && player.getEquippedRightHand() != null) {
                player.getEquippedRightHand().playerUseInRightHand(player);
            }

        }

        mobs.values().forEach(x -> {
            if (x instanceof Mob m && x != player) {
                if (!m.getNode().getWorldTranslation().equals(m.getServerLocation())) {
                    grid.remove(m);
                    interpolateMobPosition(m, tpf);
                    grid.insert(m);
                }
                m.interpolateRotation(tpf);
            } else if (x instanceof ThrownGrenade g) {
                if (!g.getNode().getWorldTranslation().equals(g.getServerLocation())) {
                    interpolateGrenadePosition(g, tpf);
                }
            }
        }
        );
    }

    public Player registerPlayer(Integer id, boolean setAsPlayer, int playerClassIndex) {
        Player p = new PlayerFactory(id, destructibleNode, MAIN_INSTANCE.getCamera(), setAsPlayer).createClientSide(null, playerClassIndex);
        this.mobs.put(id, p);
        return p;
    }

    public <T extends InteractiveEntity> T registerEntity(T entity) {
        this.mobs.put(entity.getId(), entity);
        return entity;
    }

    public FlyByCamera getFlyCam() {
        return MAIN_INSTANCE.getFlyByCamera();
    }

    public AppStateManager getStateManager() {
        return MAIN_INSTANCE.getStateManager();
    }

}
