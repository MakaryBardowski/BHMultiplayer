/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public abstract class TwoWayMessage extends AbstractMessage {

    public abstract void handleServer(ServerMain server);

    public abstract void handleClient(ClientGameAppState client);
    
}
