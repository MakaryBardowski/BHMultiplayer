/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items;

/**
 *
 * @author 48793
 */
public class ItemTemplates {

    public static final ItemTemplate RIFLE_MANNLICHER_95 = new ItemTemplate("Models/testRifleFP/testRifleFP.j3o", null, null);
    public static final ItemTemplate VEST_TRENCH = new ItemTemplate("Models/officers/officersCoat.j3o", null, null);
    public static final ItemTemplate BOOTS_TRENCH = new ItemTemplate("Models/officers/officersLeg?.j3o", null, null);
    public static final ItemTemplate GLOVES_TRENCH = new ItemTemplate("Models/officers/officersHand?.j3o", null, null);
    public static final ItemTemplate HEAD_1 = new ItemTemplate("Models/officers/head1.j3o", null, null);

    public static class ItemTemplate {

        private final String fpPath; // path to the model seen in first person
        private final String iconPath; // path to the icon seen eq
        private final String dropPath; // path to the model seen when dropped on the ground/equipped by others

        public ItemTemplate(String fpPath, String iconPath, String dropPath) {
            this.fpPath = fpPath;
            this.iconPath = iconPath;
            this.dropPath = dropPath;
        }

        public String getFpPath() {
            return fpPath;
        }

        public String getIconPath() {
            return iconPath;
        }

        public String getDropPath() {
            return dropPath;
        }

    }

}
