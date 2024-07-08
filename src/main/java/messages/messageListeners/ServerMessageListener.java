/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.messageListeners;

import client.Main;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import server.ServerMain;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;

/**
 *
 * @author 48793
 */
@Serializable
public class ServerMessageListener implements MessageListener<HostedConnection> {

    private ServerMain serverApp;
    private static final Main MAIN_APP = Main.getInstance();

    public ServerMessageListener() {
    }

    public ServerMessageListener(ServerMain s) {
        this.serverApp = s;
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
        if (msg instanceof TwoWayMessage tm) {
            tm.handleServer(serverApp);
        }
    }


    public static void enqueueExecutionServer(Runnable runnable) {
        MAIN_APP.enqueue(runnable);
    }

}
