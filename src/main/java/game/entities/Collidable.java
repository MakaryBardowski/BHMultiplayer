/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.CollisionShape;
import game.map.collision.RectangleAABB;
import game.map.collision.RectangleOBB;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public abstract class Collidable extends InteractiveEntity {

    protected CollisionShape collisionShape;
    protected Node hitboxNode = new Node(); // hitbox center is at this node
    protected Geometry hitboxDebug;

    public Collidable(int id, String name, Node node) {
        super(id, name, node);
        node.attachChild(hitboxNode);
    }

    public abstract void onCollisionClient(Collidable other);

    public abstract void onCollisionServer(Collidable other);

    protected void showHitboxIndicator() {
        hitboxDebug = CollisionDebugUtils.createHitboxGeometry(collisionShape.getWidth(), collisionShape.getHeight(), collisionShape.getLength(), ColorRGBA.Green);
        hitboxDebug.setName("" + id);
        hitboxNode.attachChild(hitboxDebug);
    }

    protected void hideHitboxIndicator() {
        if (hitboxDebug != null) {
            hitboxDebug.removeFromParent();
        }
    }

    public static boolean isNotCollisionShapePassable(Collidable c) {
        return c.getCollisionShape() instanceof RectangleAABB;
    }

    public static boolean isCollisionShapePassable(Collidable c) {
        return c.getCollisionShape() instanceof RectangleOBB;
    }
}
