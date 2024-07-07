/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author 48793
 */
public class ClientStoryGameManager extends ClientGameManager {

    protected long[] levelSeeds;

    @Override
    public void startGame() {
        setupLevelManager();
        initializeLevelManager();
    }

    @Override
    public void updateMainLoop(float tpf) {
        if (levelManager != null) {
            levelManager.updateLoop(tpf);
        }
    }

    private void setupLevelManager() {
        levelManager = new ClientLevelManager();
    }

    private void initializeLevelManager() {
        levelManager.initialize();
    }

}
