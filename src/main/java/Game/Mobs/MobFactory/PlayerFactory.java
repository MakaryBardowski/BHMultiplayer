/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs.MobFactory;

import Game.Items.ItemTemplates;
import Game.Items.Rifle;
import Game.Mobs.Player;
import com.Networking.Client.ClientGameAppState;
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

    private final int mobId;
    private final Camera mainCamera;
    private Camera firstPersonCamera;
    private RenderManager renderManager;

    public PlayerFactory(int id, AssetManager assetManager, Node mobNode, Camera mainCamera, RenderManager renderManager) {
        super(assetManager, mobNode);
        this.mainCamera = mainCamera;
        this.firstPersonCamera = mainCamera.clone();
        this.mobId = id;
        this.renderManager = renderManager;
    }

    @Override
    public Player create() {
        Node playerNode = loadPlayerModel();
        setupModelLight(playerNode);
        setupModelShootability(playerNode, mobId);

        String name = "Gracz_" + mobId;
        Player p = new Player(mobId, playerNode, name,mainCamera);

        Vector3f playerSpawnpoint = new Vector3f(0, 4, 0);
        attachToMobsNode(playerNode, playerSpawnpoint);
        Debugging.DebugUtils.addArrow(playerNode, assetManager);
        
        setupFirstPersonCamera(p, playerNode);
        
        addStartEquipment(p);
        return p;
    }
    
    private void setupFirstPersonCamera(Player p, Node playerNode){
        CameraNode gunCameraNode = new CameraNode("Gun Camera Node", firstPersonCamera);
                gunCameraNode.move(0,2.12f,0);
        ViewPort view2 = renderManager.createMainView("View of firstPersonCamera", firstPersonCamera);
        view2.setClearFlags(false, true, true);
        view2.attachScene(p.getGunNode());
gunCameraNode.setControlDir(ControlDirection.SpatialToCamera);
        firstPersonCamera.setFrustumPerspective(45f, (float) firstPersonCamera.getWidth() / firstPersonCamera.getHeight(), 0.01f, 1000f);
        playerNode.attachChild(gunCameraNode);
        gunCameraNode.attachChild(p.getGunNode());
        gunCameraNode.setCullHint(Spatial.CullHint.Never);
    }
    
    private Node loadPlayerModel() {
        return (Node) assetManager.loadModel("Models/testHuman/testHuman.j3o");
    }
    
    
    private void addStartEquipment(Player p){
    p.getEquipment()[0] = new Rifle(ItemTemplates.RIFLE_MANNLICHER_95);
    p.getHotbar()[0] = p.getEquipment()[0];
    }

}
