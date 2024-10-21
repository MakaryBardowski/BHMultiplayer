package game.items;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class ArmorItemStatTemplate {
    @Getter
    @AllArgsConstructor
    public static class ArmorStatTemplate {

        float armorValue;
    }

    public static class HelmetStatTemplate extends ArmorStatTemplate {
    public static HelmetStatTemplate DEFAULT_HEAD_DEFAULT_STATS = new HelmetStatTemplate(0f);
    public static HelmetStatTemplate TRENCH_HELMET_DEFAULT_STATS = new HelmetStatTemplate(0.5f);
    public static HelmetStatTemplate GAS_MASK_DEFAULT_STATS = new HelmetStatTemplate(0.1f);

        public HelmetStatTemplate(float armorValue) {
            super(armorValue);
        }
    }

    public static class VestStatTemplate extends ArmorStatTemplate {
    public static VestStatTemplate DEFAULT_TORSO_DEFAULT_STATS = new VestStatTemplate(0f);
    public static VestStatTemplate TRENCH_VEST_DEFAULT_STATS = new VestStatTemplate(0.75f);
        public VestStatTemplate(float armorValue) {
            super(armorValue);
        }
    }

    public static class GlovesStatTemplate extends ArmorStatTemplate {
    public static GlovesStatTemplate DEFAULT_GLOVES_DEFAULT_STATS = new GlovesStatTemplate(0f);
    public static GlovesStatTemplate TRENCH_GLOVES_DEFAULT_STATS = new GlovesStatTemplate(0.15f);

        public GlovesStatTemplate(float armorValue) {
            super(armorValue);
        }
    }

    public static class BootsStatTemplate extends ArmorStatTemplate {
    public static BootsStatTemplate DEFAULT_BOOTS_DEFAULT_STATS = new BootsStatTemplate(0f);
    public static BootsStatTemplate TRENCH_BOOTS_DEFAULT_STATS = new BootsStatTemplate(0.2f);

        public BootsStatTemplate(float armorValue) {
            super(armorValue);
        }
    }

}
