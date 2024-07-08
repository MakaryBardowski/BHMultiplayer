/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import game.map.proceduralGeneration.ProceduralMapGenerator;
import game.map.proceduralGeneration.GenType;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import static game.map.MapType.ARMORY;
import static game.map.MapType.BOSS;
import static game.map.MapType.CASUAL;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class MapGenerator {
    private long seed;
    private MapType mapType;
    
    public MapGenerator(long seed, MapType mapType){
        this.seed = seed;
        this.mapType = mapType;
    }

    /**
     * this method returns a new map (allocates a new array for voxel data).
     * Recommended use is for first-time map generation
     *
     * @param seed map generation seed
     * @param type map generation type
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
        return decideAndGenerateMap(logicMap, mapType, blockSize, chunkSize, mapSize, a, mapNode);
    }

    /**
     * this method returns a new map (allocates a new array for voxel data).
     * Recommended use is for first-time map generation
     *
     * @param map that will be cleaned up and reinitialized (allows to avoid new
     * memory allocation for map)

     * @param blockSize size of 1 voxel
     * @param chunkSize chunk size is equal to chunkSize * chunkSize
     * @param mapSize map size is equal to mapSize * mapSize
     * @param a assetManager to be used for node attachment and creating the
     * texture atlas
     * @param mapNode a node that the map should be attached to
     * @return
     */
    public Level generateOnExistingMapNoMemoryAllocation(Level map, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        return decideAndGenerateMapOnExistingMap(map, mapType, blockSize, chunkSize, mapSize, a, mapNode);
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
        
        for (int x = 5; x < 8; x++) {
            logicMap[x][1][4] = 1;

        }
        return logicMap;
    }

    private Level decideAndGenerateMapOnExistingMap(Level map, MapType type, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        var existingLogicMap = map.getBlockWorld().getLogicMap();
        fillLogicMap(existingLogicMap, (byte) 0);
        switch (type) {
            case ARMORY ->
                createArmoryLogicMap(existingLogicMap);
//            case CASUAL ->
//                createCasualLogicMap(mapSize);
            case BOSS ->
                createBossLogicMap(existingLogicMap);
            default ->
                createBossLogicMap(existingLogicMap);
        }

        return map.updateAfterLogicMapChange();
    }

    private Level decideAndGenerateMap(byte[][][] logicMap, MapType type, int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        var gameWorldLogicMap = switch (type) {
            case ARMORY ->
                createArmoryLogicMap(logicMap);
//            case CASUAL ->
//                createCasualLogicMap(mapSize);
            case BOSS ->
                createBossLogicMap(logicMap);
            default ->
                createBossLogicMap(logicMap);
        };
        Level map = new Level(blockSize, chunkSize, mapSize, gameWorldLogicMap, a, mapNode);

        return map;
    }
    
    public byte[][][] decideAndGenerateMapServer(byte[][][] logicMap) {
        var gameWorldLogicMap = switch (mapType) {
            case ARMORY ->
                createArmoryLogicMap(logicMap);
//            case CASUAL ->
//                createCasualLogicMap(mapSize);
            case BOSS ->
                createBossLogicMap(logicMap);
            default ->
                createBossLogicMap(logicMap);
        };
        return gameWorldLogicMap;
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

}
