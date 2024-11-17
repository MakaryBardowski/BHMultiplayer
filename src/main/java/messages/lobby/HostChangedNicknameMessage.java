/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.lobby;

import client.ClientGameAppState;
import client.Main;
import client.MainMenuAppState;
import client.MainMenuController;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import lombok.Setter;
import messages.TwoWayMessage;
import static messages.lobby.HostJoinedLobbyMessage.updateLobby;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class HostChangedNicknameMessage extends TwoWayMessage {

    private int connectionId;
    private String nick;


    public HostChangedNicknameMessage() {
        setReliable(true);
    }

    public HostChangedNicknameMessage(int connectionId, String nick) {
        this.connectionId = connectionId;
        this.nick = nick;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain serverMain,HostedConnection hc) {
        var server = ServerMain.getInstance().getServer();
        var newHc = server.getConnection(connectionId);
        
        newHc.setAttribute("nick", nick);
//        System.out.println("connection"+connectionId+" new name "+newHc.getAttribute("nick"));
        server.broadcast(this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        updateLobby(connectionId,nick);
    }


}
