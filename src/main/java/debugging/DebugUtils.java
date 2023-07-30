/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package debugging;

import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;

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
     
     public static Node createUnshadedBoxNode(){
                  Box box2 = new Box(0.2f,0.2f,0.2f);
        Geometry red = new Geometry("Box", box2);
        Material mat2 = new Material(Main.getInstance().getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);
        Node n = new Node();
        n.attachChild(red);
        return n;
     }
     
     
}
