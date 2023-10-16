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

    public static final ArrayList<DecorationTemplate> templates = new ArrayList<>();

    private static final String DECO_PATH = "Models/Decorations/";

    public static final DecorationTemplate TABLE = new DecorationTemplate(DECO_PATH + "table.j3o", 1f, 0, 0.6f, 0.65f, 0.6f);
    public static final DecorationTemplate BARBED_WIRE = new DecorationTemplate(DECO_PATH + "barbedWire.j3o", 1f, 1, 1.2f, 0.6f, 0.75f);

    static {
        templates.add(TABLE.templateIndex, TABLE);
        templates.add(BARBED_WIRE.templateIndex, BARBED_WIRE);

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
