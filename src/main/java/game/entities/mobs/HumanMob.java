/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs;

import game.effects.EmitterPooler;
import game.items.Equippable;
import game.items.Holdable;
import game.items.Item;
import game.map.collision.WorldGrid;
import client.ClientGameAppState;
import client.ClientSynchronizationUtils;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.AnimationMask;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.SkinningControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import game.effects.ParticleUtils;
import game.entities.Collidable;
import game.entities.Destructible;
import static game.entities.factories.MobSpawnType.HUMAN;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.map.collision.RectangleAABB;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import messages.DestructibleDamageReceiveMessage;
import messages.NewMobMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class HumanMob extends Mob {

    protected Holdable equippedRightHand;
    protected Holdable equippedLeftHand;
    protected SkinningControl skinningControl;

    @Getter
    @Setter
    protected Helmet helmet;
    @Getter
    @Setter
    protected Helmet defaultHelmet; // equipped when nothing is equipped (bare head)
    @Getter
    @Setter
    protected Vest vest;
    @Getter
    @Setter
    protected Vest defaultVest;
    @Getter
    @Setter
    protected Boots boots;
    @Getter
    @Setter
    protected Boots defaultBoots;
    @Getter
    @Setter
    protected Gloves gloves;
    @Getter
    @Setter
    protected Gloves defaultGloves;

    @Getter
    private AnimComposer modelComposer;

    private Geometry hitboxDebug;

    public HumanMob(int id, Node node, String name, SkinningControl skinningControl, AnimComposer modelComposer) {
        super(id, node, name);
        this.skinningControl = skinningControl;
        this.modelComposer = modelComposer;

        var armature = skinningControl.getArmature();
        ArmatureMask mask = new ArmatureMask();
        mask.addBones(armature, "LegL");
        mask.addBones(armature, "LegR");
        mask.addBones(armature, "Spine");
        modelComposer.makeLayer("Legs", mask);
        modelComposer.setCurrentAction("Idle", "Legs");

        createHitbox();
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        shooter.notifyServerAboutDealingDamage(damage, this);
    }

    @Override
    public void onInteract() {
        System.out.println(name + " says hi! ");
    }

    @Override
    public void move(float tpf ) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void die() {
        dropEquipment();
        List<Node> guts = new ArrayList<>();
        Node gore = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore, node.getWorldTranslation().add(new Vector3f(0, 1.2f, 0)));
        Node gore1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore1, node.getWorldTranslation().add(new Vector3f(0.1f, 1.4f, -0.1f)));
        Node gore2 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso2.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore2, node.getWorldTranslation().add(new Vector3f(-0.15f, 1.6f, 0.1f)));
        Node goreHand = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHand, node.getWorldTranslation().add(new Vector3f(0.6f, 1.2f, 0)));
        Node goreHand1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHand1, node.getWorldTranslation().add(new Vector3f(-0.6f, 1.2f, 0)));
        Node goreHand2 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand2.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHand2, node.getWorldTranslation().add(new Vector3f(-0.6f, 1.2f, 0)));
        Node goreHead = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHead0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHead, node.getWorldTranslation().add(new Vector3f(0f, 2.12f, 0)));
        Node goreHead1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHead1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHead1, node.getWorldTranslation().add(new Vector3f(0.1f, 2.2f, -0.1f)));
        Node goreLeg = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreLeg0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreLeg, node.getWorldTranslation().add(new Vector3f(0.6f, 0.6f, 0.1f)));
        Node goreLeg1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreLeg1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreLeg1, node.getWorldTranslation().add(new Vector3f(-0.6f, 0.6f, -0.1f)));
        guts.add(gore);
        guts.add(gore1);
        guts.add(gore2);
        guts.add(goreHand);
        guts.add(goreHand1);
        guts.add(goreHand2);
        guts.add(goreHead);
        guts.add(goreHead1);
        guts.add(goreLeg);
        guts.add(goreLeg1);
        Random r = new Random();
        for (Node gut : guts) {
            gut.scale(1 + r.nextFloat(0.25f));
        }
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void receiveDamage(float damage) {
        health -= calculateDamage(damage);

        var notMe = this != ClientGameAppState.getInstance().getPlayer();
        ParticleEmitter blood = EmitterPooler.getBlood();
        Vector3f bloodPos = node.getWorldTranslation().clone().add(0, 2, 0);
        blood.setLocalTranslation(bloodPos);
        if (health <= 0) {
            if (notMe) {
                blood.emitParticles(50);
            }
            die();
        } else if (notMe) {
            blood.emitParticles(20);
        }

    }

    @Override
    public void notifyServerAboutDealingDamage(float damage, Destructible mob) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(mob.getId(), damage);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    @Override
    public void onCollision() {
    }

    public Holdable getEquippedRightHand() {
        return equippedRightHand;
    }

    public void setEquippedRightHand(Holdable equippedRightHand) {
        this.equippedRightHand = equippedRightHand;
    }

    public Holdable getEquippedLeftHand() {
        return equippedLeftHand;
    }

    public void setEquippedLeftHand(Holdable equippedLeftHand) {
        this.equippedLeftHand = equippedLeftHand;
    }

    public SkinningControl getSkinningControl() {
        return skinningControl;
    }

    @Override
    public void equip(Item i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewMobMessage msg = new NewMobMessage(this, node.getWorldTranslation(), HUMAN);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void unequip(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void equipServer(Item e) {
        if (e instanceof Equippable equippableItem) {
            equippableItem.playerServerEquip(this);
        }
    }

    @Override
    public void unequipServer(Item e) {
        if (e instanceof Equippable equippableItem) {
            equippableItem.playerServerUnequip(this);
        }
    }

    @Override
    public float getArmorValue() {
        return helmet.getArmorValue() + vest.getArmorValue()
                + gloves.getArmorValue()
                + boots.getArmorValue();
    }

    @Override
    public float calculateDamage(float damage) {
        float reducedDmg = damage - getArmorValue();
        return reducedDmg > 0 ? reducedDmg : 0;
//return 0;
    }

    @Override
    protected final void createHitbox() {
        float hitboxWidth = 0.5f;
        float hitboxHeight = 1.25f;
        float hitboxLength = 0.5f;
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();
        System.out.println(" hitbox " + collisionShape);
    }

    @Override
    public void setPosition(Vector3f newPos) {
        WorldGrid grid = ClientGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerMain.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(Collidable other) {
    }

    @Override
    public void interpolateRotation(float tpf) {
        setRotInterpolationValue(Math.min(rotInterpolationValue + MOB_ROTATION_RATE * tpf, 1));

        node.getLocalRotation().nlerp(ClientSynchronizationUtils.GetYAxisRotation(serverRotation), rotInterpolationValue);
        node.setLocalRotation(node.getLocalRotation());

        skinningControl.getArmature().getJoint("HandR").getLocalRotation().nlerp(
                ClientSynchronizationUtils.GetXAxisRotation(getServerRotation()), rotInterpolationValue
        );

        var rot = skinningControl.getArmature().getJoint("HandR").getLocalRotation();
        skinningControl.getArmature().getJoint("HandL").setLocalRotation(rot);
        skinningControl.getArmature().getJoint("Head").getLocalTransform().setRotation(rot);
    }

    @Override
    public void setPosInterpolationValue(float posInterpolationValue) {
        super.setPosInterpolationValue(posInterpolationValue);

        System.out.println("posInterpolationValue " + posInterpolationValue);
        System.out.println("modelComposer.getLayer(\"Legs\").getTime() " + modelComposer.getLayer("Legs").getTime());

        if (!modelComposer.getLayer("Legs").getCurrentActionName().equals("Run")) {
            modelComposer.setCurrentAction("Run", "Legs");
            modelComposer.getLayer("Legs").getCurrentAction().setSpeed(2);
        }  

    }

}
