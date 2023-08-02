/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.proceduralGeneration;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author tomasz_potoczko
 */
public class Floor {
    ///floor data
    private final int floorIdx;
    private final boolean IsLastFloor;
    private byte[][] floorMap;
    private final int numOfRooms;
    private final int width, length;
    private final Random randomGen;
    
    ///for map gen
    ArrayList<Room> roomList;
    private int[] start;
    private int[] end;
    private int lowerBound=-1;
    private int upperBound=-1;

    public Floor(int length, int width, int numOfRooms, Random randomGen, int lowerBound, int upperBound, int floorIdx, boolean IsLastFloor) {
        this.width = width; this.length = length;
        this.numOfRooms = numOfRooms;
        this.randomGen=randomGen;
        this.lowerBound=lowerBound;
        this.upperBound=upperBound;
        this.floorIdx = floorIdx;
        this.IsLastFloor = IsLastFloor;
        
        initRooms();
        generateBSPFloor();
    }

    private void generateBSPFloor(){
        
        int divideCounter=0;
        for (int i=0; i<numOfRooms; i++){
            initStartEnd();
            int numOfDivides = randomGen.nextInt(lowerBound, upperBound);
            for (int div=0; div<numOfDivides; div++){
                
                boolean typeOfDivide = divideCounter%2 == 0;
                boolean sideDiscarded = randomGen.nextBoolean();
                
                divideMap(typeOfDivide, sideDiscarded);
                divideCounter++;
            }
            roomList.add(new Room(start, end));
            addRoom();

        }
        connectRooms();
        if (!IsLastFloor){
            addEntranceToNextFloor();
        }
        
        //printMap();
    }

///------------------------------------------METHODS FOR GENERATION-----------------------------------------------------------------------------

    private void divideMap(boolean typeOfDivide, boolean sideDiscarded) {
        if (typeOfDivide == true){
            //divide vertically
            if (sideDiscarded == true){
                //discard left side -> room[middleLine to end][all]=true;
                start[1] = (start[1]+end[1])/2;
                
            }else{
                //discard right side -> -> room[0 to middleLine][all]=true;
                end[1] = (start[1]+end[1])/2;
            }

        }else{
            //divide horizontally
            if (sideDiscarded == true){
                //discard top side -> room[all][middleLine to end]=true;
                start[0] = (start[0]+end[0])/2;
                
            }else{
                //discard bottom side -> room[all][0 to middleLine]=true;
                end[0] = (start[0]+end[0])/2;
            }
        }
    }
    
    private void connectRooms() {
        Room startRoom;
        Room endRoom = null;
        for (Iterator<Room> roomIterator = roomList.iterator(); roomIterator.hasNext();) {
            startRoom = endRoom != null ? endRoom: roomIterator.next();
            if (roomIterator.hasNext()){
                endRoom = roomIterator.next();
            } else{
                System.out.println("break");
                break;
            }
            addCorridor(startRoom.getCentreAsArray(), endRoom.getCentreAsArray());
        }
    }
    
    private void addCorridor(float[] start, float[] end) {
        int iterStart;
        int iterEnd;
        
        //makes the vertical line
        if (start[0] > end[0]){
            iterStart = (int)end[0];
            iterEnd = (int)start[0];
        }else{
            iterStart = (int)start[0];
            iterEnd = (int)end[0];
        }
        for (int i=iterStart; i<=iterEnd; i++){
            floorMap[(int)start[1]][i] = 1;
        }
        
        //makes the horizontal line
        if (start[1] > end[1]){
            iterStart = (int)end[1];
            iterEnd = (int)start[1];
        }else{
            iterStart = (int)start[1];
            iterEnd = (int)end[1];
        }
        for (int i=iterStart; i<=iterEnd; i++){
            floorMap[i][(int)end[0]] = 1;
        }
    }

    private void addRoom() {
        for (int i=start[0];i<end[0]; i++){
            for (int j=start[1]; j<end[1];j++){
                floorMap[j][i] = 1;
            }
        }
    }
    
    private void addEntranceToNextFloor() {
        Room randRoom = roomList.get(randomGen.nextInt(roomList.size()-1));
        int[] pos = randRoom.getRandomTile(randomGen);
        floorMap[pos[0]][pos[1]] = 2;
    }
    
    public Vector3f getFreeSpace() {
        while(true){
            int i = randomGen.nextInt(width);
            int j = randomGen.nextInt(length);
            if (floorMap[i][j] == 1){
                return new Vector3f(i*2-1, 3.1f, j*2-1);
            }
        }
    }
    
///-----------------------------------------UTIL METHODS------------------------------------------------------------------------------
    
    private void initStartEnd() {
        start = new int[2];
        end = new int[2];
        
        start[0] = 0;
        start[1]=0;
        end[0]=width;
        end[1]=length;
    }
  
    private void initRooms() {
        floorMap = new byte[length][width];
        roomList = new ArrayList<>();
    }
    
    public ArrayList<Room> getRoomList() {
        return roomList;
    }
    
    public byte[][] getFloorMap() {
        return floorMap;
    }
    
    public int getNumOfRooms() {
        return numOfRooms;
    }

    public int getFloorIdx() {
        return floorIdx;
    }

    public boolean isIsLastFloor() {
        return IsLastFloor;
    }
    
    public void printMap() {
        ///outputs a map of the floor
        System.out.println("printing map of floor "+floorIdx);
        for (byte[] roomsLine: floorMap){
            System.out.println(Arrays.toString(roomsLine));
        }
    }
    
}
