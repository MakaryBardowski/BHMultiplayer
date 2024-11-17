/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import static client.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import data.DamageReceiveData;
import events.DamageReceivedEvent;
import game.entities.DecorationTemplates.DecorationTemplate;
import static game.entities.DestructibleUtils.attachDestructibleToNode;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.Mob;
import game.items.Item;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.map.collision.RectangleAABB;
import game.map.collision.WorldGrid;
import lombok.Getter;
import messages.DeleteEntityMessage;
import messages.DestructibleDamageReceiveMessage;
import messages.NewDestructibleDecorationMessage;
import server.ServerMain;
import static server.ServerMain.removeEntityByIdServer;

/**
 *
 * @author 48793
 */
public class DestructibleDecoration extends Destructible {

    @Getter
    protected DecorationTemplate template;

    public DestructibleDecoration(int id, String name, Node node, DecorationTemplate template) {
        super(id, name, node);
        this.template = template;
        createHitbox();
    }

    @Override
    protected void createHitbox() {
        float hitboxWidth = template.getCollisionShapeWidth();
        float hitboxHeight = template.getCollisionShapeHeight();
        float hitboxLength = template.getCollisionShapeLength();
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        notifyServerAboutDealingDamage(damage, shooter);
    }

    @Override
    public void onInteract() {
    }

    @Override
    public void setPosition(Vector3f newPos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerMain.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewDestructibleDecorationMessage msg = new NewDestructibleDecorationMessage(this);
        msg.setReliable(true);
        return msg;
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
            System.out.println("\n\n\n\n mine died");
            destroyServer();
            onDeathServer();
            return;
        }
        Main.getInstance().enqueue(() -> {
            notifyEventSubscribers(new DamageReceivedEvent(damageData));
        }
        );
    }

    @Override
    public void die() {
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
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
    public void notifyServerAboutDealingDamage(float damage, InteractiveEntity attacker) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(id, attacker.id, damage);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(Collidable other) {
    }

    @Override
    public void move(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
