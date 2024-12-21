/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.proceduralGeneration;

import game.map.MapGenerationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.map.blocks.Map;
import jme3utilities.math.Vector3i;

/**
 *
 * @author 48793
 */
public class RandomMapGenerator {

    private final Random random;
    private final int mapSizeX;
    private final int mapSizeZ;
    private final int mapSizeY;

    public RandomMapGenerator(long seed, int mapSizeX,int mapSizeY, int mapSizeZ) {
        this.random = new Random(seed);
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.mapSizeZ = mapSizeZ;
    }

    public MapGenerationResult createRandomMap(Map logicMap) {
        int numRooms = random.nextInt(3, 4);

//        int numRooms = 2;
        List<Room> rooms = new ArrayList<>();
        for (int roomCounter = 0; roomCounter < numRooms; roomCounter++) {
            var randomRoomSizeX = random.nextInt(7, 9);
            var randomRoomSizeY = random.nextInt(5, 6);
            var randomRoomSizeZ = random.nextInt(7, 9);

            var room = addRandomRoom(logicMap, randomRoomSizeX, randomRoomSizeY, randomRoomSizeZ, rooms);
            rooms.add(room);
        }

        connectRooms(logicMap, rooms);
        
        var mapGenResult = new MapGenerationResult(logicMap,rooms);
        return mapGenResult;
    }

    private Room addRandomRoom(Map logicMap, int roomSizeX, int roomSizeY, int roomSizeZ, List<Room> allRooms) {
        while (true) {
            var roomPosX = random.nextInt(0, mapSizeX);
            var roomPosY = random.nextInt(0, 1);
            var roomPosZ = random.nextInt(0, mapSizeZ);
            if (isRoomPlaceable(roomSizeX, roomSizeY, roomSizeZ, roomPosX, roomPosY, roomPosZ, allRooms)) {

                for (int x = roomPosX; x < roomPosX + roomSizeX; x++) {
                    for (int y = roomPosY; y < roomPosY + roomSizeY; y++) {
                        for (int z = roomPosZ; z < roomPosZ + roomSizeZ; z++) {
                            logicMap.setBlockIdAtPosition(x,y,z,(byte)1);
                            // hardcode dla podlogi
                            if (y == 0) {
                                logicMap.setBlockIdAtPosition(x,y,z,(byte)2);
                            }
                            // hardcode dla podlogi

                        }
                    }
                }

                // drill the room out
                for (int x = roomPosX + 1; x < roomPosX + roomSizeX - 1; x++) {
                    for (int y = roomPosY + 1; y < roomPosY + roomSizeY - 1; y++) {
                        for (int z = roomPosZ + 1; z < roomPosZ + roomSizeZ - 1; z++) {
                            logicMap.setBlockIdAtPosition(x,y,z,(byte)0);
                        }
                    }
                }
                // drill the room out 
                return new Room(roomPosX, roomPosY, roomPosZ,
                        roomPosX + roomSizeX, roomPosY + roomSizeY, roomPosZ + roomSizeZ);
            }
        }
    }

    private boolean isRoomPlaceable(int roomSizeX, int roomSizeY, int roomSizeZ, int roomPosX, int roomPosY, int roomPosZ, List<Room> allRooms) {
        // is within bounds
        if (roomPosX + roomSizeX >= mapSizeX) {
            return false;
        }
        if (roomPosY + roomSizeY >= mapSizeY) {
            return false;
        }
        if (roomPosZ + roomSizeZ >= mapSizeZ) {
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

    public void connectRooms(Map logicMap, List<Room> roomConnectionPositions) {
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
                    logicMap.setBlockIdAtPosition(x,0,startZ,(byte) 2);
                    logicMap.setBlockIdAtPosition(x,1,startZ,(byte) 0);
                }
            } else if (startX > endX) {
                for (int x = endX; x <= startX; x++) {
                    logicMap.setBlockIdAtPosition(x,0,startZ,(byte) 2);
                    logicMap.setBlockIdAtPosition(x,1,startZ,(byte) 0);
                }
            }

            if (startZ <= endZ) {
                for (int z = startZ; z <= endZ; z++) {
                    logicMap.setBlockIdAtPosition(endX,0,z,(byte)2);
                    logicMap.setBlockIdAtPosition(endX,1,z,(byte)0);
                }
            } else if (endZ <= startZ) {
                for (int z = endZ; z <= startZ; z++) {
                    logicMap.setBlockIdAtPosition(endX,0,z,(byte)2);
                    logicMap.setBlockIdAtPosition(endX,1,z,(byte)0);
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
