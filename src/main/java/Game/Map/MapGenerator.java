/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Map;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Dominik Waœniewski
 */
public class MapGenerator {

    public Map generateMap( MapType type,int blockSize,int chunkSize,int mapSize,AssetManager a,Node mapNode) {
        //TODO: Create bossMap
//        switch (type) {
//            case CASUAL:
//                return generateCasualMap(blockSize,chunkSize,mapSize,a,mapNode);
//            case BOSS:
//                return generateBossMap(blockSize,chunkSize,mapSize,a,mapNode);
//            default:
//                return generateBossMap(blockSize,chunkSize,mapSize,a,mapNode);
//
//        }
        return generateCasualMap(blockSize,chunkSize,mapSize,a,mapNode);

    }

    private Map generateBossMap(int blockSize,int chunkSize,int mapSize,AssetManager a,Node mapNode) {
        byte[][][] logicMap = new byte[mapSize][mapSize][mapSize]; // blocks are added based on logicMap

        // generate floor 
        int floorLevel = 0;
        for (int x = 0; x < logicMap.length; x++) {
            for (int z = 0; z < logicMap.length; z++) {
                logicMap[x][floorLevel][z] = 1;
            }
        }
        // generate floor 
        
        logicMap[15][1][13] = 1;
        

        Map map = new Map(blockSize,chunkSize,mapSize,logicMap,a,mapNode);
        return map;
    }

    private Map generateCasualMap(int blockSize,int chunkSize,int mapSize,AssetManager a,Node mapNode) {
        long seed = generateSeed();
        byte[][][] logicMap = new byte[mapSize][mapSize][mapSize]; // blocks are added based on logicMap
        Random seedRandom = new Random(seed);
        int numberOfRooms = 30;
        
        int[][] roomsPositions = new int[numberOfRooms][2]; //[x][z][room size]
        for (int i = 0; i < numberOfRooms; i++) {
            int roomSize = seedRandom.nextInt(4,8); //Change room size (including walls)
            int[] startPoint = {seedRandom.nextInt(0, mapSize-roomSize), seedRandom.nextInt(0, mapSize-roomSize)};
            while(!roomSpaceAvailability(startPoint, roomSize, logicMap)){
                roomSize = seedRandom.nextInt(4, 8);    //Change this aswell to change room size
                startPoint = new int[]{seedRandom.nextInt(0, mapSize - roomSize), seedRandom.nextInt(0, mapSize - roomSize)};
            }
            roomsPositions[i][0] = startPoint[0];
            roomsPositions[i][1] = startPoint[1];

            //Creating rooms and walls
            for (int x = startPoint[0]; x < startPoint[0] + roomSize; x++) {
                for (int z = startPoint[1]; z < startPoint[1] + roomSize; z++) {
                    logicMap[x][0][z] = 1;
                    if(x == startPoint[0] || z == startPoint[1] || x == (startPoint[0] + roomSize) - 1 || z == (startPoint[1] + roomSize) - 1){
                        logicMap[x][1][z] = 1;
                    }
                }
            }

        }
        
        Arrays.sort(roomsPositions, (c, d) -> (c[0] + c[1]) - (d[0] + d[1]));

        //Creating corridors
        for (int i = 0; i < roomsPositions.length - 1; i++) {
            int j;
            if (roomsPositions[i][0] < roomsPositions[i + 1][0]) {
                for (j = roomsPositions[i][0]; j < roomsPositions[i + 1][0]; j++) {
                    logicMap[j][0][roomsPositions[i][1]] = 1;
                }
            } else {
                for (j = roomsPositions[i][0]; j > roomsPositions[i + 1][0]; j--) {
                    logicMap[j][0][roomsPositions[i][1]] = 1;
                }
            }

            if (roomsPositions[i][1] < roomsPositions[i + 1][1]) {
                for (int k = roomsPositions[i][1]; k < roomsPositions[i + 1][1]; k++) {
                    logicMap[j][0][k] = 1;
                }
            } else {
                for (int k = roomsPositions[i][1]; k > roomsPositions[i + 1][1]; k--) {
                    logicMap[j][0][k] = 1;
                }
            }
        }
        
       
        Map map = new Map(blockSize,chunkSize,mapSize,logicMap,a,mapNode);
        return map;
    }
    
    private long generateSeed(){
        Random r = new Random();
        return r.nextLong();
    }
    
    public static boolean roomSpaceAvailability(int[] startPoint, int roomSize, byte[][][] logicMap){
        for (int i = startPoint[0]; i < startPoint[0] + roomSize; i++) {
            for (int j = startPoint[1]; j < startPoint[1] + roomSize; j++) {
                if (logicMap[i][0][j] != 0){
                    return false;
                }
            }
        }
        return true;
    }
}
