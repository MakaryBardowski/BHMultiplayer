/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import client.ClientGameAppState;
import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.Player;
import client.Main;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import debugging.DebugUtils;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.items.ItemTemplates.GlovesTemplate;
import java.nio.FloatBuffer;
import messages.items.MobItemInteractionMessage;
import messages.items.NewBootsMessage;
import messages.items.NewGlovesMessage;

/**
 *
 * @author 48793
 */
public class Gloves extends Armor {

    public Gloves(int id, GlovesTemplate template, String name, Node node) {
        super(id, template, name, node);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    public Gloves(int id, GlovesTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    @Override
    public void playerEquip(Player m) {
        humanMobEquip(m);
        m.getFirstPersonHands().setFpHands(this);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void humanMobEquip(HumanMob m) {
        m.setGloves(this);
//        Node test = m.getSkinningControl().getAttachmentsNode("HandR");
//        var debugNode = DebugUtils.createUnshadedBoxNode();
//        debugNode.scale(1,4f,1);
//        test.attachChild(debugNode);
        var r = (Node) m.getThirdPersonHandsNode();
        r.detachAllChildren();

//        var gloveR = debugNode;
        Node gloveR = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "R"));
        Mesh gloveMesh = ((Geometry) gloveR.getChild(0)).getMesh();
        byte leftHandIndex = (byte) m.getSkinningControl().getArmature().getJointIndex("HandL");
        byte righHandIndex = (byte) m.getSkinningControl().getArmature().getJointIndex("HandR");

        Gloves.weightPaint(gloveMesh, leftHandIndex, righHandIndex);
        setupModelLight(gloveR);
        setupModelShootability(gloveR, m.getId());

        r.attachChild((Geometry) gloveR.getChild(0));
    }

    @Override
    public void humanMobUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onInteract() {
        ClientGameAppState gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewGlovesMessage msg = new NewGlovesMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void playerServerEquip(HumanMob m) {
        m.setGloves(this);
    }

    @Override
    public void playerServerUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Worn\n");
        builder.append("Armor value: ");
        builder.append(armorValue);
        return builder.toString();
    }

    private static void weightPaint(Mesh mesh, byte leftHandBoneIndex, byte rightHandBoneIndex) {
        if (leftHandBoneIndex == -1 || rightHandBoneIndex == -1) {
            return;
        }

        var posBuffer = (FloatBuffer) (mesh.getBuffer(VertexBuffer.Type.Position).getData());
        int posBufferLimit = posBuffer.limit();
        int vertexCount = posBufferLimit / 3;// by default

        int boneIndexCount = vertexCount;
        byte[] boneIndex = new byte[boneIndexCount];
        float[] boneWeight = new float[boneIndexCount];
        var temp = new Vector3f();
        System.out.println("vertex count " + vertexCount);
        for (int i = 0; i < posBufferLimit; i += 3) { // for each vertex position
            temp.set(posBuffer.get(i), posBuffer.get(i + 1), posBuffer.get(i + 2));

            var vertexIndex = i / 3;
            posBuffer.put(i + 1, posBuffer.get(i + 1) + 1.5f); // set blender Y to higher = jmonkey Z

            if (temp.getX() < 0) {
                // weight paint for right hand bone
                boneIndex[vertexIndex] = rightHandBoneIndex;
                boneWeight[vertexIndex] = 100f;
            } else {
                // weight paint for left hand bone
                boneIndex[vertexIndex] = leftHandBoneIndex;
                boneWeight[vertexIndex] = 100f;
            }
        }

        mesh.setMaxNumWeights(1);
        mesh.setBuffer(VertexBuffer.Type.BoneIndex, 1, boneIndex);
        mesh.setBuffer(VertexBuffer.Type.BoneWeight, 1, boneWeight);
        mesh.setBuffer(VertexBuffer.Type.HWBoneIndex, 1, boneIndex);
        mesh.setBuffer(VertexBuffer.Type.HWBoneWeight, 1, boneWeight);
        if (mesh.getBuffer(VertexBuffer.Type.BindPosePosition) == null) {
            mesh.generateBindPose();
        }
    }

}
