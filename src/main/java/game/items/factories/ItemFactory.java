/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.factories;

import client.ClientGameAppState;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import game.items.AmmoPack;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.ItemTemplates.ItemType;
import static game.items.ItemTemplates.ItemType.PISTOL;
import static game.items.ItemTemplates.ItemType.PISTOL_AMMO;
import static game.items.ItemTemplates.ItemType.SMG_AMMO;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.weapons.Grenade;
import game.items.weapons.Knife;
import game.items.weapons.Pistol;
import game.items.weapons.Rifle;
import org.w3c.dom.Text;

/**
 *
 * @author 48793
 */
public class ItemFactory {

    private AssetManager assetManager;

    public ItemFactory() {
        this.assetManager = Main.getInstance().getAssetManager();
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
                case PISTOL_AMMO:
                    return createPistolAmmoPack(id, template, droppable);
                case RIFLE_AMMO:
                    System.out.println("rifle!!!!");
                    return createRifleAmmoPack(id, template, droppable);
                case SMG_AMMO:
                    return createSmgAmmoPack(id, template, droppable);
                case SNIPER_AMMO:
                    return createSniperAmmoPack(id, template, droppable);
                case SHOTGUN_AMMO:
                    return createShotgunAmmoPack(id, template, droppable);
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
        int maxAmmo = 7;
        float roundsPerSecond = 4.43f;
        Pistol pistol = new Pistol(id, 2.95f, template, "Pistol", dropNode, droppable, maxAmmo, roundsPerSecond);
        return pistol;
    }

    private Item createGrenade(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Grenade grenade = new Grenade(id, 0f, template, "Smoke Grenade", dropNode, droppable);
        return grenade;
    }

    private Item createKnife(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        float attacksPerSec = 2;
        Knife pistol = new Knife(id, 6f, template, "Trench Knife", dropNode, droppable, attacksPerSec);
        return pistol;
    }

    private Item createPistolAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 14;
        short maxAmmo = 14;
        AmmoPack ammoPack = new AmmoPack(id, template, "Ammo Pack", dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createRifleAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 17;
        short maxAmmo = 17;
        AmmoPack ammoPack = new AmmoPack(id, template, "Ammo Pack", dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createSmgAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 24;
        short maxAmmo = 24;
        AmmoPack ammoPack = new AmmoPack(id, template, "Ammo Pack", dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createSniperAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 7;
        short maxAmmo = 7;
        AmmoPack ammoPack = new AmmoPack(id, template, "Ammo Pack", dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createShotgunAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 9;
        short maxAmmo = 9;
        AmmoPack ammoPack = new AmmoPack(id, template, "Ammo Pack", dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
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
            Node dropNode = (Node) assetManager.loadModel(template.getDropPath());
            return dropNode;
        } else {
            Node dropNode = (Node) assetManager.loadModel(ItemTemplates.RIFLE_MANNLICHER_95.getDropPath());
            return dropNode;
        }
    }

}
