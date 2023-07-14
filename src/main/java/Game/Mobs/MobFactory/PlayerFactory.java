/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs.MobFactory;

import Game.Items.ItemTemplates;
import Game.Items.Rifle;
import Game.Mobs.Mob;
import Game.Mobs.Player;
import com.Networking.Client.ClientGameAppState;
import com.Networking.Client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import java.util.Arrays;

/**
 *
 * @author 48793
 */
public class PlayerFactory extends MobFactory {

    private static final float PLAYER_HEIGHT = 2.12f;
    private final int mobId;
    private final Camera mainCamera;
    private final Camera firstPersonCamera;
    private final RenderManager renderManager;
    private boolean setAsPlayer;

    public PlayerFactory(int id, AssetManager assetManager, Node mobNode, RenderManager renderManager) {
        super(assetManager, mobNode);
        this.mainCamera = null;
        this.firstPersonCamera = null;
        this.mobId = id;
        this.renderManager = renderManager;
    }

    public PlayerFactory(int id, Node mobNode, Camera mainCamera, boolean setAsPlayer) {
        super( mobNode);
        this.mainCamera = mainCamera;
        this.firstPersonCamera = mainCamera.clone();
        this.mobId = id;
        this.renderManager = Main.getInstance().getRenderManager();
        this.setAsPlayer = setAsPlayer;
    }

    @Override
    public Player createClientSide() {
        Node playerNode = loadPlayerModel();
        setupModelLight(playerNode);
        setupModelShootability(playerNode, mobId);
        String name = "Gracz_" + mobId;
        Player p = new Player(mobId, playerNode, name, mainCamera);
        Vector3f playerSpawnpoint = new Vector3f(0, 4, 0);
        attachToMobsNode(playerNode, playerSpawnpoint);
        Debugging.DebugUtils.addArrow(playerNode, assetManager);
        if (setAsPlayer) {
            setupFirstPersonCamera(p);
            addStartEquipment(p);
        }
        return p;
    }

    @Override
    public Player createServerSide() {
        Node playerNode = loadPlayerModel();
        setupModelLight(playerNode);
        setupModelShootability(playerNode, mobId);
        String name = "Gracz_" + mobId;
        Player p = new Player(mobId, playerNode, name, mainCamera);
        Vector3f playerSpawnpoint = new Vector3f(0, 4, 0);
        attachToMobsNode(playerNode, playerSpawnpoint);
        addStartEquipment(p);
        return p;
    }

    private void setupFirstPersonCamera(Player p) {
        CameraNode playerCameraNode = new CameraNode("Main Camera Node" + p.getId(), mainCamera);
        p.getRotationNode().setName("player " + p.getId() + " rotation node");
        p.getRotationNode().attachChild(playerCameraNode);
        p.getNode().attachChild(p.getRotationNode());
        p.getRotationNode().setLocalTranslation(0, PLAYER_HEIGHT, 0);

        CameraNode gunCameraNode = new CameraNode("Gun Camera Node" + p.getId(), firstPersonCamera);
        gunCameraNode.move(0, PLAYER_HEIGHT, 0);
        ViewPort view2 = renderManager.createMainView("View of firstPersonCamera", firstPersonCamera);
        view2.setClearFlags(false, true, true);
        view2.attachScene(p.getGunNode());
        firstPersonCamera.setFrustumPerspective(45f, (float) firstPersonCamera.getWidth() / firstPersonCamera.getHeight(), 0.01f, 1000f);
        p.getNode().attachChild(gunCameraNode);
        gunCameraNode.attachChild(p.getGunNode());
        gunCameraNode.setCullHint(Spatial.CullHint.Never);

        p.setMainCameraNode(playerCameraNode);
        p.setFirstPersonCameraNode(gunCameraNode);
    }

    private Node loadPlayerModel() {
        return (Node) assetManager.loadModel("Models/testHuman/testHuman.j3o");
    }

    private void addStartEquipment(Player p) {
        p.getEquipment()[0] = new Rifle(4, ItemTemplates.RIFLE_MANNLICHER_95);
        p.getHotbar()[0] = p.getEquipment()[0];
    }
    
    




}
