/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import com.jme3.math.Vector3f;
import java.util.Random;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class MobGenerator {
    private static final Random RANDOM = new Random();
    public void spawnMobs(byte[][][] logicMap) {
        var server = ServerMain.getInstance();
        var serverLevelManager = server.getCurrentGamemode().getLevelManager();
        var blockSize = server.getBLOCK_SIZE();

        int spawnpointOffset = 5 * blockSize;
        for (int i = 0; i < 300; i++) {
            Vector3f pos = new Vector3f(RANDOM.nextInt(37 * blockSize) + blockSize, blockSize, RANDOM.nextInt(37 * blockSize) + blockSize);
//            4.5
//            if(logicMap[][][] != 0){
//            }
            serverLevelManager.registerRandomChest(pos);
        }

        for (int i = 0; i < 300; i++) {
            var playerSpawnpointOffset = new Vector3f(spawnpointOffset * 2, 0, 0);
            if (new Random().nextBoolean() == false) {
                playerSpawnpointOffset = new Vector3f(0, 0, spawnpointOffset * 2);
            }
//
            serverLevelManager.registerMob().setPositionServer(new Vector3f(RANDOM.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getX()) + blockSize, blockSize, RANDOM.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getZ()) + blockSize)
                            .addLocal(playerSpawnpointOffset)
            );
        }

        for (int i = 0; i < 300; i++) {

            var playerSpawnpointOffset = new Vector3f(spawnpointOffset, 0, 0);
            if (new Random().nextBoolean() == false) {
                playerSpawnpointOffset = new Vector3f(0, 0, spawnpointOffset);
            }

            serverLevelManager.registerRandomDestructibleDecoration(new Vector3f(RANDOM.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getX()) + blockSize, blockSize, RANDOM.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getZ()) + blockSize)
                            .addLocal(playerSpawnpointOffset)
            );
        }
    }

}
