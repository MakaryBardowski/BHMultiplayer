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
import static game.items.ItemTemplates.ItemType.PISTOL;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.weapons.Grenade;
import game.items.weapons.Knife;
import game.items.weapons.Pistol;
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
                case PISTOL:
                    return createPistol(id, template, droppable);
                case GRENADE:
                    return createGrenade(id, template, droppable);

                case KNIFE:
                    return createKnife(id, template, droppable);
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
        int maxAmmo = 5;
        float roundsPerSecond = 1.1f;
        Rifle rifle = new Rifle(id, 8.25f, template, "Rifle", dropNode, droppable, maxAmmo, roundsPerSecond);
        return rifle;
    }

    private Item createPistol(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        int maxAmmo = 5;
        float roundsPerSecond = 4.43f;
        Pistol pistol = new Pistol(id, 3.3f, template, "Pistol", dropNode, droppable, maxAmmo, roundsPerSecond);
        return pistol;
    }

    private Item createGrenade(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Grenade pistol = new Grenade(id, 0f, template, "Smoke Grenade", dropNode, droppable);
        return pistol;
    }

    private Item createKnife(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        float attacksPerSec = 2;
        Knife pistol = new Knife(id, 6f, template, "Smoke Grenade", dropNode, droppable,1,attacksPerSec);
        return pistol;
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
