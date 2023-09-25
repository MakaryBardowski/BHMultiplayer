/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.RectangleCollisionShape;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public abstract class Collidable extends InteractiveEntity {

    protected RectangleCollisionShape collisionShape;
    protected Node hitboxNode = new Node(); // hitbox center is at this node
    private Geometry hitboxDebug;
    public Collidable(int id, String name, Node node) {
        super(id, name, node);
        node.attachChild(hitboxNode);
    }
    
    protected void showHitboxIndicator() {
//        hitboxDebug = CollisionDebugUtils.createHitboxGeometry(collisionShape.getWidth(), collisionShape.getHeight(), collisionShape.getLength(), ColorRGBA.Green);
//        hitboxDebug.setName(""+id);
//        hitboxNode.attachChild(hitboxDebug);
    }

    protected void hideHitboxIndicator() {
        if(hitboxDebug != null){
            hitboxDebug.removeFromParent();
//        hitboxDebug.getControl(HitboxDebugControl.class).removeHitboxAndControl();
        }
    }
}
