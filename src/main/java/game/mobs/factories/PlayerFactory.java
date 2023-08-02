/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.mobs.factories;

import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.ItemTemplates;
import game.items.weapons.Rifle;
import game.items.armor.Vest;
import game.mobs.Player;
import client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

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
    private final Vector3f playerSpawnpoint = new Vector3f(10, 4, 10);

    public PlayerFactory(int id, AssetManager assetManager, Node mobNode, RenderManager renderManager) {
        super(assetManager, mobNode);
        this.mainCamera = null;
        this.firstPersonCamera = null;
        this.mobId = id;
        this.renderManager = renderManager;
    }

    public PlayerFactory(int id, Node mobNode, Camera mainCamera, boolean setAsPlayer) {
        super(mobNode);
        this.mainCamera = mainCamera;
        this.firstPersonCamera = mainCamera.clone();
        this.mobId = id;
        this.renderManager = Main.getInstance().getRenderManager();
        this.setAsPlayer = setAsPlayer;
    }

    @Override
    public Player createClientSide() {
        Player p = createPlayer();
        attachToMobsNode(p, playerSpawnpoint);
        if (setAsPlayer) {
            setupFirstPersonCamera(p);
        }
        addStartEquipment(p);
        equipStartEquipment(p);
        return p;
    }

    @Override
    public Player createServerSide() {
        Player p = createPlayer();
        attachToMobsNode(p, playerSpawnpoint);
        addStartEquipment(p);

        return p;
    }

    private Player createPlayer() {
        Node playerNode = loadPlayerModel();
        String name = "Player_" + mobId;
        SkinningControl skinningControl = getSkinningControl(playerNode);
        return new Player(mobId, playerNode, name, mainCamera, skinningControl);
    }

    private void setupFirstPersonCamera(Player p) {
        setupMainCamera(p);
        setupHandsCamera(p);
    }

    private Node loadPlayerModel() {
        Node node = (Node) assetManager.loadModel("Models/testSkeleton/testSkeleton.j3o");
        return node;
    }

    private void addStartEquipment(Player p) {
        p.getEquipment()[0] = new Rifle(40f, ItemTemplates.RIFLE_MANNLICHER_95);
        p.getHotbar()[0] = p.getEquipment()[0];

        p.getEquipment()[1] = new Vest(ItemTemplates.VEST_TRENCH,true,true);
        p.getEquipment()[2] = new Boots(ItemTemplates.BOOTS_TRENCH);
    }

    private void equipStartEquipment(Player p) {
        p.equip(p.getEquipment()[1]);
        p.equip(p.getEquipment()[2]);
        p.equip(new Helmet(ItemTemplates.HEAD_1,false));
    }

    private SkinningControl getSkinningControl(Node node) {
        return node.getChild(0).getControl(SkinningControl.class);
    }

    private void setupMainCamera(Player p) {
        CameraNode playerCameraNode = new CameraNode("Main Camera Node" + p.getId(), mainCamera);
        p.getRotationNode().setName("player " + p.getId() + " rotation node");
        p.getRotationNode().attachChild(playerCameraNode);
        p.getNode().attachChild(p.getRotationNode());
        p.getRotationNode().setLocalTranslation(0, PLAYER_HEIGHT, 0);
        p.setMainCameraNode(playerCameraNode);
    }

    private void setupHandsCamera(Player p) {
        CameraNode gunCameraNode = new CameraNode("Gun Camera Node" + p.getId(), firstPersonCamera);
        ViewPort view2 = renderManager.createMainView("View of firstPersonCamera", firstPersonCamera);
        view2.setClearFlags(false, true, true);
        view2.attachScene(p.getGunNode());
        firstPersonCamera.setFrustumPerspective(45f, (float) firstPersonCamera.getWidth() / firstPersonCamera.getHeight(), 0.01f, 1000f);
        p.getRotationNode().attachChild(gunCameraNode);
        gunCameraNode.attachChild(p.getGunNode());
        gunCameraNode.setCullHint(Spatial.CullHint.Never);
        p.setFirstPersonCameraNode(gunCameraNode);
    }

}
