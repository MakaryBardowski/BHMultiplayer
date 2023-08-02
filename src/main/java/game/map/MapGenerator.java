/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import game.map.proceduralGeneration.ProceduralMapGenerator;
import game.map.proceduralGeneration.GenType;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public class MapGenerator {

    public Map generateMap(MapType type, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        return switch (type) {
            case CASUAL -> generateCasualMap(blockSize, chunkSize, mapSize, a, mapNode);
            case BOSS -> generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);
            default -> generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);
        };

    }

    private Map generateBossMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        ///makes a square map
        
        byte[][][] logicMap = new byte[mapSize][mapSize][mapSize]; // blocks are added based on logicMap

        int floorLevel = 0;
        for (int x = 0; x < logicMap.length; x++) {
            for (int z = 0; z < logicMap.length; z++) {
                logicMap[x][floorLevel][z] = 1;
            }
        }
        
        for (int x = 0; x < logicMap.length; x++) {
            for (int z = 0; z < logicMap.length; z++) {
                logicMap[x][1][z] = 1;
                logicMap[x][2][z] = 1;
                logicMap[x][3][z] = 1;
            }
        }
        
        for (int x = 1; x < logicMap.length-1; x++) {
            for (int z = 1; z < logicMap.length-1; z++) {
                logicMap[x][1][z] = 0;
                logicMap[x][2][z] = 0;
            }
        }
        
        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }

    private Map generateCasualMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        ///generates a random map
        
        int length=mapSize, height=mapSize, minRoomSize=42, maxRoomSize=42, numOfRooms=10, numOfFloors=mapSize;
        long SEED = 1234567890L;
        ProceduralMapGenerator mapGen = new ProceduralMapGenerator(SEED, length, height, minRoomSize, maxRoomSize, numOfRooms, numOfFloors);
        mapGen.generate(GenType.BSP);
        mapGen.getFloorList().get(0).printMap();
        byte[][][] logicMap = mapGen.getMap();
        

        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }
}
