/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.lobby;

import client.ClientGameAppState;
import client.LobbyTeamViewAppState;
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
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class HostJoinedLobbyMessage extends TwoWayMessage {

    private String nick;
    private int connectionId;

    @Setter
    private boolean host;

    public HostJoinedLobbyMessage() {
        setReliable(true);
    }

    public HostJoinedLobbyMessage(int connectionId, String nick) {
        this.connectionId = connectionId;
        this.nick = nick;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain serverMain,HostedConnection senderHc) {
        var server = ServerMain.getInstance().getServer();
        var newHc = server.getConnection(connectionId);
        if (server.getConnections().size() >= 5) {
            newHc.close("Lobby is full.");
        }

        if (server.getConnections().size() == 1) {
            newHc.setAttribute("isHost", true);
            setHost(true);
        } else {
            newHc.setAttribute("isHost", false);
        }
        newHc.setAttribute("nick", nick);

        server.broadcast(Filters.notEqualTo(newHc), this);

        server.getConnections().forEach(hc -> {
            String playerNick = (String) hc.getAttribute("nick");
//            System.out.println("sending "+connectionId+" nick => "+playerNick);
            var msg = new HostJoinedLobbyMessage(hc.getId(), playerNick);
            boolean isHost = (boolean) hc.getAttribute("isHost");
            msg.setHost(isHost);
            server.broadcast(Filters.in(newHc), msg);
        });
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        updateLobby(connectionId, nick);
    }

    public static void updateLobby(int connectionId, String nick) {
        var nifty = MainMenuAppState.getNifty();
        Label textField = nifty.getCurrentScreen().findNiftyControl("playerSlot" + connectionId, Label.class);
        if (textField == null) {
            return;
        }
        textField.setText(nick);

        if (connectionId == MainMenuAppState.getClient().getClient().getId()) {
            LobbyTeamViewAppState.setCurrentNickname(nick);
        }
    }

}
