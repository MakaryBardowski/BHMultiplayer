/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.collision;

import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author 48793
 */
public class CollisionDebugUtils {

    public static Geometry createHitboxGeometry(float width, float height, float length, ColorRGBA c) {
        Box box2 = new Box(width, height, length);
        Geometry red = new Geometry("Box", box2);
        Material mat2 = new Material(Main.getInstance().getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", c);
        mat2.getAdditionalRenderState().setWireframe(true);
        red.setMaterial(mat2);
        return red;
    }

    public static void setColor(Geometry g, ColorRGBA c) {
        g.getMaterial().setColor("Color", c);
    }
}
