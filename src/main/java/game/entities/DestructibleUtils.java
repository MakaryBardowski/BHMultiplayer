/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.mobs.Mob;

/**
 *
 * @author 48793
 */
public class DestructibleUtils {

    public static void attachDestructibleToNode(Destructible m, Node parent, Vector3f spawnpoint) {
        Node node = m.getNode();
        parent.attachChild(node);
        node.move(spawnpoint);
    }
    
    public static void attachNodeToNode(Node child, Node parent, Vector3f spawnpoint) {
        parent.attachChild(child);
        child.move(spawnpoint);
    }

    public static void setupModelShootability(Node node, int id) {
        node.setName("" + id);
        for (Spatial spatial : node.getChildren()) {
            if (spatial != null && spatial instanceof Node n) {
                setupModelShootability(n, id);
            } else {
                spatial.setName("" + id);
            }
        }
    }
}
