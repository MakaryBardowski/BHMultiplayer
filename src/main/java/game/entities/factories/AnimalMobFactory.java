/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities.factories;

import game.entities.mobs.Mob;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.DestructibleUtils;
import static game.entities.factories.MobSpawnType.MUD_BEETLE;
import game.entities.mobs.MudBeetle;
import game.entities.mobs.Player;

/**
 *
 * @author 48793
 */
public class AnimalMobFactory extends MobFactory {

    public AnimalMobFactory(int id, AssetManager assetManager, Node mobNode) {
        super(id, assetManager, mobNode);
    }

    public AnimalMobFactory(int id, Node mobNode) {
        super(id, mobNode);

    }

    @Override
    public Mob createClientSide(MobSpawnType spawnType) {
      if(spawnType == MUD_BEETLE){
        MudBeetle p = createMudBeetle();
        return p;
        }
        return null;
    }

    @Override
    public Mob createServerSide(MobSpawnType spawnType) {
        if(spawnType == MUD_BEETLE){
        MudBeetle p = createMudBeetle();
        DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));
        return p;
        }
        return null;
    }

    private MudBeetle createMudBeetle() {
        Node playerNode = loadBeetleModel();
        String name = "Mud Beetle " + id;
        SkinningControl skinningControl = getSkinningControl(playerNode);
        AnimComposer composer = getAnimComposer(playerNode);
        System.out.println("[AnimalMobFactory] create mud beetle id " + id);
        return new MudBeetle(id, playerNode, name, skinningControl, composer);

    }
    
    
    private Node loadBeetleModel() {
        Node node = (Node) assetManager.loadModel("Models/Mobs/MudBeetle.j3o");
        return node;
    }

    private SkinningControl getSkinningControl(Node node) {
        return node.getChild(0).getControl(SkinningControl.class);
    }

    private AnimComposer getAnimComposer(Node node) {
        return node.getChild(0).getControl(AnimComposer.class);
    }

}
