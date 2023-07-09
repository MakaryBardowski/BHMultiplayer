/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs.MobFactory;

import Game.Mobs.Player;
import com.Networking.Client.ClientGameAppState;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author 48793
 */
public class PlayerFactory extends MobFactory {

    private final int mobId;
    private Camera firstPersonCamera;

    public PlayerFactory(int id, AssetManager assetManager, Node mobNode, Camera mainCamera) {
        super(assetManager, mobNode);
        this.firstPersonCamera = mainCamera.clone();
        this.mobId = id;
    }

    @Override
    public Player create() {
        Node playerNode = loadPlayerModel();
        setupModelLight(playerNode);
        setupModelShootability(playerNode, mobId);

        String name = "Gracz_" + mobId;
        Player p = new Player(mobId, playerNode, name);

        Vector3f playerSpawnpoint = new Vector3f(0, 5, 0);
        attachToMobsNode(playerNode, playerSpawnpoint);
        Debugging.DebugUtils.addArrow(playerNode, assetManager);

        return p;
    }

    private Node loadPlayerModel() {
        return (Node) assetManager.loadModel("Models/testHuman/testHuman.j3o");
    }

}
