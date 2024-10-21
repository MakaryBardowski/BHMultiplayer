/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import Utils.GridUtils;
import client.ClientGameAppState;
import com.jme3.math.Vector3f;
import game.entities.factories.MobSpawnType;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import game.entities.mobs.Player;
import java.util.Random;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class MobGenerator {

    private final Random random;
    private final int levelIndex;

    public MobGenerator(long seed, int levelIndex) {
        random = new Random(seed);
        this.levelIndex = levelIndex;
    }

    public void spawnMobs(byte[][][] logicMap) {
        if (levelIndex == 0) {
            spawnArmoryMobs(logicMap);
        }

        if (levelIndex != 0) {
            spawnRandomMobs(logicMap);
        }
    }

    public void spawnArmoryMobs(byte[][][] logicMap) {
        var server = ServerMain.getInstance();
        var serverLevelManager = server.getCurrentGamemode().getLevelManager();
        var blockSize = server.getBLOCK_SIZE();

        serverLevelManager.registerMob(MobSpawnType.TRAINING_DUMMY)
                .setPositionServer(
                        new Vector3f(
                                5 * blockSize + blockSize,
                                blockSize,
                                5 * blockSize + blockSize
                        )
                );

    }

    public void spawnRandomMobs(byte[][][] logicMap) {
        var server = ServerMain.getInstance();
        var serverLevelManager = server.getCurrentGamemode().getLevelManager();
        var blockSize = server.getBLOCK_SIZE();

//        for (int i = 0; i < 50; i++) {
//            Vector3f pos = new Vector3f(random.nextInt(37 * blockSize) + blockSize, blockSize, random.nextInt(37 * blockSize) + blockSize);
////            4.5
////            if(logicMap[][][] != 0){
////            }
//            serverLevelManager.registerRandomChest(pos);
//        }



//if(levelIndex == 1){
        for (int i = 0; i < 30; i++) {
            var mobPos = new Vector3f(random.nextInt(37 * blockSize) + 0.5f * blockSize, blockSize, random.nextInt(37 * blockSize) + 0.5f * blockSize);
            while (!GridUtils.isSpotEmpty(mobPos,logicMap)) {
                mobPos = new Vector3f(random.nextInt(37 * blockSize) + 0.5f * blockSize, blockSize, random.nextInt(37 * blockSize) + 0.5f * blockSize);
            }

//            var randomNumber = random.nextInt(5);
//            if (randomNumber < 4) {
//                MudBeetle mob = (MudBeetle) serverLevelManager.registerMob(MobSpawnType.MUD_BEETLE);
//                mob.addAi();
//                mob.setPositionServer(mobPos);
//            } else {
                HumanMob mob = (HumanMob) serverLevelManager.registerMob(MobSpawnType.HUMAN);
                mob.addAi();
                mob.setPositionServer(mobPos);
            }
//        }
//}


//        for (int i = 0; i < 50; i++) {
//
//            var playerSpawnpointOffset = new Vector3f(spawnpointOffset, 0, 0);
//            if (new Random().nextBoolean() == false) {
//                playerSpawnpointOffset = new Vector3f(0, 0, spawnpointOffset);
//            }
//
//            serverLevelManager.registerRandomDestructibleDecoration(new Vector3f(random.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getX()) + blockSize, blockSize, random.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getZ()) + blockSize)
//                    .addLocal(playerSpawnpointOffset)
//            );
//        }
    }

}
