/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class ItemTemplates {

    public static final ArrayList<ItemTemplate> templates = new ArrayList<>();

    private static final String ITEMS_PATH = "Models/Items/";
    private static final String ICON_PATH = "Textures/GUI/EquipmentIcons/";
    private static final String WEAPONS_PATH = ITEMS_PATH+"Weapons/";
    private static final String CONSUMABLE_PATH = ITEMS_PATH+"Consumable/";

    public static final ItemTemplate RIFLE_MANNLICHER_95 = new ItemTemplate("Mannlicher 95 [Rifle]", WEAPONS_PATH + "Mannlicher95/mannlicher95.j3o", ICON_PATH + "equipmentMannlicher95.png", WEAPONS_PATH + "Mannlicher95/Mannlicher95Drop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1.2f), ItemType.RIFLE, 0);
    public static final ItemTemplate VEST_TRENCH = new ItemTemplate("Trench Jacket [Vest]", ITEMS_PATH + "TrenchSet/officersCoat.j3o", ICON_PATH + "equipmentTrenchCoat.png", ITEMS_PATH + "TrenchSet/officersCoatDrop.j3o", new DropOffsetData(new Vector3f(0, 0.15f, 0), new Vector3f(-90 * FastMath.DEG_TO_RAD, 0, 0 * FastMath.DEG_TO_RAD), 0.85f), ItemType.VEST, 1);
    public static final ItemTemplate BOOTS_TRENCH = new ItemTemplate("Trench Boots [Boots]", ITEMS_PATH + "TrenchSet/officersLeg?.j3o", ICON_PATH + "equipmentTrenchShoes.png", ITEMS_PATH + "TrenchSet/officersBootsDrop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 0.75f), ItemType.BOOTS, 2);
    public static final ItemTemplate GLOVES_TRENCH = new ItemTemplate("Trench Gloves [Gloves]", ITEMS_PATH + "TrenchSet/officersHand?.j3o", null, null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1), ItemType.GLOVES, 3);
    public static final ItemTemplate KNIFE = new ItemTemplate("Trench Knife [Melee]", WEAPONS_PATH + "trenchKnife/trenchKnife.j3o", ICON_PATH + "equipmentTrenchKnife.png", WEAPONS_PATH + "trenchKnife/trenchKnifeDrop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(90 * FastMath.DEG_TO_RAD, 0, 90 * FastMath.DEG_TO_RAD), 1), ItemType.KNIFE, 4);

    //bare    
    public static final ItemTemplate HEAD_1 = new HelmetTemplate("", ITEMS_PATH + "TrenchSet/head1.j3o", null, null, null, ItemType.HELMET, 5,true);
    public static final ItemTemplate TORSO_1 = new ItemTemplate("", ITEMS_PATH + "Bare/torsoBare.j3o", null, null, null, ItemType.VEST, 6);
    public static final ItemTemplate LEG_1 = new ItemTemplate("", ITEMS_PATH + "Bare/leg?.j3o", null, null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 0.75f), ItemType.BOOTS, 7);
    public static final ItemTemplate HAND_1 = new ItemTemplate("", ITEMS_PATH + "Bare/hands.j3o", null, null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 0.75f), ItemType.GLOVES, 8);

    public static final ItemTemplate PISTOL_C96 = new ItemTemplate("C96 [Pistol]", WEAPONS_PATH + "C96/c96.j3o", ICON_PATH + "equipmentC96.png", WEAPONS_PATH + "C96/c96drop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1.2f), ItemType.PISTOL, 9);
    public static final ItemTemplate SMOKE_GRENADE = new ItemTemplate("Smoke Grenade [Throwable]", ITEMS_PATH + "Throwable/smokeGrenade.j3o", ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "Throwable/smokeGrenadeDrop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1.1f), ItemType.GRENADE, 10);
    public static final ItemTemplate PISTOL_AMMO_PACK = new ItemTemplate("Pistol Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/pistolAmmoPack.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f), ItemType.PISTOL_AMMO, 11);
    public static final ItemTemplate RIFLE_AMMO_PACK = new ItemTemplate("Rifle Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/rifleAmmoPack.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f), ItemType.RIFLE_AMMO, 12);
    public static final ItemTemplate SMG_AMMO_PACK = new ItemTemplate("SMG Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/pistolAmmoPack.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f), ItemType.SMG_AMMO, 13);
    public static final ItemTemplate LMG_AMMO_PACK = new ItemTemplate("LMG Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/lmgAmmoPack.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f), ItemType.LMG_AMMO, 14);
    public static final ItemTemplate SHOTGUN_AMMO_PACK = new ItemTemplate("Shotgun Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/pistolAmmoPack.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f), ItemType.SHOTGUN_AMMO, 15);
    public static final ItemTemplate LMG_HOTCHKISS = new ItemTemplate("Hotchkiss [LMG]", WEAPONS_PATH + "Hotchkiss/hotchkissFp.j3o", ICON_PATH + "equipmentHotchkiss.png", WEAPONS_PATH + "Hotchkiss/hotchkissDrop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 0.8f), ItemType.LMG, 16);
    public static final ItemTemplate TRENCH_HELMET = new HelmetTemplate("Trench Helmet", ITEMS_PATH+"TrenchSet/trenchHelmet.j3o", ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH+"TrenchSet/trenchHelmet.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 0.8f), ItemType.HELMET, 17,false);
    public static final ItemTemplate MEDPACK = new ItemTemplate("Medpack", CONSUMABLE_PATH+"medpack.j3o", ICON_PATH + "equipmentMedpack.png", CONSUMABLE_PATH+"medpack.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 0.8f), ItemType.MEDPACK, 18);

    static {
        templates.add(RIFLE_MANNLICHER_95.getTemplateIndex(), RIFLE_MANNLICHER_95);
        templates.add(VEST_TRENCH.getTemplateIndex(), VEST_TRENCH);
        templates.add(BOOTS_TRENCH.getTemplateIndex(), BOOTS_TRENCH);
        templates.add(GLOVES_TRENCH.getTemplateIndex(), GLOVES_TRENCH);
        templates.add(KNIFE.getTemplateIndex(), KNIFE);

        templates.add(HEAD_1.getTemplateIndex(), HEAD_1);
        templates.add(TORSO_1.getTemplateIndex(), TORSO_1);
        templates.add(LEG_1.getTemplateIndex(), LEG_1);
        templates.add(HAND_1.getTemplateIndex(), HAND_1);
        templates.add(PISTOL_C96.getTemplateIndex(), PISTOL_C96);
        templates.add(SMOKE_GRENADE.getTemplateIndex(), SMOKE_GRENADE);
        templates.add(PISTOL_AMMO_PACK.getTemplateIndex(), PISTOL_AMMO_PACK);
        templates.add(RIFLE_AMMO_PACK.getTemplateIndex(), RIFLE_AMMO_PACK);
        templates.add(SMG_AMMO_PACK.getTemplateIndex(), SMG_AMMO_PACK);
        templates.add(LMG_AMMO_PACK.getTemplateIndex(), LMG_AMMO_PACK);
        templates.add(SHOTGUN_AMMO_PACK.getTemplateIndex(), SHOTGUN_AMMO_PACK);
        templates.add(LMG_HOTCHKISS.getTemplateIndex(), LMG_HOTCHKISS);
        templates.add(TRENCH_HELMET.getTemplateIndex(), TRENCH_HELMET);
        templates.add(MEDPACK.getTemplateIndex(), MEDPACK);

    }

    @Getter
    @AllArgsConstructor
    public static class ItemTemplate {

        protected final String name;
        protected final String fpPath; // path to the model seen in first person
        protected final String iconPath; // path to the icon seen eq
        protected final String dropPath; // path to the model seen when dropped on the ground/equipped by others
        protected final DropOffsetData dropData;
        protected final ItemType type;
        protected final int templateIndex;

    }
    
    @Getter
    public static class HelmetTemplate extends ItemTemplate{
        private final boolean replacesHead;

        public HelmetTemplate(String name, String fpPath, String iconPath, String dropPath, DropOffsetData dropData, ItemType type, int templateIndex,boolean replacesHead) {
            super(name, fpPath, iconPath, dropPath, dropData, type, templateIndex);
            this.replacesHead = replacesHead;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class DropOffsetData {

        private final Vector3f offset;
        private final Vector3f rotation;
        private final float scale;
    }

    public enum ItemType {
        HELMET, VEST, GLOVES, BOOTS,
        RIFLE, PISTOL,LMG, KNIFE, GRENADE,
        PISTOL_AMMO, RIFLE_AMMO, SMG_AMMO, LMG_AMMO, SHOTGUN_AMMO,
        MEDPACK
    }

}
