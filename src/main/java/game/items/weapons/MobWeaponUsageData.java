/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public class MobWeaponUsageData {
    private float weaponRange;
    private float minimumRangeToUse;

    public MobWeaponUsageData(float weaponRange, float minimumRangeToUse) {
        this.weaponRange = weaponRange;
        this.minimumRangeToUse = minimumRangeToUse;
    }

    @Getter
    @Setter
    public static class MobMeleeWeaponUsageData extends MobWeaponUsageData {
        public static MobMeleeWeaponUsageData AXE_DEFAULT_STATS = new MobMeleeWeaponUsageData(0.8f, 2.3f,2f);
        public static MobMeleeWeaponUsageData KNIFE_DEFAULT_STATS = new MobMeleeWeaponUsageData(0.7f,1.45f,1.2f);
        private float cooldown;
        public MobMeleeWeaponUsageData(float weaponRange,float minimumRangeToUse,float cooldown) {
            super(weaponRange,minimumRangeToUse);
            this.cooldown = cooldown;
        }
    }

    @Getter
    @Setter
    public static class MobRangedWeaponUsageData extends MobWeaponUsageData {
        public static MobRangedWeaponUsageData RIFLE_MANNLICHER_95_MOB_USAGE_DATA = new MobRangedWeaponUsageData(20f,20f);
        public static MobRangedWeaponUsageData PISTOL_C96_MOB_USAGE_DATA = new MobRangedWeaponUsageData(20f,20f);
        public static MobRangedWeaponUsageData RIFLE_BORYSOV_MOB_USAGE_DATA = new MobRangedWeaponUsageData(20f,20f);
        public static MobRangedWeaponUsageData LMG_HOTCHKISS_MOB_USAGE_DATA = new MobRangedWeaponUsageData(20f,20f);

        public MobRangedWeaponUsageData(float weaponRange,float minimumRangeToUse) {
            super(weaponRange,minimumRangeToUse);
        }
    }
}
