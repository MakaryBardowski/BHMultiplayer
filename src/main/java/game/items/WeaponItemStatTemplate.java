package game.items;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class WeaponItemStatTemplate {

    @Getter
    public static class WeaponStatTemplate {

        private final float damage;
        private final float attacksPerSecond;

        public WeaponStatTemplate(float damage, float attacksPerSecond) {
            this.damage = damage;
            this.attacksPerSecond = attacksPerSecond;
        }
    }

    @Getter
    public static class MeleeWeaponStatTemplate extends WeaponStatTemplate {

        public static MeleeWeaponStatTemplate AXE_DEFAULT_STATS = new MeleeWeaponStatTemplate(8f,1.25f);
        public static MeleeWeaponStatTemplate KNIFE_DEFAULT_STATS = new MeleeWeaponStatTemplate(6f,2f);

        public MeleeWeaponStatTemplate(float damage, float attacksPerSecond) {
            super(damage, attacksPerSecond);
        }
    }

    @Getter
    public static class RangedWeaponStatTemplate extends WeaponStatTemplate {
        public static RangedWeaponStatTemplate RIFLE_MANNLICHER_95_DEFAULT_STATS = new RangedWeaponStatTemplate(5f,1.5f,8);
        public static RangedWeaponStatTemplate PISTOL_C96_DEFAULT_STATS = new RangedWeaponStatTemplate(4.5f,4.43f,10);
        public static RangedWeaponStatTemplate RIFLE_BORYSOV_DEFAULT_STATS = new RangedWeaponStatTemplate(3.6f,6f,24);
        public static RangedWeaponStatTemplate LMG_HOTCHKISS_DEFAULT_STATS = new RangedWeaponStatTemplate(3.6f,7.5f,60);

        private final int maxAmmo;

        public RangedWeaponStatTemplate(float damage, float attacksPerSecond, int maxAmmo) {
            super(damage, attacksPerSecond);
            this.maxAmmo = maxAmmo;
        }
    }
}
