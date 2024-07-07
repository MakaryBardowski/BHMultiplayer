/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.gameSetupMessages;

import client.ClientGameAppState;
import client.ClientGameManager;
import client.ClientStoryGameManager;
import com.jme3.network.serializing.Serializable;
import game.map.MapType;
import java.util.Arrays;
import lombok.NoArgsConstructor;
import messages.TwoWayMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */ 
@Serializable
public class NextLevelMessage extends TwoWayMessage {

    private int nextLevelIndex;

    public NextLevelMessage() {
        setReliable(true);
    }    
    
    public NextLevelMessage(int nextLevelIndex) {
        this.nextLevelIndex = nextLevelIndex;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server) {
        var levelManager = server.getCurrentGamemode().getLevelManager();
        nextLevelIndex = levelManager.getCurrentLevelIndex()+1;
        levelManager.jumpToLevel(nextLevelIndex);
        server.getServer().broadcast(this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var clientLevelManager = ClientGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        clientLevelManager.jumpToLevel(nextLevelIndex);
    }


}
