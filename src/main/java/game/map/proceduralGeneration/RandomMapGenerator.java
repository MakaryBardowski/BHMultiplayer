/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.proceduralGeneration;

import game.map.MapGenerationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jme3utilities.math.Vector3i;

/**
 *
 * @author 48793
 */
public class RandomMapGenerator {

    private final Random random;
    private final int mapSize;

    public RandomMapGenerator(long seed, int mapSize) {
        this.random = new Random(seed);
        this.mapSize = mapSize;
    }

    public MapGenerationResult createRandomMap(byte[][][] logicMap) {
        int numRooms = random.nextInt(8, 12);
//        int numRooms = 2;
        List<Room> rooms = new ArrayList<>();
        for (int roomCounter = 0; roomCounter < numRooms; roomCounter++) {
            var randomRoomSizeX = random.nextInt(5, 10);
            var randomRoomSizeY = random.nextInt(4, 7);
            var randomRoomSizeZ = random.nextInt(5, 10);

            var room = addRandomRoom(logicMap, randomRoomSizeX, randomRoomSizeY, randomRoomSizeZ, rooms);
            rooms.add(room);
        }

        connectRooms(logicMap, rooms);
        
        var mapGenResult = new MapGenerationResult(logicMap,rooms);
        return mapGenResult;
    }

    private Room addRandomRoom(byte[][][] logicMap, int roomSizeX, int roomSizeY, int roomSizeZ, List<Room> allRooms) {
        while (true) {
            var roomPosX = random.nextInt(0, mapSize);
            var roomPosY = random.nextInt(0, 1);
            var roomPosZ = random.nextInt(0, mapSize);
            if (isRoomPlaceable(logicMap, roomSizeX, roomSizeY, roomSizeZ, roomPosX, roomPosY, roomPosZ, allRooms)) {

                for (int x = roomPosX; x < roomPosX + roomSizeX; x++) {
                    for (int y = roomPosY; y < roomPosY + roomSizeY; y++) {
                        for (int z = roomPosZ; z < roomPosZ + roomSizeZ; z++) {
                            logicMap[x][y][z] = 1;
                            // hardcode dla podlogi
                            if (y == 0) {
                                logicMap[x][y][z] = 2;

                            }
                            // hardcode dla podlogi

                        }
                    }
                }

                // drill the room out
                for (int x = roomPosX + 1; x < roomPosX + roomSizeX - 1; x++) {
                    for (int y = roomPosY + 1; y < roomPosY + roomSizeY - 1; y++) {
                        for (int z = roomPosZ + 1; z < roomPosZ + roomSizeZ - 1; z++) {
                            logicMap[x][y][z] = 0;

                        }
                    }
                }
                // drill the room out 
                return new Room(roomPosX, roomPosY, roomPosZ,
                        roomPosX + roomSizeX, roomPosY + roomSizeY, roomPosZ + roomSizeZ);
            }
        }
    }

    private boolean isRoomPlaceable(byte[][][] logicMap, int roomSizeX, int roomSizeY, int roomSizeZ, int roomPosX, int roomPosY, int roomPosZ, List<Room> allRooms) {
        // is within bounds
        if (roomPosX + roomSizeX >= logicMap.length) {
            return false;
        }
        if (roomPosY + roomSizeY >= logicMap[0].length) {
            return false;
        }
        if (roomPosZ + roomSizeZ >= logicMap[0][0].length) {
            return false;
        }
        // is within bounds
        if (isRoomOverlapOthers(roomPosX, roomPosY, roomPosZ, roomSizeX, roomSizeY, roomSizeZ, allRooms)) {
            return false;
        }

        // no room placed here
//        if()
        return true;
    }

    public void connectRooms(byte[][][] logicMap, List<Room> roomConnectionPositions) {
        for (int i = 0; i < roomConnectionPositions.size() - 1; i++) {
            var currentRoom = roomConnectionPositions.get(i);
            var nextRoom = roomConnectionPositions.get(i + 1);
            var currentRoomConnectionPoint = getRandomConnectionPointForRoom(currentRoom);
            var nextRoomConnectionPoint = getRandomConnectionPointForRoom(nextRoom);

            int startX = currentRoomConnectionPoint.x();
            int endX = nextRoomConnectionPoint.x();

            int startZ = currentRoomConnectionPoint.z();
            int endZ = nextRoomConnectionPoint.z();
            System.out.println("ABCDE connecting: [" + startX + "][" + startZ + "]");
            System.out.println("to: [" + endX + "][" + endZ + "]");

            if (startX <= endX) {
                for (int x = startX; x <= endX; x++) {
                    logicMap[x][0][startZ] = 2;
                    logicMap[x][1][startZ] = 0;

                }
            } else if (startX > endX) {
                for (int x = endX; x <= startX; x++) {
                    logicMap[x][0][startZ] = 2;
                    logicMap[x][1][startZ] = 0;

                }
            }

            if (startZ <= endZ) {
                for (int z = startZ; z <= endZ; z++) {
                    logicMap[endX][0][z] = 2;
                    logicMap[endX][1][z] = 0;

                }
            } else if (endZ <= startZ) {
                for (int z = endZ; z <= startZ; z++) {
                    logicMap[endX][0][z] = 2;
                    logicMap[endX][1][z] = 0;

                }
            }
        }
    }

    public Vector3i getRandomConnectionPointForRoom(Room room) {
        return new Vector3i( // the +1 is so that it doesnt connect to the corner (wall)
                random.nextInt(room.getStartX() + 1, room.getEndX() - 1),
                room.getStartY(),
                random.nextInt(room.getStartZ() + 1, room.getEndZ() - 1)
        );
    }

    public boolean isRoomOverlapOthers(int roomStartX, int roomStartY, int roomStartZ, int roomSizeX, int roomSizeY, int roomSizeZ, List<Room> allRooms) {
        int tMinX = roomStartX;
        int tMaxX = roomStartX + roomSizeX;
        int tMinY = roomStartY;
        int tMaxY = roomStartY + roomSizeY;
        int tMinZ = roomStartZ;
        int tMaxZ = roomStartZ + roomSizeZ;

        for (var room : allRooms) {
            int oMinX = room.getStartX();
            int oMaxX = room.getEndX();
            int oMinY = room.getStartY();
            int oMaxY = room.getEndY();
            int oMinZ = room.getStartZ();
            int oMaxZ = room.getEndZ();

            if (!(tMaxX < oMinX || tMinX > oMaxX
                    || tMaxY < oMinY || tMinY > oMaxY
                    || tMaxZ < oMinZ || tMinZ > oMaxZ)) {
                return true;
            }
        }
        return false;
    }
}
