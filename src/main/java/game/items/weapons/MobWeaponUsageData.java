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

    private float minimumRangeToUse;

    public MobWeaponUsageData(float minimumRangeToUse) {
        this.minimumRangeToUse = minimumRangeToUse;
    }

    @Getter
    @Setter
    public static class MobMeleeWeaponUsageData extends MobWeaponUsageData {

        public MobMeleeWeaponUsageData(float minimumRangeToUse) {
            super(minimumRangeToUse);
        }
    }

    @Getter
    @Setter
    public static class MobRangedWeaponUsageData extends MobWeaponUsageData {

        public MobRangedWeaponUsageData(float minimumRangeToUse) {
            super(minimumRangeToUse);
        }
    }
}
