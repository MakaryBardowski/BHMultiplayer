/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import LevelLoadSystem.LevelLoader;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import game.map.blocks.Map;
import game.map.proceduralGeneration.RandomMapGenerator;

import java.io.IOException;

/**
 *
 * @author 48793
 */
public class MapGenerator {

    private final long seed;
    private final MapType mapType;

    public MapGenerator(long seed, MapType mapType) {
        this.seed = seed;
        this.mapType = mapType;
    }

    /**
     * this method returns a new map (allocates a new array for voxel data).
     * Recommended use is for first-time map generation
     *
     * @param blockSize size of 1 voxel
     * @param chunkSize chunk size is equal to chunkSize * chunkSize
     * @param mapSizeX desired map size X
     * @param mapSizeY desired map size Y
     * @param mapSizeZ desired map size Z
     * @param a assetManager to be used for node attachment and creating the
     * texture atlas
     * @param mapNode a node that the map should be attached to
     * @return
     */
    public Level generateMap(int blockSize, int chunkSize, int mapSizeX,int mapSizeY,int mapSizeZ, AssetManager a, Node mapNode) throws IOException {
        byte[] logicMap = new byte[mapSizeX*mapSizeZ*mapSizeY]; // blocks are added based on logicMap
        Map map = new Map(logicMap,mapSizeX,mapSizeY,mapSizeZ,blockSize);
        return decideAndGenerateMapClient(map, blockSize, chunkSize, mapSizeX,mapSizeY,mapSizeZ, a, mapNode);
    }

    public Map createBossLogicMap(Map logicMap,int mapSizeX, int mapSizeY, int mapSizeZ) {

        int floorLevel = 0;
        for (int x = 0; x < mapSizeX; x++) {
            for (int z = 0; z < mapSizeZ; z++) {
                logicMap.setBlockIdAtPosition(x,floorLevel,z,(byte)1);
            }
        }

        for (int x = 0; x < mapSizeX; x++) {
            for (int z = 0; z < mapSizeZ; z++) {
                logicMap.setBlockIdAtPosition(x,1,z,(byte)1);
                logicMap.setBlockIdAtPosition(x,2,z,(byte)1);
                logicMap.setBlockIdAtPosition(x,3,z,(byte)1);
            }
        }

        for (int x = 1; x < mapSizeX - 1; x++) {
            for (int z = 1; z < mapSizeZ - 1; z++) {
                logicMap.setBlockIdAtPosition(x,1,z,(byte) 0);
                logicMap.setBlockIdAtPosition(x,2,z,(byte) 0);
            }
        }

        logicMap.setBlockIdAtPosition(10,1,16,(byte)1);
        logicMap.setBlockIdAtPosition(13,1,16,(byte)1);

        logicMap.setBlockIdAtPosition(10,2,16,(byte)1);
        logicMap.setBlockIdAtPosition(13,2,16,(byte)1);


        logicMap.setBlockIdAtPosition(11,1,16,(byte)1);
        logicMap.setBlockIdAtPosition(12,1,16,(byte)1);
        logicMap.setBlockIdAtPosition(11,2,16,(byte)1);
        logicMap.setBlockIdAtPosition(12,2,16,(byte)1);


        logicMap.setBlockIdAtPosition(10,1,10,(byte)1);
        logicMap.setBlockIdAtPosition(14,1,10,(byte)1);
        logicMap.setBlockIdAtPosition(10,2,10,(byte)1);
        logicMap.setBlockIdAtPosition(14,2,10,(byte)1);


        logicMap.setBlockIdAtPosition(11,1,10,(byte)1);
        logicMap.setBlockIdAtPosition(12,1,10,(byte)1);
        logicMap.setBlockIdAtPosition(11,2,10,(byte)1);
        logicMap.setBlockIdAtPosition(12,2,10,(byte)1);

        logicMap.setBlockIdAtPosition(20,1,12,(byte)1);
        logicMap.setBlockIdAtPosition(20,1,19,(byte)1);
        logicMap.setBlockIdAtPosition(20,2,12,(byte)1);
        logicMap.setBlockIdAtPosition(20,2,19,(byte)1);

        logicMap.setBlockIdAtPosition(26,1,12,(byte)1);
        logicMap.setBlockIdAtPosition(27,1,19,(byte)1);
        logicMap.setBlockIdAtPosition(16,1,22,(byte)1);
        logicMap.setBlockIdAtPosition(30,1,23,(byte)1);
        logicMap.setBlockIdAtPosition(11,1,23,(byte)1);


        for (int x = 0; x < mapSizeX; x++) {
            for (int z = 0; z < mapSizeZ; z++) {
                if (logicMap.isPositionNotEmpty(x,0,z)) {
                    logicMap.setBlockIdAtPosition(x,0,z,(byte)2);
                }
            }
        }

        return logicMap;
    }

    private Level decideAndGenerateMapClient(Map logicMap, int blockSize, int chunkSize, int mapSizeX,int mapSizeY,int mapSizeZ, AssetManager a, Node mapNode) throws IOException {
        switch (mapType) {
            case ARMORY:
                // expect server to provide map info
//                logicMap = new LevelLoader().readLevelFile("assets/Maps/office.json").getLogicMap();
                break;
            case CASUAL:
                fillLogicMap(logicMap, (byte) 1); // casual maps are carved out in a 
                createCasualLogicMap(logicMap,mapSizeX,mapSizeY,mapSizeZ);
                break;
            case BOSS:
                createBossLogicMap(logicMap,mapSizeX,mapSizeY,mapSizeZ);
                break;
            default:
                createBossLogicMap(logicMap,mapSizeX,mapSizeY,mapSizeZ);
                break;
        }

        Level map = new Level(blockSize, chunkSize, mapSizeX,mapSizeY,mapSizeZ, logicMap, a, mapNode);

        return map;
    }

    public MapGenerationResult decideAndGenerateMapServer(Map logicMap,int mapSizeX,int mapSizeY, int mapSizeZ) throws IOException {
        MapGenerationResult mapGenResult;
        switch (mapType) {
            case ARMORY:
                logicMap = new LevelLoader().readLevelFile("assets/Maps/office.json").getMap();
                mapGenResult = new MapGenerationResult(logicMap, null);
                break;
            case CASUAL:
                fillLogicMap(logicMap, (byte) 1); // casual maps are carved out in a 
                mapGenResult = createCasualLogicMapServer(logicMap,mapSizeX,mapSizeY,mapSizeZ);
                break;
            case BOSS:
                createBossLogicMap(logicMap,mapSizeX,mapSizeY,mapSizeZ);
                mapGenResult = new MapGenerationResult(logicMap, null);
                break;
            default:
                createBossLogicMap(logicMap,mapSizeX,mapSizeY,mapSizeZ);
                mapGenResult = new MapGenerationResult(logicMap, null);
                break;
        }

        return mapGenResult;
    }

    private void fillLogicMap(Map map, byte value) {
        for (int i = 0; i < map.getLength(); i++) {
            map.setBlockIdAtIndex(i,value);
        }

    }

    public Map createCasualLogicMap(Map logicMap,int mapSizeX,int mapSizeY, int mapSizeZ) {
        return new RandomMapGenerator(seed, mapSizeX,mapSizeY,mapSizeZ).createRandomMap(logicMap).getMap();
    }

    public MapGenerationResult createCasualLogicMapServer(Map logicMap,int mapSizeX, int mapSizeY, int mapSizeZ) {
        return new RandomMapGenerator(seed, mapSizeX,mapSizeY,mapSizeZ).createRandomMap(logicMap);
    }
}
