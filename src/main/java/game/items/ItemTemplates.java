/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import game.items.ArmorItemStatTemplate.*;
import game.items.WeaponItemStatTemplate.MeleeWeaponStatTemplate;
import game.items.WeaponItemStatTemplate.RangedWeaponStatTemplate;
import game.items.weapons.MobWeaponUsageData.MobMeleeWeaponUsageData;
import game.items.weapons.MobWeaponUsageData.MobRangedWeaponUsageData;
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
    private static final String WEAPONS_PATH = ITEMS_PATH + "Weapons/";
    private static final String CONSUMABLE_PATH = ITEMS_PATH + "Consumable/";

    public static final ItemTemplate RIFLE_MANNLICHER_95 = new RangedWeaponTemplate("Mannlicher 95 [Rifle]", WEAPONS_PATH + "Mannlicher95/mannlicher95.j3o", ICON_PATH + "equipmentMannlicher95.png", WEAPONS_PATH + "Mannlicher95/Mannlicher95Drop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1.2f),
            new ThirdPersonOffsetData(new Vector3f(-0.37f,          0.33f,    0f), new Vector3f(0 * FastMath.DEG_TO_RAD, -90 * FastMath.DEG_TO_RAD, 180 * FastMath.DEG_TO_RAD), 1),
            ItemType.RIFLE, 0,
            RangedWeaponStatTemplate.RIFLE_MANNLICHER_95_DEFAULT_STATS,
            MobRangedWeaponUsageData.RIFLE_MANNLICHER_95_MOB_USAGE_DATA
    );

    public static final ItemTemplate VEST_TRENCH = new VestTemplate("Trench Jacket [Vest]", ITEMS_PATH + "TrenchSet/officersCoat.j3o", ICON_PATH + "equipmentTrenchCoat.png", ITEMS_PATH + "TrenchSet/officersCoatDrop.j3o",
            new DropOffsetData(new Vector3f(0, 0.15f, 0), new Vector3f(-90 * FastMath.DEG_TO_RAD, 0, 0 * FastMath.DEG_TO_RAD), 0.85f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.VEST, 1,
            VestStatTemplate.TRENCH_VEST_DEFAULT_STATS
    );

    public static final ItemTemplate BOOTS_TRENCH = new BootsTemplate("Trench Boots [Boots]", ITEMS_PATH + "TrenchSet/officersLeg?.j3o", ICON_PATH + "equipmentTrenchShoes.png", ITEMS_PATH + "TrenchSet/officersBootsDrop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 0.75f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.BOOTS, 2,
            BootsStatTemplate.TRENCH_BOOTS_DEFAULT_STATS
    );

    public static final ItemTemplate GLOVES_TRENCH = new GlovesTemplate("Trench Gloves [Gloves]", ITEMS_PATH + "TrenchSet/officersHand?.j3o", null, null,
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.GLOVES, 3,
            GlovesStatTemplate.TRENCH_GLOVES_DEFAULT_STATS
    );

    public static final ItemTemplate KNIFE = new MeleeWeaponTemplate("Knife [Melee]", WEAPONS_PATH + "trenchKnife/trenchKnife.j3o", ICON_PATH + "equipmentTrenchKnife.png", WEAPONS_PATH + "trenchKnife/trenchKnifeDrop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(90 * FastMath.DEG_TO_RAD, 0, 90 * FastMath.DEG_TO_RAD), 1),
            new ThirdPersonOffsetData(new Vector3f(-0.23f,          0.25f,    0f), new Vector3f(200 * FastMath.DEG_TO_RAD, 90 * FastMath.DEG_TO_RAD, 0), 1),
            ItemType.KNIFE, 4,
            MeleeWeaponStatTemplate.KNIFE_DEFAULT_STATS,
            MobMeleeWeaponUsageData.KNIFE_DEFAULT_STATS
    );

    //bare    
    public static final ItemTemplate HEAD_1 = new HelmetTemplate("", ITEMS_PATH + "TrenchSet/head1.j3o", null, null, null,
            null,
            ItemType.HELMET, 5, true,
            HelmetStatTemplate.DEFAULT_HEAD_DEFAULT_STATS
    );

    public static final ItemTemplate TORSO_1 = new VestTemplate("", ITEMS_PATH + "Bare/torsoBare.j3o", null, null, null,
            null,
            ItemType.VEST, 6,
            VestStatTemplate.DEFAULT_TORSO_DEFAULT_STATS
    );

    public static final ItemTemplate LEG_1 = new BootsTemplate("", ITEMS_PATH + "Bare/leg?.j3o", null, null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1f),
            null,
            ItemType.BOOTS, 7,
            BootsStatTemplate.DEFAULT_BOOTS_DEFAULT_STATS
    );

    public static final ItemTemplate HAND_1 = new GlovesTemplate("", ITEMS_PATH + "Bare/hands.j3o", null, null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1f),
            null,
            ItemType.GLOVES, 8,
            GlovesStatTemplate.DEFAULT_GLOVES_DEFAULT_STATS
    );

    public static final ItemTemplate PISTOL_C96 = new RangedWeaponTemplate("C96 [Pistol]", WEAPONS_PATH + "C96/c96.j3o", ICON_PATH + "equipmentC96.png", WEAPONS_PATH + "C96/c96drop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1.2f),
            new ThirdPersonOffsetData(new Vector3f(-0.37f,          0.33f,    0f), new Vector3f(180 * FastMath.DEG_TO_RAD, 90 * FastMath.DEG_TO_RAD, 0), 1),
            ItemType.PISTOL, 9,
            RangedWeaponStatTemplate.PISTOL_C96_DEFAULT_STATS,
            MobRangedWeaponUsageData.PISTOL_C96_MOB_USAGE_DATA
    );

    public static final ItemTemplate SMOKE_GRENADE = new ItemTemplate("Smoke Grenade [Throwable]", ITEMS_PATH + "Throwable/smokeGrenade.j3o", ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "Throwable/smokeGrenadeDrop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1.1f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.GRENADE, 10);

    public static final ItemTemplate PISTOL_AMMO_PACK = new ItemTemplate("Pistol Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/pistolAmmoPack.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.PISTOL_AMMO, 11);

    public static final ItemTemplate RIFLE_AMMO_PACK = new ItemTemplate("Rifle Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/rifleAmmoPack.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.RIFLE_AMMO, 12);

    public static final ItemTemplate SMG_AMMO_PACK = new ItemTemplate("SMG Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/pistolAmmoPack.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.SMG_AMMO, 13);

    public static final ItemTemplate LMG_AMMO_PACK = new ItemTemplate("LMG Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/lmgAmmoPack.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.LMG_AMMO, 14);

    public static final ItemTemplate SHOTGUN_AMMO_PACK = new ItemTemplate("Shotgun Ammo Pack [Ammo]", null, ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "AmmoPacks/pistolAmmoPack.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 1.1f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.SHOTGUN_AMMO, 15);

    public static final ItemTemplate LMG_HOTCHKISS = new RangedWeaponTemplate("Hotchkiss [LMG]", WEAPONS_PATH + "Hotchkiss/hotchkissFp.j3o", ICON_PATH + "equipmentHotchkiss.png", WEAPONS_PATH + "Hotchkiss/hotchkissDrop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 0.8f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.LMG, 16,
            RangedWeaponStatTemplate.LMG_HOTCHKISS_DEFAULT_STATS,
            MobRangedWeaponUsageData.LMG_HOTCHKISS_MOB_USAGE_DATA
    );

    public static final ItemTemplate TRENCH_HELMET = new HelmetTemplate("Trench Helmet", ITEMS_PATH + "TrenchSet/trenchHelmet.j3o", ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "TrenchSet/trenchHelmet.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 0.8f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.HELMET, 17, false,
            HelmetStatTemplate.TRENCH_HELMET_DEFAULT_STATS
    );

    public static final ItemTemplate MEDPACK = new ItemTemplate("Medpack", CONSUMABLE_PATH + "medpack.j3o", ICON_PATH + "equipmentMedpack.png", CONSUMABLE_PATH + "medpack.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD), 0.8f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.MEDPACK, 18);

    public static final ItemTemplate GAS_MASK = new HelmetTemplate("Gas Mask", ITEMS_PATH + "Misc/gasMask.j3o", ICON_PATH + "equipmentSmokeGrenade.png", ITEMS_PATH + "Misc/gasMask.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 0.8f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.HELMET, 19, false,
            HelmetStatTemplate.GAS_MASK_DEFAULT_STATS
    );

    public static final ItemTemplate AXE = new MeleeWeaponTemplate("Fireaxe [Melee]", WEAPONS_PATH + "Fireaxe/fireaxe.j3o", ICON_PATH + "equipmentTrenchKnife.png", WEAPONS_PATH + "Fireaxe/fireaxeDrop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0 * FastMath.DEG_TO_RAD, 0, 0 * FastMath.DEG_TO_RAD), 1),
            //                                   lewo/prawo  przod/tyl   gora/dol
            new ThirdPersonOffsetData(new Vector3f(-0.22f,          0.2f,    0f), new Vector3f(-90 * FastMath.DEG_TO_RAD, 0 * FastMath.DEG_TO_RAD, -110 * FastMath.DEG_TO_RAD), 1),
            ItemType.AXE, 20,
            MeleeWeaponStatTemplate.AXE_DEFAULT_STATS,
            MobMeleeWeaponUsageData.AXE_DEFAULT_STATS
    );

    public static final ItemTemplate RIFLE_BORYSOV = new RangedWeaponTemplate("Borysov [Rifle]", WEAPONS_PATH + "borysov/borysovFP.j3o", ICON_PATH + "equipmentMannlicher95.png", WEAPONS_PATH + "borysov/borysovDrop.j3o",
            new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD), 1.2f),
            new ThirdPersonOffsetData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1),
            ItemType.RIFLE, 21,
            RangedWeaponStatTemplate.RIFLE_BORYSOV_DEFAULT_STATS,
            MobRangedWeaponUsageData.RIFLE_BORYSOV_MOB_USAGE_DATA
    );

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
        templates.add(GAS_MASK.getTemplateIndex(), GAS_MASK);
        templates.add(AXE.getTemplateIndex(), AXE);
        templates.add(RIFLE_BORYSOV.getTemplateIndex(), RIFLE_BORYSOV);

    }

    @Getter
    @AllArgsConstructor
    public static class ItemTemplate {

        protected final String name;
        protected final String fpPath; // path to the model seen in first person
        protected final String iconPath; // path to the icon seen eq
        protected final String dropPath; // path to the model seen when dropped on the ground/equipped by others
        protected final DropOffsetData dropData;
        protected final ThirdPersonOffsetData thirdPersonOffsetData;
        protected final ItemType type;
        protected final int templateIndex;

    }

    @Getter
    public static class HelmetTemplate extends ItemTemplate {

        private final HelmetStatTemplate defaultStats;
        private final boolean replacesHead;

        public HelmetTemplate(String name, String fpPath, String iconPath, String dropPath, DropOffsetData dropData, ThirdPersonOffsetData thirdPersonOffsetData, ItemType type, int templateIndex, boolean replacesHead, HelmetStatTemplate defaultStats) {
            super(name, fpPath, iconPath, dropPath, dropData, thirdPersonOffsetData, type, templateIndex);
            this.replacesHead = replacesHead;
            this.defaultStats = defaultStats;
        }
    }

    @Getter
    public static class VestTemplate extends ItemTemplate {

        private final VestStatTemplate defaultStats;

        public VestTemplate(String name, String fpPath, String iconPath, String dropPath, DropOffsetData dropData, ThirdPersonOffsetData thirdPersonOffsetData, ItemType type, int templateIndex, VestStatTemplate defaultStats) {
            super(name, fpPath, iconPath, dropPath, dropData, thirdPersonOffsetData, type, templateIndex);
            this.defaultStats = defaultStats;

        }
    }

    @Getter
    public static class GlovesTemplate extends ItemTemplate {

        private final GlovesStatTemplate defaultStats;

        public GlovesTemplate(String name, String fpPath, String iconPath, String dropPath, DropOffsetData dropData, ThirdPersonOffsetData thirdPersonOffsetData, ItemType type, int templateIndex, GlovesStatTemplate defaultStats) {
            super(name, fpPath, iconPath, dropPath, dropData, thirdPersonOffsetData, type, templateIndex);
            this.defaultStats = defaultStats;
        }
    }

    @Getter
    public static class BootsTemplate extends ItemTemplate {

        private final BootsStatTemplate defaultStats;

        public BootsTemplate(String name, String fpPath, String iconPath, String dropPath, DropOffsetData dropData, ThirdPersonOffsetData thirdPersonOffsetData, ItemType type, int templateIndex, BootsStatTemplate defaultStats) {
            super(name, fpPath, iconPath, dropPath, dropData, thirdPersonOffsetData, type, templateIndex);
            this.defaultStats = defaultStats;
        }
    }

    @Getter
    public static class RangedWeaponTemplate extends ItemTemplate {

        private final RangedWeaponStatTemplate defaultStats;
        private final MobRangedWeaponUsageData mobUsageData;

        public RangedWeaponTemplate(String name, String fpPath, String iconPath, String dropPath, DropOffsetData dropData, ThirdPersonOffsetData thirdPersonOffsetData, ItemType type, int templateIndex, RangedWeaponStatTemplate defaultStats, MobRangedWeaponUsageData mobUsageData) {
            super(name, fpPath, iconPath, dropPath, dropData, thirdPersonOffsetData, type, templateIndex);
            this.defaultStats = defaultStats;
            this.mobUsageData = mobUsageData;
        }
    }

    @Getter
    public static class MeleeWeaponTemplate extends ItemTemplate {

        private final MeleeWeaponStatTemplate defaultStats;
        private final MobMeleeWeaponUsageData mobUsageData;

        public MeleeWeaponTemplate(String name, String fpPath, String iconPath, String dropPath, DropOffsetData dropData, ThirdPersonOffsetData thirdPersonOffsetData, ItemType type, int templateIndex, MeleeWeaponStatTemplate defaultStats,MobMeleeWeaponUsageData mobUsageData) {
            super(name, fpPath, iconPath, dropPath, dropData, thirdPersonOffsetData, type, templateIndex);
            this.defaultStats = defaultStats;
            this.mobUsageData = mobUsageData;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class DropOffsetData {

        private final Vector3f offset;
        private final Vector3f rotation;
        private final float scale;
    }

    @Getter
    @AllArgsConstructor
    public static class ThirdPersonOffsetData {

        private final Vector3f offset;
        private final Vector3f rotation;
        private final float scale;
    }

    public enum ItemType {
        HELMET, VEST, GLOVES, BOOTS,
        RIFLE, PISTOL, LMG, KNIFE, AXE, GRENADE,
        PISTOL_AMMO, RIFLE_AMMO, SMG_AMMO, LMG_AMMO, SHOTGUN_AMMO,
        MEDPACK
    }

}
