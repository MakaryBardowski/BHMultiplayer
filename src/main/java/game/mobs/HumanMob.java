/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.mobs;

import static game.cameraAndInput.InputController.projectBlood;
import game.effects.EmitterPooler;
import game.effects.GoreControl;
import game.effects.GoreParticle;
import game.items.Equippable;
import game.items.Holdable;
import game.items.Item;
import game.map.collision.CollidableInterface;
import game.map.collision.WorldGrid;
import messages.MobHealthUpdateMessage;
import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
    public void move(float tpf, ClientGameAppState cm) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void die() {
        List<Node> guts = new ArrayList<>();
        Node gore = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso0.j3o");
        spawnDeathGore(gore, new Vector3f(0, 1.2f, 0));
        Node gore1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso1.j3o");
        spawnDeathGore(gore1, new Vector3f(0.1f, 1.4f, -0.1f));
        Node gore2 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso2.j3o");
        spawnDeathGore(gore2, new Vector3f(-0.15f, 1.6f, 0.1f));
        Node goreHand = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand0.j3o");
        spawnDeathGore(goreHand, new Vector3f(0.6f, 1.2f, 0));
        Node goreHand1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand1.j3o");
        spawnDeathGore(goreHand1, new Vector3f(-0.6f, 1.2f, 0));
        Node goreHand2 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand2.j3o");
        spawnDeathGore(goreHand2, new Vector3f(-0.6f, 1.2f, 0));
        Node goreHead = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHead0.j3o");
        spawnDeathGore(goreHead, new Vector3f(0f, 2.12f, 0));
        Node goreHead1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHead1.j3o");
        spawnDeathGore(goreHead1, new Vector3f(0.1f, 2.2f, -0.1f));
        Node goreLeg = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreLeg0.j3o");
        spawnDeathGore(goreLeg, new Vector3f(0.6f, 0.6f, 0.1f));
        Node goreLeg1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreLeg1.j3o");
        spawnDeathGore(goreLeg1, new Vector3f(-0.6f, 0.6f, -0.1f));
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
            gut.scale(1 + r.nextFloat() * 0.25f);
        }
//        ClientGameAppState.getInstance().getDebugNode().instance();
        node.removeFromParent();
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void receiveDamage(float damage) {
        health = health - damage;
        MobHealthUpdateMessage hpUpd = new MobHealthUpdateMessage(id, health);
        ClientGameAppState.getInstance().getClient().send(hpUpd);

                ParticleEmitter blood = EmitterPooler.getBlood();
        Vector3f bloodPos = node.getWorldTranslation().clone().add(0, 2, 0);
        blood.setLocalTranslation(bloodPos);
        if (health <= 0) {
            blood.emitParticles(50);
            die();
        } else {
            blood.emitParticles(20); //50
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

    private void spawnDeathGore(Node gore, Vector3f initialPos) {
        for (Spatial c : gore.getChildren()) {
            if (c != null && c instanceof Geometry) {
                Geometry g = (Geometry) c;
                Material originalMaterial = g.getMaterial();
                Material newMaterial = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
                newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
//                newMaterial.setBoolean("UseInstancing", true);
                g.setMaterial(newMaterial);
            }
        }

        ClientGameAppState.getInstance().getDebugNode().attachChild(gore);
        gore.setLocalTranslation(node.getWorldTranslation().clone().add(initialPos));
        Random r = new Random();
        // Constants for the range of initial velocity you want to randomize
        float minVelocityX = -2.0f; // Minimum initial velocity in the x-direction
        float maxVelocityX = 2.0f; // Maximum initial velocity in the x-direction
        float minVelocityY = 3.0f;  // Minimum initial velocity in the y-direction
        float maxVelocityY = 6.0f; // Maximum initial velocity in the y-direction
        float minVelocityZ = -2.0f; // Minimum initial velocity in the z-direction
        float maxVelocityZ = 2.0f;  // Maximum initial velocity in the z-direction

        // Create an instance of the Random class
        Random random = new Random();

        // Generate random initial velocity components within the specified ranges
        float initialVelocityX = minVelocityX + (maxVelocityX - minVelocityX) * random.nextFloat();
        float initialVelocityY = minVelocityY + (maxVelocityY - minVelocityY) * random.nextFloat();
        float initialVelocityZ = minVelocityZ + (maxVelocityZ - minVelocityZ) * random.nextFloat();
        Vector3f velocity = new Vector3f(initialVelocityX, initialVelocityY, initialVelocityZ);
        float rotx = r.nextFloat() * r.nextInt(9) * (r.nextInt(3) - 1);
        float roty = r.nextFloat() * r.nextInt(9) * (r.nextInt(3) - 1);
        float rotz = r.nextFloat() * r.nextInt(9) * (r.nextInt(3) - 1);

        Vector3f rotVelocity = new Vector3f(rotx * 3, roty * 3, rotz * 3);
        
        float rotMult = 1;
        float linearMult = 1;
        rotVelocity.multLocal(rotMult);
        velocity.multLocal(linearMult);

        GoreParticle particle = new GoreParticle(gore, velocity, rotVelocity, node.getWorldTranslation().getY());
        gore.addControl(new GoreControl(List.of(particle)));
    }
}
