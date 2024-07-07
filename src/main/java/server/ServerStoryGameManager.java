/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import messages.gameSetupMessages.GameInfoOnStartMessage;

/**
 *
 * @author 48793
 */
public class ServerStoryGameManager extends ServerGameManager {

    private final int LEVEL_COUNT = 6;

    public ServerStoryGameManager() {
        gamemodeId = 1;
    }

    @Override
    public void startGame() {
        setupLevelManager();
        transmitSeedsToConnectedClients();
        levelManager.jumpToLevel(0);
    }

    @Override
    public void updateMainLoop(float tpf) {}

    private void transmitSeedsToConnectedClients() {
        var server = ServerMain.getInstance().getServer();
        var levelSeeds = levelManager.getLevelSeeds();
        var levelTypes = levelManager.getLevelTypes();
        var levelSeedsMessage = new GameInfoOnStartMessage(gamemodeId, levelSeeds,levelTypes);
        server.broadcast(levelSeedsMessage);
    }

    private void setupLevelManager() {
        levelManager = new ServerLevelManager(LEVEL_COUNT, ServerMain.getInstance().getServer());
        levelManager.setupLevelSeeds();
    }

}
