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
public class HostJoinedGameMessage extends TwoWayMessage {

    private int connectionId;


    public HostJoinedGameMessage() {
        setReliable(true);
    }

    public HostJoinedGameMessage(int connectionId) {
        this.connectionId = connectionId;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain serverMain) {
        var server = serverMain.getServer();
        var conns = server.getConnections();
        var newHc = conns.stream().filter(a->a.getId()==connectionId).toList().get(0);
        serverMain.addPlayerToGame(newHc);

    }

    @Override
    public void handleClient(ClientGameAppState client) {
    }


}
