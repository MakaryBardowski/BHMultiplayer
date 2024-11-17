/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.gameSetupMessages;

import client.ClientGameAppState;
import client.ClientGameManager;
import client.ClientStoryGameManager;
import com.jme3.network.HostedConnection;
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
@NoArgsConstructor
@Serializable
public class GameInfoOnStartMessage extends TwoWayMessage {

    private static String CLIENT_INVALID_GAMEMODE_MESSAGE = "There is no gamemode associated with id: ";
    private int gamemodeId;
    private long[] mapSeeds;
    private MapType[] mapTypes;

    public GameInfoOnStartMessage(int gamemodeId, long[] mapSeeds, MapType[] mapTypes) {
        this.gamemodeId = gamemodeId;
        this.mapSeeds = mapSeeds;
        this.mapTypes = mapTypes;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var clientLevelManager = ClientGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        clientLevelManager.setLevelSeeds(mapSeeds);
        clientLevelManager.setLevelTypes(mapTypes);
        clientLevelManager.jumpToLevel(0);
    }


}   
