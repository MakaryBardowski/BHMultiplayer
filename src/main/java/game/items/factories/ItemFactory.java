/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.factories;

import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import game.items.AmmoPack;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.HelmetTemplate;
import game.items.ItemTemplates.ItemTemplate;
import static game.items.ItemTemplates.ItemType.PISTOL;
import static game.items.ItemTemplates.ItemType.PISTOL_AMMO;
import static game.items.ItemTemplates.ItemType.SMG_AMMO;
import game.items.ItemTemplates.VestTemplate;
import game.items.ItemTemplates.BootsTemplate;
import game.items.ItemTemplates.GlovesTemplate;
import game.items.ItemTemplates.MeleeWeaponTemplate;
import game.items.ItemTemplates.RangedWeaponTemplate;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.consumable.Medpack;
import game.items.misc.Report;
import game.items.weapons.Axe;
import game.items.weapons.Grenade;
import game.items.weapons.Knife;
import game.items.weapons.LightMachineGun;
import game.items.weapons.Pistol;
import game.items.weapons.Rifle;

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
                    return createHelmet(id, (HelmetTemplate) template, droppable);
                case VEST:
                    return createVest(id, (VestTemplate) template, droppable);
                case GLOVES:
                    return createGloves(id, (GlovesTemplate) template, droppable);
                case BOOTS:
                    return createBoots(id, (BootsTemplate) template, droppable);
                case RIFLE:
                    return createRifle(id, (RangedWeaponTemplate) template, droppable);
                case PISTOL:
                    return createPistol(id, (RangedWeaponTemplate)template, droppable);
                case LMG:
                    return createLmg(id, (RangedWeaponTemplate)template, droppable);
                case GRENADE:
                    return createGrenade(id, template, droppable);
                case KNIFE:
                    return createKnife(id, (MeleeWeaponTemplate)template, droppable);
                case AXE:
                    return createAxe(id, (MeleeWeaponTemplate)template, droppable);
                case PISTOL_AMMO:
                    return createPistolAmmoPack(id, template, droppable);
                case RIFLE_AMMO:
                    return createRifleAmmoPack(id, template, droppable);
                case SMG_AMMO:
                    return createSmgAmmoPack(id, template, droppable);
                case LMG_AMMO:
                    return createLmgAmmoPack(id, template, droppable);
                case SHOTGUN_AMMO:
                    return createShotgunAmmoPack(id, template, droppable);
                case MEDPACK:
                    return createMedpack(id, template, droppable);
                case REPORT:
                    return createReport(id,template,droppable);
                default:
                    break;
            }
        }
        return null;
    }

    private Helmet createHelmet(int id, HelmetTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Helmet helmet = new Helmet(id, template, "Helmet", dropNode, droppable);
        return helmet;
    }

    private Vest createVest(int id, VestTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Vest vest = new Vest(id, template, "Vest", dropNode, droppable);
        return vest;
    }

    private Gloves createGloves(int id, GlovesTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Gloves gloves = new Gloves(id, template, "Gloves", dropNode, droppable);
        return gloves;
    }

    private Boots createBoots(int id, BootsTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Boots boots = new Boots(id, template, "Boots", dropNode, droppable);
        return boots;
    }

    private Rifle createRifle(int id, RangedWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Rifle rifle = new Rifle(id, template.getDefaultStats().getDamage(), template, "Rifle", dropNode, droppable, template.getDefaultStats().getMaxAmmo(), template.getDefaultStats().getAttacksPerSecond());
        return rifle;
    }

    private LightMachineGun createLmg(int id, RangedWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        LightMachineGun rifle = new LightMachineGun(id, template.getDefaultStats().getDamage(), template, "Lmg", dropNode, droppable, template.getDefaultStats().getMaxAmmo(), template.getDefaultStats().getAttacksPerSecond());
        return rifle;
    }

    private Item createPistol(int id, RangedWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Pistol pistol = new Pistol(id, template.getDefaultStats().getDamage(), template, "Pistol", dropNode, droppable, template.getDefaultStats().getMaxAmmo(), template.getDefaultStats().getAttacksPerSecond());
        return pistol;
    }

    private Item createGrenade(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Grenade grenade = new Grenade(id, 0f, template, "Smoke Grenade", dropNode, droppable);
        return grenade;
    }

    private Item createKnife(int id, MeleeWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Knife pistol = new Knife(id, template.getDefaultStats().getDamage(), template, "Trench Knife", dropNode, droppable, template.getDefaultStats().getAttacksPerSecond());
        return pistol;
    }

    private Item createAxe(int id, MeleeWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Axe pistol = new Axe(id, template.getDefaultStats().getDamage(), template, "Fireaxe", dropNode, droppable, template.getDefaultStats().getAttacksPerSecond());
        return pistol;
    }

    private Item createPistolAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 24;
        short maxAmmo = 24;
        AmmoPack ammoPack = new AmmoPack(id, template, "Ammo Pack", dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createRifleAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 23;
        short maxAmmo = 23;
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

    private Item createLmgAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 45;
        short maxAmmo = 45;
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

    private Item createMedpack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Medpack medpack = new Medpack(id, 0f, template, "Medpack", dropNode, droppable);
        return medpack;
    }

    private Item createReport(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Report report = new Report(id, template, "Report", dropNode, droppable);
        return report;
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
