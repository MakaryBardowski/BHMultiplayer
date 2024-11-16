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
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.custom.ArmatureDebugger;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import game.entities.mobs.player.Player;
import game.items.armor.Gloves;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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

        // This is how to change first person hand models. uncomment to understand.
//        Node TEST_HAND = (Node) assetManager.loadModel("Models/bareHandFP_R/bareHandFP_R.j3o");
//        Geometry tg = (Geometry) ((Node) TEST_HAND.getChild(0)).getChild(0);
//        tg.move(0, 0, 1f);
//       
//        // do not delete below code. Allows to weight paint any mesh.
////        Box box = new Box(0.6f, 0.6f, 0.6f);
////        skinning(box, (byte) skinningControl.getArmature().getJoint("HandR").getId());
////        Geometry g = new Geometry("box", box);
////        g.move(0, 0, 3);
////        Material ma = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
////        ma.setColor("Color", ColorRGBA.Red);
////        g.setMaterial(ma);
//        
//        ((Node) handR.getChild(0)).attachChild(tg);

        //test
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

    private void skinning(Mesh mesh, byte targetBoneIndex) {
        if (targetBoneIndex == -1) {
            return;
        }

        // Calculate vertex count
        int limit = mesh.getBuffer(Type.Position).getData().limit();
        // Notice: i should call mesh.getMode() to decide how many 
        // floats is used for each vertex. Default mode is Mode.Triangles
        int vertexCount = limit / 3;// by default

        int boneIndexCount = vertexCount * 4;
        byte[] boneIndex = new byte[boneIndexCount];
        float[] boneWeight = new float[boneIndexCount];

        // calculate bone indices and bone weights;
        for (int i = 0; i < boneIndexCount; i += 4) {
            boneIndex[i] = targetBoneIndex;
            // I don't need the other 3 indices so I discard them
            boneIndex[i + 1] = 0;
            boneIndex[i + 2] = 0;
            boneIndex[i + 3] = 0;

            boneWeight[i] = 1;
            // I don't need the other 3 indices so I discard them
            boneWeight[i + 1] = 0;
            boneWeight[i + 2] = 0;
            boneWeight[i + 3] = 0;
        }
        mesh.setMaxNumWeights(1);

        // apply software skinning
        mesh.setBuffer(Type.BoneIndex, 4, boneIndex);
        mesh.setBuffer(Type.BoneWeight, 4, boneWeight);
        // apply hardware skinning
        mesh.setBuffer(Type.HWBoneIndex, 4, boneIndex);
        mesh.setBuffer(Type.HWBoneWeight, 4, boneWeight);

        mesh.generateBindPose(true);
    }
}
