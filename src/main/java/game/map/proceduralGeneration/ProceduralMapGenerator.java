/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.proceduralGeneration;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author tomasz_potoczko
 */
public class ProceduralMapGenerator {
    private final long SEED;
    private Random randomGen;
    
    private byte[][][] logicMap;
    private final int numOfRooms;
    private final int width, length;
    private final int numOfFloors;
    private int lowerBound=-1;
    private int upperBound=-1;
    
    private ArrayList<Floor> floorList;

    public ProceduralMapGenerator(long seed, int length, int width, int minRoomSize, int maxRoomSize, int numOfRooms, int numOfFloors) {
        this.SEED = seed;
        this.length = length;
        this.width = width;
        
        this.numOfRooms = numOfRooms;
        this.numOfFloors = numOfFloors;
        
        initBounds(minRoomSize, maxRoomSize);
        initArrays();
        initRandom();
    }
    
    public void generate(GenType... floorTypes) {
        for (int i=0; i<numOfFloors; i++){
            int j = i%floorTypes.length;
            
            switch (floorTypes[j]) {
                case BSP -> generateBSPFloor(i);
                case CellularAutomata -> generateCAFloor(i);
                default -> generatePNFloor(i);
            }
        }
    }
    
///---------------------------------------------TYPES OF GENERATION-------------------------------------------------------------------
    
    private void generateBSPFloor(int floorIdx) {
        ///generates a BSP floor
        floorList.add(new Floor(length, width, numOfRooms, randomGen, lowerBound, upperBound, floorIdx, floorIdx==(numOfFloors-1)));
        logicMap[floorIdx] = floorList.get(floorIdx).getFloorMap();
        
    }

    private void generateCAFloor(int floorIdx) {
        ///generates a CA floor
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void generatePNFloor(int floorIdx) {
        ///generates a PNF floor
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    

    
///-----------------------------------------------INIT, GETTERS AND SETTERS---------------------------------------------------------

    public byte[][][] getMap() {
        return logicMap;    
    }


    private void initArrays() {
        logicMap = new byte[numOfFloors][length][width];
        floorList = new ArrayList<>();
    }

    private void initRandom() {
        randomGen = new Random();
        randomGen.setSeed(SEED);
    }

    public byte[][][] returnMap() {
        return logicMap;
    }
    
    private void initBounds(int minRoomSize, int maxRoomSize) {
        ///initialises bounds for room sizes for the BSP floor making
        
        int counter=1;
        while (true){
            double size = length*width/Math.pow(2, counter);
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
    
}
