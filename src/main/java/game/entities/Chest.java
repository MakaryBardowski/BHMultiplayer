/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import static client.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import data.DamageReceiveData;
import game.entities.DecorationTemplates.DecorationTemplate;
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
import static server.ServerMain.removeEntityByIdServer;

/**
 *
 * @author 48793
 */
public class Chest extends Destructible {

    static int cnt;

    @Getter
    private final Item[] equipment = new Item[9];
    private boolean locked;

    private static final DecorationTemplate TEMPLATE = DecorationTemplates.CRATE;

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
        Node node = (Node) a.loadModel(TEMPLATE.getModelPath());
        Chest chest = new Chest(id, "Crate " + id, node);
        node.scale(0.8f);
        chest.hitboxNode.scale(1.25f);
        attachDestructibleToNode(chest, parentNode, offset);
        setupModelShootability(node, id);
        setupModelLight(node);
        return chest;
    }

    public static Chest createRandomChestServer(int id, Node parentNode, Vector3f offset, AssetManager a) {
        Node node = (Node) a.loadModel(TEMPLATE.getModelPath());
        Chest chest = new Chest(id, "Crate " + id, node);
        attachDestructibleToNode(chest, parentNode, offset);
        return chest;
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        notifyServerAboutDealingDamage(damage, shooter);
    }

    @Override
    public void onInteract() {
        System.out.println("This " + name + " might contain valuable loot.");
    }

    @Override
    public void receiveDamage(DamageReceiveData damageData) {
        health = health - damageData.getRawDamage();

        if (health <= 0) {
            die();
            destroyClient();
            onDeathClient();
        }
    }

    @Override
    public void receiveDamageServer(DamageReceiveData damageData) {
        health = health - damageData.getRawDamage();

        if (health <= 0) {
            destroyServer();
            onDeathServer();
        }
    }

    @Override
    public void die() {
        for (int i = 0; i < equipment.length; i++) {
            Item item = equipment[i];
            if (item != null) {
                item.drop(node.getWorldTranslation().add(0, 1, 0));
            }
            equipment[i] = null;
        }
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
        destroyClient();
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
        float hitboxWidth = 0.8f;
        float hitboxHeight = 0.8f;
        float hitboxLength = 0.8f;
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

    @Override
    public void move(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void notifyServerAboutDealingDamage(float damage, InteractiveEntity attackingEntity) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(id, attackingEntity.id, damage);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
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

    @Override
    public void destroyServer() {
        var server = ServerMain.getInstance();
        server.getGrid().remove(this);
        if (node.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                node.removeFromParent();
            });
        }
        removeEntityByIdServer(id);
    }

    @Override
    public void destroyClient() {
        var client = ClientGameAppState.getInstance();
        client.getGrid().remove(this);
        Main.getInstance().enqueue(() -> {
            node.removeFromParent();
        });
        removeEntityByIdClient(id);
    }

}
