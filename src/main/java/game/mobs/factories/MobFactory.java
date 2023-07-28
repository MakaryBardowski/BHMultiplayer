/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.mobs.factories;

import game.mobs.Mob;
import client.Main;
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

    protected MobFactory(AssetManager assetManager, Node mobsNode) {
        this.assetManager = assetManager;
        this.mobsNode = mobsNode;
    }

    protected MobFactory(Node mobsNode) {
        this.assetManager = Main.getInstance().getAssetManager();
        this.mobsNode = mobsNode;
    }

    public abstract Mob createClientSide();

    public abstract Mob createServerSide();





    protected void attachToMobsNode(Mob m, Vector3f spawnpoint) {
        Node node = m.getNode();
        mobsNode.attachChild(node);
        node.move(spawnpoint);
    }

}
