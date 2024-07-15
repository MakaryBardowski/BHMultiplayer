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
import static game.entities.DestructibleUtils.attachNodeToNode;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.IndestructibleDecoration;
import game.entities.LevelExit;
import game.entities.Mine;
import static game.map.blocks.VoxelLighting.setupModelLight;

/**
 *
 * @author 48793
 */
public class DecorationFactory {

    public static DestructibleDecoration createTable(int id, Node parentNode, Vector3f pos, AssetManager a) {
        Node node = (Node) a.loadModel(DecorationTemplates.TABLE.getModelPath());
        node.scale(DecorationTemplates.TABLE.getScale());
        DestructibleDecoration d = new DestructibleDecoration(id, "Table " + id, node, DecorationTemplates.TABLE);
        attachDestructibleToNode(d, parentNode, pos);
        setupModelShootability(node, id);
        setupModelLight(node);
        return d;
    }

    public static DestructibleDecoration createBarbedWire(int id, Node parentNode, Vector3f pos, AssetManager a) {
        DecorationTemplate template = DecorationTemplates.BARBED_WIRE;
        Node node = (Node) a.loadModel(template.getModelPath());
        node.scale(template.getScale());
        BarbedWire d = new BarbedWire(id, "Barbed Wire " + id, node, template);
        attachDestructibleToNode(d, parentNode, pos);
        setupModelShootability(node, id);
        setupModelLight(node);
        return d;
    }

    public static DestructibleDecoration createMine(int id, Node parentNode, Vector3f pos, AssetManager a) {
        DecorationTemplate template = DecorationTemplates.MINE;
        Node node = (Node) a.loadModel(template.getModelPath());
        node.scale(template.getScale());
        Mine d = new Mine(id, "Landmine " + id, node, template);
        attachDestructibleToNode(d, parentNode, pos);
        setupModelShootability(node, id);
        setupModelLight(node);
        return d;
    }

    public static DestructibleDecoration createCrate(int id, Node parentNode, Vector3f pos, AssetManager a) {
        DecorationTemplate template = DecorationTemplates.CRATE;
        Node node = (Node) a.loadModel(template.getModelPath());
        node.scale(template.getScale());
        Mine d = new Mine(id, "Landmine " + id, node, template);
        attachDestructibleToNode(d, parentNode, pos);
        setupModelShootability(node, id);
        setupModelLight(node);
        return d;
    }

    public static DestructibleDecoration createDestructibleDecoration(int id, Node parentNode, Vector3f pos, DecorationTemplate template, AssetManager a) {
        if (template.equals(DecorationTemplates.TABLE)) {
            return createTable(id, parentNode, pos, a);
        } else if (template.equals(DecorationTemplates.BARBED_WIRE)) {
            return createBarbedWire(id, parentNode, pos, a);
        } else if (template.equals(DecorationTemplates.MINE)) {
            return createMine(id, parentNode, pos, a);
        } else if (template.equals(DecorationTemplates.CRATE)) {
            return createCrate(id, parentNode, pos, a);
        }

        return null;
    }

    // indestructible
    public static IndestructibleDecoration createExit(int id, Node parentNode, Vector3f pos, AssetManager a) {
        Node node = (Node) a.loadModel(DecorationTemplates.EXIT_CAR.getModelPath());
        node.scale(DecorationTemplates.EXIT_CAR.getScale());
        var d = new LevelExit(id, "Car " + id, node, DecorationTemplates.EXIT_CAR);
        attachNodeToNode(node, parentNode, pos);
        setupModelShootability(node, id);
        setupModelLight(node);
        return d;
    }

    public static IndestructibleDecoration createIndestructibleDecoration(int id, Node parentNode, Vector3f pos, DecorationTemplate template, AssetManager a) {
        if (template.equals(DecorationTemplates.EXIT_CAR)) {
            return createExit(id, parentNode, pos, a);
        }

        return null;
    }
}
