/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import static game.entities.DestructibleUtils.attachDestructibleToNode;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.Mob;
import game.items.Item;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.RectangleAABB;
import game.map.collision.WorldGrid;
import lombok.Getter;
import messages.DestructibleDamageReceiveMessage;
import messages.NewChestMessage;
import messages.SystemHealthUpdateMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class Chest extends Destructible {

    @Getter
    private final Item[] equipment = new Item[3];
    private boolean locked;
    private static final String WOODEN_CRATE = "Models/Chests/crate.j3o";

    public Chest(int id, String name, Node node) {
        super(id, name, node);
        locked = true;

        createHitbox();
    }

    public Item addToEquipment(Item item) {
        for (int i = 0; i < equipment.length; i++) {
            if (equipment[i] == null) {
                equipment[i] = item;
                break;
            }
        }
        return item;
    }

    public Item removeFromEquipment(Item item) {
        for (int i = 0; i < equipment.length; i++) {
            if (equipment[i] != null && equipment[i] == item) {
                equipment[i] = null;
                break;
            }
        }
        return item;
    }

    public static Chest createRandomChestClient(int id, Node parentNode, Vector3f offset, AssetManager a) {
        Node node = (Node) a.loadModel(WOODEN_CRATE);
        Chest chest = new Chest(id, "Crate "+id, node);
        node.scale(0.8f);
        chest.hitboxNode.scale(1.25f);
        attachDestructibleToNode(chest, parentNode, offset);
        setupModelShootability(node, id);
        setupModelLight(node);
        return chest;
    }

    public static Chest createRandomChestServer(int id, Node parentNode, Vector3f offset, AssetManager a) {
        Node node = (Node) a.loadModel(WOODEN_CRATE);
        Chest chest = new Chest(id, "Crate "+id, node);
        attachDestructibleToNode(chest, parentNode, offset);
        return chest;
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        notifyServerAboutDealingDamage(damage, this);
    }

    @Override
    public void onInteract() {
        System.out.println("This " + name + " might contain valuable loot.");
    }

    public void notifyServerAboutDealingDamage(float damage, Chest mob) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(mob.getId(), damage);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    @Override
    public void receiveDamage(float damage) {
        health = health - damage;

        if (health <= 0) {
            die();
        }
    }

    @Override
    public void die() {
        for (Item i : equipment) {
            if (i != null) {
                i.drop(node.getWorldTranslation().add(0, 1, 0));
            }
        }
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewChestMessage msg = new NewChestMessage(this, node.getWorldTranslation());
        msg.setReliable(true);
        return msg;
    }

    @Override
    public float getArmorValue() {
        return 0;
    }

    @Override
    public float calculateDamage(float damage) {
        float reducedDmg = damage - getArmorValue();
        return reducedDmg > 0 ? reducedDmg : 0;
    }

    @Override
    protected final void createHitbox() {
        float hitboxWidth = 1f;
        float hitboxHeight = 1f;
        float hitboxLength = 1f;
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();

    }

    @Override
    public void setPosition(Vector3f newPos) {
        ClientGameAppState.getInstance().getGrid().remove(this);
        node.setLocalTranslation(newPos);
        ClientGameAppState.getInstance().getGrid().insert(this);
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerMain.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    enum ChestType {
        COMMON_LOOT_CHEST,
        WEAK_LOOT_CHEST,
        MEDIUM_LOOT_CHEST,
        GOOD_LOOT_CHEST
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }
    
    @Override
    public void onCollisionServer(Collidable other) {
    }

}
