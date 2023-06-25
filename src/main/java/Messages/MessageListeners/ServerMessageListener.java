/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Messages.MessageListeners;

import Messages.MobHealthUpdateMessage;
import Messages.MobUpdateMessage;
import Messages.MobUpdatePosRotMessage;
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
    
    public ServerMessageListener(){};
    public ServerMessageListener(ServerMain s){
    this.serverApp = s;
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
                if(msg instanceof MobUpdatePosRotMessage nmsg){
                validateMovement();
                Vector3f newPos = new Vector3f(nmsg.getX(), nmsg.getY(), nmsg.getZ());
                serverApp.getMobs().get( nmsg.getId() ).getNode().setLocalTranslation(newPos);
                } else if(msg instanceof MobHealthUpdateMessage hmsg){
                serverApp.getMobs().get(hmsg.getId()).setHealth(hmsg.getHealth());
                }
    }


  private void validateMovement(){
  // validate
  }



}
