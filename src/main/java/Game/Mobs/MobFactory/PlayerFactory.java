/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs.MobFactory;

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

/**
 *
 * @author 48793
 */
public class PlayerFactory extends MobFactory {

    private final int mobId;
    private Camera firstPersonCamera;
    private RenderManager renderManager;

    public PlayerFactory(int id, AssetManager assetManager, Node mobNode, Camera mainCamera, RenderManager renderManager) {
        super(assetManager, mobNode);
        //System.out.println(mainCamera.);
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
        Player p = new Player(mobId, playerNode, name);

        Vector3f playerSpawnpoint = new Vector3f(0, 5, 0);
        attachToMobsNode(playerNode, playerSpawnpoint);
        Debugging.DebugUtils.addArrow(playerNode, assetManager);
        
        firstPersonCameraSetup(p, playerNode);

        return p;
    }
    
    private void firstPersonCameraSetup(Player p, Node playerNode){
        Node gunCameraNode = new CameraNode("Gun Camera Node", firstPersonCamera);
        Node model = (Node) assetManager.loadModel("Models/testRifleFP/testRifleFP.j3o");
        model.move(-.45f, -.52f, 1.8f);
        model.setLocalRotation((new Quaternion()).fromAngleAxis(FastMath.PI / 32, new Vector3f(-.15f, .5f, 0)));
        
        /// i don't know why the setupModelLight() method doesn't work <<-- big congo
            Geometry ge = (Geometry) ((Node) model.getChild(0)).getChild(0);
            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);
        ///
        
        SkinningControl skinningControl = model.getChild(0).getControl(SkinningControl.class);
        Node muzzleNode = skinningControl.getAttachmentsNode("muzzleAttachmentBone");
        
        p.addToGunNode(model, muzzleNode);
        
        ViewPort view2 = renderManager.createMainView("View of firstPersonCamera", firstPersonCamera);
        view2.setClearFlags(false, true, true);
        view2.attachScene(p.getGunNode());
        firstPersonCamera.setFrustumPerspective(45f, (float) firstPersonCamera.getWidth() / firstPersonCamera.getHeight(), 0.01f, 1000f);
        
        playerNode.attachChild(gunCameraNode);
        gunCameraNode.attachChild(p.getGunNode());
        
        model.move((gunCameraNode.getLocalTranslation().clone().subtract(model.getLocalTranslation().clone())).mult(.9f));
        gunCameraNode.setCullHint(Spatial.CullHint.Never);
    }
    
    private Node loadPlayerModel() {
        return (Node) assetManager.loadModel("Models/testHuman/testHuman.j3o");
    }

}
