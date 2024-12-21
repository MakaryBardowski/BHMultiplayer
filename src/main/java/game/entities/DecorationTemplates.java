/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.mobs.Mob;
import game.items.ItemTemplates;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class DecorationTemplates {

    private static final ArrayList<DecorationTemplate> templates = new ArrayList<>();

    private static final String DECO_PATH = "Models/Decorations/";

    public static final DecorationTemplate TABLE = new DecorationTemplate(DECO_PATH + "table.j3o", 1f, 0, 0.6f, 0.65f, 0.6f);
    public static final DecorationTemplate BARBED_WIRE = new DecorationTemplate(DECO_PATH + "barbedWire.j3o", 1f, 1, 1.2f, 0.6f, 0.75f);
    public static final DecorationTemplate MINE = new DecorationTemplate(DECO_PATH + "mine.j3o", 1f, 2, 0.5f, 0.3f, 0.5f);
    public static final DecorationTemplate EXIT_CAR = new DecorationTemplate(DECO_PATH + "exitCar.j3o", 1.4f, 3, 1.6f, 0.7f, 3f);
    public static final DecorationTemplate CRATE = new DecorationTemplate(DECO_PATH + "crate.j3o", 1f, 4, 0.5f, 0.3f, 0.5f);
    public static final DecorationTemplate WEAPON_RACK = new DecorationTemplate(DECO_PATH + "weaponRack.j3o", 1f, 5, 1.4f, 1.6f, 0.8f);
    public static final DecorationTemplate OFFICE_TABLE = new DecorationTemplate(DECO_PATH + "officeTable.j3o", 1f, 6, 1.3f, 0.7f, 1.1f);
    public static final DecorationTemplate SMALL_FLAT_CRATE = new DecorationTemplate(DECO_PATH + "flatChest.j3o", 1f, 7, 0.8f, 0.4f, 0.6f);

    static {
        templates.add(TABLE.templateIndex, TABLE);
        templates.add(BARBED_WIRE.templateIndex, BARBED_WIRE);
        templates.add(MINE.templateIndex, MINE);
        templates.add(EXIT_CAR.templateIndex, EXIT_CAR);
        templates.add(CRATE.templateIndex, CRATE);
        templates.add(WEAPON_RACK.templateIndex, WEAPON_RACK);
        templates.add(OFFICE_TABLE.templateIndex, OFFICE_TABLE);
        templates.add(SMALL_FLAT_CRATE.templateIndex, SMALL_FLAT_CRATE);
    }

    public static DecorationTemplate getTemplateByIndex(int index){
        return templates.get(index);
    }

    @Getter
    @AllArgsConstructor
    public static class DecorationTemplate {

        private final String modelPath;
        private final float scale;
        private final int templateIndex;
        private final float collisionShapeWidth;
        private final float collisionShapeHeight;
        private final float collisionShapeLength;

    }

}
