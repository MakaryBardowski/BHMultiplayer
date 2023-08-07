/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.proceduralGeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ProceduralMapGenerator {
    private Random randomGen;
    private final long SEED;
    private final static int BLOCK_SIZE=4;
    
    private ArrayList<Floor> floorList;
    
    private byte[][][] map;
    private final int numOfRooms;
    private final int sizeX; int sizeY;
    private final int numOfFloors;
    private int lowerBound=-1;
    private int upperBound=-1;
    

    public ProceduralMapGenerator(final long seed, int sizeX, int sizeY, int minRoomSize, int maxRoomSize, int numOfRooms, int numOfFloors) {
        this.SEED = seed;
        this.sizeX = sizeX; this.sizeY=sizeY;
        this.numOfRooms = numOfRooms;
        this.numOfFloors = numOfFloors;
        
        initBounds(minRoomSize, maxRoomSize);
        initArrays();
        initRandom();
    }


    public void generate(GenType... floorTypes) {
        if (numOfRooms>1){
            for (int i=0; i<numOfFloors; i++){
            int j = i%floorTypes.length;
            
                switch (floorTypes[j]) {
                    case BSP -> generateBSPFloor(i);
                    case CellularAutomata -> generateCAFloor(i);
                    default -> generatePNFloor(i);
                }
            }
        }else{
            makeBossRoomMap();
        }

    }
    
    private void generateBSPFloor(int floorIdx) {
        floorList.add(new Floor(sizeX, sizeY, numOfRooms, randomGen, lowerBound, upperBound, floorIdx, floorIdx==(numOfFloors-1)));
        addFloorToMap(floorList.get(floorIdx));
    }
    
    private void addFloorToMap(Floor floor) {
        for (int x=0; x<map.length-1; x++){
            for (int y=getGroundLevel(floor); y<getCeilingLevel(floor); y++){
                for (int z=0; z<map[0][0].length-1; z++){
                    if (floor.getFloorMap()[z][x] == 8){
                        map[x][getCeilingLevel(floor)][z] = 1;
                        map[x][getGroundLevel(floor)][z] = 0;
                    }else{
                        map[x][y][z] = floor.getFloorMap()[z][x];
                    }
                }
            }
        }
        
    }
    
    private void makeBossRoomMap() {
        floorList.add(new Floor(sizeX, sizeY, numOfRooms, randomGen, lowerBound, upperBound, 0, true));
        addFloorToMap(floorList.get(0));
        
    }

    private void generateCAFloor(int floorIdx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void generatePNFloor(int floorIdx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    private void initArrays() {
        map = new byte[sizeX][sizeX][sizeY];
        
        for (byte[][] layer: map){
            for (byte[] row: layer){
                Arrays.fill(row, (byte)1);
            }
        }
        
        floorList = new ArrayList<>();
    }

    private void initRandom() {
        randomGen = new Random();
        randomGen.setSeed(SEED);
    }

    public byte[][][] getMap() {
        return map;
    }
    
    private void initBounds(int minRoomSize, int maxRoomSize) {
        ///initialises bounds for room sizes for the BSP floor making
        
        int counter=1;
        while (true){
            double size = sizeX*sizeY/Math.pow(2, counter);
            if (size <= maxRoomSize && lowerBound==-1){
                lowerBound = counter;
            }else if(size <= minRoomSize && lowerBound!=-1){
                upperBound = counter;
                break;
            }
            counter++;
        }
    }
    
    public long getSeed() {
        return SEED;
    }

    public ArrayList<Floor> getFloorList() {
        return floorList;
    }

    private int getGroundLevel(Floor floor) {
        return 1+(BLOCK_SIZE*floor.getFloorIdx());
    }

    private int getCeilingLevel(Floor floor) {
        return BLOCK_SIZE-1+(BLOCK_SIZE*floor.getFloorIdx());
    }
}

