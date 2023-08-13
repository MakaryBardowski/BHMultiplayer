/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs;

import game.effects.EmitterPooler;
import game.effects.PhysicalParticleControl;
import game.effects.PhysicalParticle;
import game.items.Equippable;
import game.items.Holdable;
import game.items.Item;
import game.map.collision.CollidableInterface;
import game.map.collision.WorldGrid;
import messages.DestructibleHealthUpdateMessage;
import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Filters;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.effects.ParticleUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import messages.NewMobMessage;

/**
 *
 * @author 48793
 */
public class HumanMob extends Mob {

    protected Holdable equippedRightHand;
    protected Holdable equippedLeftHand;
    protected SkinningControl skinningControl;

    public HumanMob(int id, Node node, String name, SkinningControl skinningControl) {
        super(id, node, name);
        this.skinningControl = skinningControl;
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        shooter.dealDamage(damage, this);
    }

    @Override
    public void onInteract() {
        System.out.println(name + " says hi! ");
    }

    @Override
    public void move(float tpf, ClientGameAppState cm) {
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
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void receiveDamage(float damage) {
        health = health - damage;
        DestructibleHealthUpdateMessage hpUpd = new DestructibleHealthUpdateMessage(id, health);
        ClientGameAppState.getInstance().getClient().send(hpUpd);

        ParticleEmitter blood = EmitterPooler.getBlood();
        Vector3f bloodPos = node.getWorldTranslation().clone().add(0, 2, 0);
        blood.setLocalTranslation(bloodPos);
        if (health <= 0) {
            blood.emitParticles(50);
            die();
        } else {
            blood.emitParticles(20);
        }
    }

    @Override
    public void dealDamage(float damage, Mob mob) {
        mob.receiveDamage(damage);
    }

    @Override
    public void insert(WorldGrid wg) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void removeFromGrid(WorldGrid wg) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HashSet<CollidableInterface> getFromCellsImIn(WorldGrid wg) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HashSet<CollidableInterface> getEntitiesFromTilesInRange(WorldGrid wg, float distance) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void checkCollision() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onCollision() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
        NewMobMessage msg = new NewMobMessage(id, node.getWorldTranslation());
        msg.setReliable(true);
        return msg;
    }
}
