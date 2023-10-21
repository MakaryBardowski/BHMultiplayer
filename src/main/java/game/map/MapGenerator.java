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
            case ARMORY ->
                generateArmory(blockSize, chunkSize, mapSize, a, mapNode);
            case CASUAL ->
                generateCasualMap(blockSize, chunkSize, mapSize, a, mapNode);
            case BOSS ->
                generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);
            default ->
                generateBossMap(blockSize, chunkSize, mapSize, a, mapNode);
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

        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }

    private Map generateCasualMap(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        ///generates a random map

        int minRoomSize = 10, maxRoomSize = 55, numOfRooms = 10, numOfFloors = 1;
        long SEED = 1234567890L;
        ProceduralMapGenerator mapGen = new ProceduralMapGenerator(SEED, mapSize, mapSize, minRoomSize, maxRoomSize, numOfRooms, numOfFloors);
        mapGen.generate(GenType.BSP);
        mapGen.getFloorList().get(0).printMap();
        byte[][][] logicMap = mapGen.getMap();

        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }

    private Map generateArmory(int blockSize, int chunkSize, int mapSize, AssetManager a, Node mapNode) {
        byte[][][] logicMap = new byte[mapSize][mapSize][mapSize]; // blocks are added based on logicMap

        int armorySizeX = 9;
        int armorySizeZ = 8;
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

        for (int z = 0; z < 5; z++) {
            logicMap[3][1][z] = 1;
        }

        for (int x = 5; x < 8; x++) {
            logicMap[x][1][4] = 1;

        }

        Map map = new Map(blockSize, chunkSize, mapSize, logicMap, a, mapNode);
        return map;
    }
}
