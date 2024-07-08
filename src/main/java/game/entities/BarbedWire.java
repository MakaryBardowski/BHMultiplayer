/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import game.entities.mobs.Mob;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.RectangleAABB;
import game.map.collision.RectangleOBB;
import messages.DestructibleDamageReceiveMessage;
import server.ServerMain;
import statusEffects.DamageOverTimeEffect;
import statusEffects.EffectFactory;
import statusEffects.EffectProcType;
import statusEffects.TimedSlowEffect;

/**
 *
 * @author 48793
 */
public class BarbedWire extends DestructibleDecoration {

    private float damage = 2f;

    public BarbedWire(int id, String name, Node node, DecorationTemplates.DecorationTemplate template) {
        super(id, name, node, template);
    }

    @Override
    protected void createHitbox() {
        float hitboxWidth = template.getCollisionShapeWidth();
        float hitboxHeight = template.getCollisionShapeHeight();
        float hitboxLength = template.getCollisionShapeLength();
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleOBB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength, 0);
        showHitboxIndicator();
    }

    @Override
    protected void showHitboxIndicator() {
        hitboxDebug = CollisionDebugUtils.createHitboxGeometry(collisionShape.getWidth(), collisionShape.getHeight(), collisionShape.getLength(), ColorRGBA.Red);
        hitboxDebug.setName("" + id);
        hitboxNode.attachChild(hitboxDebug);
    }

    @Override
    public void onCollisionClient(Collidable other) {

    }

    @Override
    public void onCollisionServer(Collidable other) {
        if (other instanceof Mob m) {
            DamageOverTimeEffect dot = EffectFactory.createBleedEffect(m, damage, 4, 5);
            m.addEffect(dot);

        }
    }
}
