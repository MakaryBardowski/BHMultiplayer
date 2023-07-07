/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Game.Mobs.MobFactory;

import Game.Mobs.Mob;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author 48793
 */
public abstract class MobFactory {

    protected AssetManager assetManager;
    protected Node mobsNode;

    protected MobFactory(AssetManager assetManager,Node mobsNode) {
        this.assetManager = assetManager;
        this.mobsNode = mobsNode;
    }

    public abstract Mob create();

    protected void setupModelLight(Node node) {
        for (Spatial c : node.getChildren()) {
            if (c != null && c instanceof Geometry) {
                Geometry g = (Geometry) c;
                Material originalMaterial = g.getMaterial();
                Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                g.setMaterial(newMaterial);

            }
        }
    }

    protected void setupModelShootability(Node node, int id) {
        node.setName("" + id);
        // bardzo wazne - KA¯DE DZIECKO NODA MUSI MIEC NAZWE ID!!!!!!!!!!
        node.getChildren().stream().forEach(x -> x.setName("" + id));
    }

    protected void attachToMobsNode(Node node, Vector3f spawnpoint) {
        mobsNode.attachChild(node);
        node.move(spawnpoint);
    }

}
