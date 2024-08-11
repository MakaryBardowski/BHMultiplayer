/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import static game.map.MapType.ARMORY;
import static game.map.MapType.BOSS;
import static game.map.MapType.CASUAL;
import game.map.proceduralGeneration.RandomMapGenerator;
import game.map.proceduralGeneration.Room;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import jme3utilities.math.Vector3i;

/**
 *
 * @author 48793
 */
public class MapGenerator {

    private final long seed;
    private final MapType mapType;
    private final int mapSize;

    public MapGenerator(long seed, MapType mapType, int mapSize) {
        this.seed = seed;
        this.mapType = mapType;
        this.mapSize = mapSize;
    }

    /**
     * this method returns a new map (allocates a new array for voxel data).
     * Recommended use is for first-time map generation
     *
     * @param blockSize size of 1 voxel
     * @param chunkSize chunk size is equal to chunkSize * chunkSize
     * @param mapSize map size is equal to mapSize * mapSize
     * @param a assetManager to be used for node attachment and creating the
     * texture atlas
     * @param mapNode a node that the map should be attached to
     * @return
     */
    public Level generateMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        byte[][][] logicMap = new byte[mapSize][mapSize][mapSize]; // blocks are added based on logicMap
        return decideAndGenerateMap(logicMap, blockSize, chunkSize, mapSize, a, mapNode);
    }

    /**
     * this method returns a new map (allocates a new array for voxel data).
     * Recommended use is for first-time map generation
     *
     * @param map that will be cleaned up and reinitialized (allows to avoid new
     * memory allocation for map)
     *
     * @param blockSize size of 1 voxel
     * @param chunkSize chunk size is equal to chunkSize * chunkSize
     * @param mapSize map size is equal to mapSize * mapSize
     * @param a assetManager to be used for node attachment and creating the
     * texture atlas
     * @param mapNode a node that the map should be attached to
     * @return
     */
    public Level generateOnExistingMapNoMemoryAllocation(Level map, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        return decideAndGenerateMapOnExistingMap(map, blockSize, chunkSize, mapSize, a, mapNode);
    }

    public byte[][][] createBossLogicMap(byte[][][] logicMap) {

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

        for (int x = 1; x < logicMap.length - 1; x++) {
            for (int z = 1; z < logicMap.length - 1; z++) {
                logicMap[x][1][z] = 0;
                logicMap[x][2][z] = 0;
            }
        }

        logicMap[10][1][16] = 1;
        logicMap[13][1][16] = 1;
        logicMap[10][2][16] = 1;
        logicMap[13][2][16] = 1;

        logicMap[11][1][16] = 1;
        logicMap[12][1][16] = 1;
        logicMap[11][2][16] = 1;
        logicMap[12][2][16] = 1;

        logicMap[10][1][10] = 1;
        logicMap[14][1][10] = 1;
        logicMap[10][2][10] = 1;
        logicMap[14][2][10] = 1;

        logicMap[11][1][10] = 1;
        logicMap[12][1][10] = 1;
        logicMap[11][2][10] = 1;
        logicMap[12][2][10] = 1;

        logicMap[20][1][12] = 1;
        logicMap[20][1][19] = 1;
        logicMap[20][2][12] = 1;
        logicMap[20][2][19] = 1;

        logicMap[26][1][12] = 1;
        logicMap[27][1][19] = 1;
        logicMap[16][1][22] = 1;
        logicMap[30][1][23] = 1;
        logicMap[11][1][23] = 1;

        for (int x = 0; x < logicMap.length; x++) {
            for (int z = 0; z < logicMap[x][1].length; z++) {
                if (logicMap[x][0][z] != 0) {
                    logicMap[x][0][z] = 2;
                }
            }
        }

        return logicMap;
    }

    public byte[][][] createArmoryLogicMap(byte[][][] logicMap) {
        int armorySizeX = 9;
        int armorySizeZ = 11;
        int floorLevel = 0;
        for (int x = 0; x < armorySizeX; x++) {
            for (int z = 0; z < armorySizeZ; z++) {
                logicMap[x][floorLevel][z] = 1;
            }
        }

        for (int x = 0; x < armorySizeX; x++) {
            for (int z = 0; z < armorySizeZ; z++) {
                logicMap[x][1][z] = 1;
                logicMap[x][2][z] = 1;
                logicMap[x][3][z] = 1;
            }
        }

        for (int x = 1; x < armorySizeX - 1; x++) {
            for (int z = 1; z < armorySizeZ - 1; z++) {
                logicMap[x][1][z] = 0;
                logicMap[x][2][z] = 0;
            }
        }

        logicMap[armorySizeX - 4][1][armorySizeZ - 1] = 0;

        for (int x = 5; x < 8; x++) {
            logicMap[x][1][4] = 1;

        }
        return logicMap;
    }

    private Level decideAndGenerateMapOnExistingMap(Level map, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        var existingLogicMap = map.getBlockWorld().getLogicMap();
        switch (mapType) {
            case ARMORY:
                fillLogicMap(existingLogicMap, (byte) 0); // armory is in an empty world
                createArmoryLogicMap(existingLogicMap);
                break;
            case CASUAL:
                fillLogicMap(existingLogicMap, (byte) 1); // casual maps are carved out in a 
                createCasualLogicMap(existingLogicMap);
                break;
            case BOSS:
                createBossLogicMap(existingLogicMap);
                break;
            default:
                createBossLogicMap(existingLogicMap);
                break;
        }

        return map.updateAfterLogicMapChange();
    }

    private Level decideAndGenerateMap(byte[][][] logicMap, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        switch (mapType) {
            case ARMORY:
                fillLogicMap(logicMap, (byte) 0); // armory is in an empty world
                createArmoryLogicMap(logicMap);
                break;
            case CASUAL:
                fillLogicMap(logicMap, (byte) 1); // casual maps are carved out in a 
                createCasualLogicMap(logicMap);
                break;
            case BOSS:
                createBossLogicMap(logicMap);
                break;
            default:
                createBossLogicMap(logicMap);
                break;
        }

        Level map = new Level(blockSize, chunkSize, mapSize, logicMap, a, mapNode);

        return map;
    }

    public MapGenerationResult decideAndGenerateMapServer(byte[][][] logicMap) {
        MapGenerationResult mapGenResult;
        switch (mapType) {
            case ARMORY:
                fillLogicMap(logicMap, (byte) 0); // armory is in an empty world
                createArmoryLogicMap(logicMap);
                mapGenResult = new MapGenerationResult(logicMap, null);
                break;
            case CASUAL:
                fillLogicMap(logicMap, (byte) 1); // casual maps are carved out in a 
                mapGenResult = createCasualLogicMapServer(logicMap);
                break;
            case BOSS:
                createBossLogicMap(logicMap);
                mapGenResult = new MapGenerationResult(logicMap, null);
                break;
            default:
                createBossLogicMap(logicMap);
                mapGenResult = new MapGenerationResult(logicMap, null);
                break;
        }

        return mapGenResult;
    }

    private void fillLogicMap(byte[][][] logicMap, byte value) {
        for (int x = 0; x < logicMap.length; x++) {
            for (int y = 0; y < logicMap[x].length; y++) {
                for (int z = 0; z < logicMap[x][y].length; z++) {
                    logicMap[x][y][z] = value;
                }
            }
        }

    }

    public byte[][][] createCasualLogicMap(byte[][][] logicMap) {
        return new RandomMapGenerator(seed, mapSize).createRandomMap(logicMap).getMap();
    }

    public MapGenerationResult createCasualLogicMapServer(byte[][][] logicMap) {
        return new RandomMapGenerator(seed, mapSize).createRandomMap(logicMap);
    }
}
