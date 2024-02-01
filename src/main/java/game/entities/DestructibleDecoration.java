/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.DecorationTemplates.DecorationTemplate;
import static game.entities.DestructibleUtils.attachDestructibleToNode;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.Mob;
import game.items.Item;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.map.collision.RectangleAABB;
import game.map.collision.WorldGrid;
import lombok.Getter;
import messages.DestructibleDamageReceiveMessage;
import messages.NewDestructibleDecorationMessage;
import server.ServerMain;

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
        notifyServerAboutDealingDamage(damage, this);
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
    public void receiveDamage(float rawDamage) {
        health = health - rawDamage;

        if (health <= 0) {
            die();
        }
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

    public void notifyServerAboutDealingDamage(float damage, DestructibleDecoration mob) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(mob.getId(), damage);
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
    public void move(float tpf, ClientGameAppState cm) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
