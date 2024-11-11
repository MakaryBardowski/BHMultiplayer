/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import LemurGUI.LemurPlayerEquipment;
import LemurGUI.LemurPlayerHealthbar;
import client.ClientGameAppState;
import client.PlayerHUD;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.cameraAndInput.InputController;
import game.entities.mobs.Player;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class SetPlayerMessage extends TwoWayMessage {

    @Getter
    private int id;
    private float x;
    private float y;
    private float z;
    @Getter
    private String name;

    @Getter
    private int classIndex;

    public SetPlayerMessage() {
    }

    public SetPlayerMessage(int id, Vector3f pos, String name, int classIndex) {
        this.id = id;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.name = name;
        this.classIndex = classIndex;
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    @Override
    public void handleServer(ServerMain server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        addMyPlayer(this);
    }

    private void addMyPlayer(SetPlayerMessage nmsg) {
        enqueueExecution(() -> {
            createMyPlayer(nmsg);
        });
    }

    private void createMyPlayer(SetPlayerMessage nmsg) {
        Player p = registerMyPlayer(nmsg);
        ClientGameAppState.getInstance().setPlayer(p);
        placeMyPlayer(nmsg, p);
        addInputListeners();
        addPlayerHUD(p);
        p.setName(nmsg.getName());
    }

    private void placeMyPlayer(SetPlayerMessage nmsg, Player p) {
        Node playerNode = p.getNode();
        ClientGameAppState.getInstance().getEntityNode().attachChild(playerNode);
        playerNode.setCullHint(Spatial.CullHint.Always);
        p.getNode().setLocalTranslation(nmsg.getPos());
        ClientGameAppState.getInstance().getGrid().insert(p);
    }

    private void addInputListeners() {
        new InputController().createInputListeners(ClientGameAppState.getInstance());
    }

    private void addPlayerHUD(Player player) {
        var LemurPlayerHud = new LemurPlayerHealthbar(player);
        var LemurPlayerEquipment = new LemurPlayerEquipment(player);

        player.setPlayerHud(LemurPlayerHud);
        player.setPlayerEquipmentGui(LemurPlayerEquipment);
        ClientGameAppState.getInstance().getPlayer().setPlayerHud(LemurPlayerHud);
        ClientGameAppState.getInstance().getStateManager().attach(new PlayerHUD(ClientGameAppState.getInstance()));

    }

    private Player registerMyPlayer(SetPlayerMessage nmsg) {
        return ClientGameAppState.getInstance().registerPlayer(nmsg.getId(), true, classIndex);
    }

}
