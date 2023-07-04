/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Messages.MessageListeners;

import Messages.MobHealthUpdateMessage;
import Messages.MobUpdateMessage;
import Messages.MobPosUpdateMessage;
import Messages.MobRotUpdateMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.Networking.Client.ClientMain;
import com.Networking.Server.ServerMain;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author 48793
 */
@Serializable
public class ServerMessageListener implements MessageListener<HostedConnection> {

    private ServerMain serverApp;

    public ServerMessageListener() {
    }

    ;
    public ServerMessageListener(ServerMain s) {
        this.serverApp = s;
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
        if (msg instanceof MobRotUpdateMessage nmsg) {
            serverApp.getMobs().get(nmsg.getId()).getNode().setLocalRotation(nmsg.getRot());

        } else if (msg instanceof MobPosUpdateMessage nmsg) {
            validateMovement();
            serverApp.getMobs().get(nmsg.getId()).getNode().setLocalTranslation(nmsg.getPos());

        } else if (msg instanceof MobHealthUpdateMessage hmsg) {
            serverApp.getMobs().get(hmsg.getId()).setHealth(hmsg.getHealth());
        }
    }

    private void validateMovement() {
        // validate
    }

}
