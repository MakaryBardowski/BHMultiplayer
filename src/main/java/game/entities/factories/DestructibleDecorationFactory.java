/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.factories;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.BarbedWire;
import game.entities.DecorationTemplates;
import game.entities.DecorationTemplates.DecorationTemplate;
import game.entities.DestructibleDecoration;
import static game.entities.DestructibleUtils.attachDestructibleToNode;
import static game.entities.DestructibleUtils.setupModelShootability;
import static game.map.blocks.VoxelLighting.setupModelLight;

/**
 *
 * @author 48793
 */
public class DestructibleDecorationFactory {

    public static DestructibleDecoration createTable(int id, Node parentNode, Vector3f pos, AssetManager a) {
        Node node = (Node) a.loadModel(DecorationTemplates.TABLE.getModelPath());
        node.scale(DecorationTemplates.TABLE.getScale());
        DestructibleDecoration d = new DestructibleDecoration(id, "Table", node, DecorationTemplates.TABLE);
        attachDestructibleToNode(d, parentNode, pos);
        setupModelShootability(node, id);
        setupModelLight(node);
        return d;
    }

    public static DestructibleDecoration createBarbedWire(int id, Node parentNode, Vector3f pos, AssetManager a) {
        Node node = (Node) a.loadModel(DecorationTemplates.BARBED_WIRE.getModelPath());
        node.scale(DecorationTemplates.BARBED_WIRE.getScale());
        BarbedWire d = new BarbedWire(id, "Barbed Wire", node, DecorationTemplates.BARBED_WIRE);
        attachDestructibleToNode(d, parentNode, pos);
        setupModelShootability(node, id);
        setupModelLight(node);
        return d;
    }

    public static DestructibleDecoration createDecoration(int id, Node parentNode, Vector3f pos, DecorationTemplate template, AssetManager a) {
        if (template.equals(DecorationTemplates.TABLE)) {
            return createTable(id, parentNode, pos, a);
        } else if (template.equals(DecorationTemplates.BARBED_WIRE)) {
            return createBarbedWire(id, parentNode, pos, a);
        }

        return null;
    }
}
