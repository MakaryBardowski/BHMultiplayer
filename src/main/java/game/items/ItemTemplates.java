/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class ItemTemplates {

    private static final String ITEMS_PATH = "Models/Items/";
    private static final String ICONS_PATH = "Textures/GUI/EquipmentIcons/";
    public static final ItemTemplate RIFLE_MANNLICHER_95 = new ItemTemplate(ITEMS_PATH + "Mannlicher95/Mannlicher95FP.j3o", ICONS_PATH+"mannlicherIcon.png", ITEMS_PATH + "Mannlicher95/Mannlicher95Drop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD),1.2f));
    public static final ItemTemplate VEST_TRENCH = new ItemTemplate(ITEMS_PATH + "TrenchSet/officersCoat.j3o", ICONS_PATH+"vestTrentch.png", ITEMS_PATH+"TrenchSet/officersCoatDrop.j3o", new DropOffsetData(new Vector3f(0, 0.15f, 0), new Vector3f(-90 * FastMath.DEG_TO_RAD, 0, 0 * FastMath.DEG_TO_RAD),0.85f));
    public static final ItemTemplate BOOTS_TRENCH = new ItemTemplate(ITEMS_PATH + "TrenchSet/officersLeg?.j3o", ICONS_PATH+"bootsTrentch.png", ITEMS_PATH+"TrenchSet/officersBootsDrop.j3o", new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD),0.75f));
    public static final ItemTemplate GLOVES_TRENCH = new ItemTemplate(ITEMS_PATH + "TrenchSet/officersHand?.j3o", null, null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD),1));
    public static final ItemTemplate HEAD_1 = new ItemTemplate(ITEMS_PATH + "TrenchSet/head1.j3o", null, null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 90 * FastMath.DEG_TO_RAD),1));
    public static final ItemTemplate AXE = new ItemTemplate(ITEMS_PATH + "Axe/axe.j3o", ICONS_PATH+"axeIcon.png", null, new DropOffsetData(new Vector3f(0, 0.05f, 0), new Vector3f(0, 0, 0 * FastMath.DEG_TO_RAD),1));

    
    @Getter
    @AllArgsConstructor
    public static class ItemTemplate {

        private final String fpPath; // path to the model seen in first person
        private final String iconPath; // path to the icon seen eq
        private final String dropPath; // path to the model seen when dropped on the ground/equipped by others
        private final DropOffsetData dropData;

    }

    @Getter
    @AllArgsConstructor
    public static class DropOffsetData {

        private final Vector3f offset;
        private final Vector3f rotation;
        private final float scale;
    }

}
