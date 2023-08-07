/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import game.map.proceduralGeneration.ProceduralMapGenerator;
import game.map.proceduralGeneration.GenType;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class MapGenerator {
    @Getter
    private final long SEED = 12345678901L;
    @Getter
    ProceduralMapGenerator mapGen;


    public Map generateMap(MapType type, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        return switch (type) {
            case CASUAL -> generateCasualMap(blockSize, chunkSize, mapSize, a, mapNode);
            case BOSS -> generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);
            default -> generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);
        };

    }

    private Map generateBossMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        ///makes a square map
        
        int minRoomSize=1, maxRoomSize=1, numOfRooms=1, numOfFloors=1;
        
        mapGen = new ProceduralMapGenerator(SEED, mapSize, mapSize, minRoomSize, maxRoomSize, numOfRooms, numOfFloors);
        mapGen.generate(GenType.BSP);
        mapGen.getFloorList().get(0).printMap();
        byte[][][] logicMap = mapGen.getMap();

        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }

    private Map generateCasualMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        ///generates a random map
        
        int minRoomSize=10, maxRoomSize=55, numOfRooms=10, numOfFloors=1;
        mapGen = new ProceduralMapGenerator(SEED, mapSize, mapSize, minRoomSize, maxRoomSize, numOfRooms, numOfFloors);
        mapGen.generate(GenType.BSP);
        byte[][][] logicMap = mapGen.getMap();
        
        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }
}
