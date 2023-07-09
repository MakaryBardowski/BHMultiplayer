/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Map;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public class MapGenerator {

    public Map generateMap(MapType type, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        switch (type) {
            case CASUAL:
                return generateCasualMap(blockSize, chunkSize, mapSize, a, mapNode);
            case BOSS:
                return generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);
            default:
                return generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);

        }

    }

    private Map generateBossMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        byte[][][] logicMap = new byte[mapSize][mapSize][mapSize]; // blocks are added based on logicMap

        // generate floor 
        int floorLevel = 0;
        for (int x = 0; x < logicMap.length; x++) {
            for (int z = 0; z < logicMap.length; z++) {
                logicMap[x][floorLevel][z] = 1;
            }
        }
//        // generate floor 

       for (int x = 8; x < logicMap.length; x++) {
           for(int y = 0; y<logicMap.length;y++){
            for (int z = 8; z < logicMap.length; z++) {
                
                logicMap[x][y][z] = 1;
            }
           }
        }
        logicMap[15][1][13] = 1;
                logicMap[15][1][15] = 1;
        logicMap[16][1][15] = 1;
        logicMap[16][1][13] = 1;
        logicMap[17][1][14] = 1;
        logicMap[14][0][13] =0;

//        logicMap[15][0][13] = 0;

        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }

    private Map generateCasualMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        byte[][][] logicMap = new byte[mapSize][mapSize][mapSize]; // blocks are added based on logicMap

        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }
}
