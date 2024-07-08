/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import static client.ClientGameAppState.removeEntityByIdClient;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.DecorationTemplates.DecorationTemplate;
import game.entities.mobs.Mob;
import java.util.Random;
import messages.gameSetupMessages.NextLevelMessage;
import server.ServerMain;
import static server.ServerMain.removeEntityByIdServer;

/**
 *
 * @author 48793
 */
public class LevelExit extends IndestructibleDecoration {

    private boolean activated = false; // prevents sending lots of requests to skip a couple levels before the packet arrives at server

    public LevelExit(int id, String name, Node node, DecorationTemplate template) {
        super(id, name, node, template);
    }

    @Override
    public void showHitboxIndicator() {
    }

    ;

    @Override
    public void onCollisionClient(Collidable other) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onCollisionServer(Collidable other) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void move(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onShot(Mob shooter, float damage) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onInteract() {
        if (!activated) {
            activated = true;

            var jumpToLevelMessage = new NextLevelMessage();
            ClientGameAppState.getInstance().getClient().send(jumpToLevelMessage);
        }
        System.out.println("exiting the level...");
    }

    @Override
    public void setPosition(Vector3f newPos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    
}
