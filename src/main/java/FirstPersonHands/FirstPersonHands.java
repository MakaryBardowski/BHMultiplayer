/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FirstPersonHands;

import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.custom.ArmatureDebugger;
import game.entities.mobs.Player;
import game.items.armor.Gloves;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class FirstPersonHands {

    private static final Vector3f rootNodePosition = new Vector3f(0, -0.7f, 0);

    private final Player player;

    @Getter
    private Node handsNode, rightHandEquipped, leftHandEquipped;

    @Getter
    private AnimComposer handsComposer;

    private final Node handsRootNode = new Node();

    public FirstPersonHands(Player player) {
        this.player = player;
    }

    public void setFpHands(Gloves gloves) {
        setuoHands();
    }

    private void setuoHands() {
        if (handsRootNode.getParent() != null) {
            handsRootNode.removeFromParent();
        }

        player.getGunNode().attachChild(handsRootNode);
        handsRootNode.move(rootNodePosition);
        handsRootNode.scale(0.8f);

        var assetManager = Main.getInstance().getAssetManager();

        Node handR = (Node) assetManager.loadModel("Models/bareHandFP_R/bareHandFP_R.j3o");

        SkinningControl skinningControl = handR.getChild(0).getControl(SkinningControl.class);

        ArmatureDebugger skeletonDebug = new ArmatureDebugger("skeleton", skinningControl.getArmature(), skinningControl.getArmature().getJointList());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
//        handR.attachChild(skeletonDebug);

        Geometry armGeom = (Geometry) handR.getChild("bareHandFP");
        Material originalMaterial = armGeom.getMaterial();
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
        armGeom.setMaterial(newMaterial);

        rightHandEquipped = skinningControl.getAttachmentsNode("AttachmentBoneR");
        handsNode = handR;
        handsRootNode.attachChild(handR);

        handsComposer = handR.getChild(0).getControl(AnimComposer.class);
        setHandsAnim(FirstPersonHandAnimationData.HOLD_PISTOL);

//        handR.move(0,-0.7f,0);
//        handR.move(-0.75f, -0.7f, 0.5f);
    }

    public void attachToHandR(Spatial model) {
        rightHandEquipped.attachChild(model);
    }

    public void attachToHandL(Spatial model) {

    }

    public void setHandsAnim(FirstPersonHandAnimationData animData) {
//    handsComposer.setCurrentAction(animData.getAnimationName());
        handsComposer.setCurrentAction(animData.getAnimationName(), "Default", false);
        handsRootNode.setLocalTranslation(rootNodePosition.add(animData.getRootOffset()));
    }
}
