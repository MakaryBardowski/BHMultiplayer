/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.proceduralGeneration;

import com.jme3.math.Vector3f;
import java.util.Random;

/**
 *
 * @author tomasz_potoczko
 */
public class Room{
    private final int[] startPos;
    private final int[] endPos;
    
    public Room(int[] start, int[] end){
        startPos = start;
        endPos = end;
    }
    
    public Vector3f getCentre(){
        return new Vector3f((float)(endPos[0]+startPos[0])/2, 0.001f, (float)(endPos[1]+startPos[1])/2);
    }
    
    public float[] getCentreAsArray(){
        float[] centre = {(float)(endPos[0]+startPos[0])/2, (float)(endPos[1]+startPos[1])/2};
        return centre;
    }
    
    public float getLength(){
        return Math.abs(endPos[0]-startPos[0]);
    }
    
    public float getHeight(){
        return Math.abs(endPos[1]-startPos[1]);
    }
    
    public float getSize(){
        return getLength()*getHeight();
    }
    
    public int[] getRandomTile(Random randomGen){
        int[] pos = {randomGen.nextInt(startPos[1], endPos[1]) , randomGen.nextInt(startPos[0], endPos[0])};
        return pos;
    }
    
}
