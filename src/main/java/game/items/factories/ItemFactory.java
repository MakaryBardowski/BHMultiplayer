/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.factories;

import client.ClientGameAppState;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.ItemTemplates.ItemType;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.weapons.Rifle;

/**
 *
 * @author 48793
 */
public class ItemFactory {

    private AssetManager assetManager;

    public ItemFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Item createItem(int id, ItemTemplate template, boolean droppable) {
        if (null != template.getType()) {
            switch (template.getType()) {
                case HELMET:
                    return createHelmet(id, template, droppable);
                case VEST:
                    return createVest(id, template, droppable);
                case GLOVES:
                    return createGloves(id, template, droppable);
                case BOOTS:
                    return createBoots(id, template, droppable);
                case RIFLE:
                    return createRifle(id, template, droppable);
                case AXE:
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    private Helmet createHelmet(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Helmet helmet = new Helmet(id, template, "Helmet", dropNode, droppable);
        return helmet;
    }

    private Vest createVest(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Vest vest = new Vest(id, template, "Vest", dropNode, droppable);
        return vest;
    }

    private Gloves createGloves(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Gloves gloves = new Gloves(id, template, "Gloves", dropNode, droppable);
        return gloves;
    }

    private Boots createBoots(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Boots boots = new Boots(id, template, "Boots", dropNode, droppable);
        return boots;
    }

    private Rifle createRifle(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Rifle rifle = new Rifle(id, 2.6f, template, "Rifle", dropNode, droppable);
        return rifle;
    }

    /**
     *
     * @param template the visual template for the item (including its FP
     * model,drop model and drop offsets)
     * @return Node - being the drop model based on provided template
     */
    private Node createItemDropNode(ItemTemplate template) {
        if (assetManager == null) {
            return new Node();
        }
        if (template.getDropPath() != null) {
            return (Node) assetManager.loadModel(template.getDropPath());
        } else {
            return (Node) assetManager.loadModel(ItemTemplates.RIFLE_MANNLICHER_95.getDropPath());
        }
    }

}
