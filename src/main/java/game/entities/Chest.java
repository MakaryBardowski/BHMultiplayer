/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.attachDestructibleToNode;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.Damageable;
import game.entities.mobs.Mob;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.armor.Vest;
import game.items.weapons.Rifle;
import static game.map.blocks.VoxelLighting.setupModelLight;

/**
 *
 * @author 48793
 */
public class Chest extends Destructible implements Damageable {

    private Item[] drop = new Item[3];
    private boolean locked;
    private static final String WOODEN_CRATE = "Models/Chests/crate.j3o";

    public Chest(int id, String name, Node node) {
        super(id, name, node);
        locked = true;
        drop[0] = new Rifle(1, ItemTemplates.RIFLE_MANNLICHER_95);
        drop[1] = new Vest(ItemTemplates.VEST_TRENCH);
    }

    public static Chest createRandomChestClient(int id, Node parentNode, Vector3f offset, AssetManager a) {
        Node node = (Node) a.loadModel(WOODEN_CRATE);
        Chest chest = new Chest(id, "Crate", node);
        node.scale(0.8f);
        attachDestructibleToNode(chest, parentNode, offset);
        setupModelShootability(node, id);
        setupModelLight(node);
        return chest;
    }

    public static Chest createRandomChestServer(int id, Node parentNode, Vector3f offset, AssetManager a) {
        Node node = (Node) a.loadModel(WOODEN_CRATE);
        Chest chest = new Chest(id, "Crate", node);
        attachDestructibleToNode(chest, parentNode, offset);
        return chest;
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        receiveDamage(damage);
    }

    @Override
    public void onInteract() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
        for (Item i : drop) {
            if (i != null) {
                i.drop(node.getWorldTranslation().add(0, 1, 0));
            }
        }
        node.removeFromParent();
    }

    enum ChestType {
        COMMON_LOOT_CHEST,
        WEAK_LOOT_CHEST,
        MEDIUM_LOOT_CHEST,
        GOOD_LOOT_CHEST
    }

}
