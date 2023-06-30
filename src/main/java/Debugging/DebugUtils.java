/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Debugging;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;

/**
 *
 * @author 48793
 */
public class DebugUtils {
    
    
    public static void addArrow(Node node,AssetManager a){
          Arrow arrow = new Arrow(Vector3f.UNIT_Z.mult(7f));
        putShape(node, arrow, ColorRGBA.Red,a).setLocalTranslation(new Vector3f(0, 0.01f, 0)); //x - czerwony

    }
    
     private static Geometry putShape(Node node, Mesh shape, ColorRGBA color,AssetManager a) {
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(a, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(4);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        node.attachChild(g);
        return g;
    }
}
