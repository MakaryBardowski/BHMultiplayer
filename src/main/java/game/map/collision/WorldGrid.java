/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.collision;


import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author 48793
 */
public class WorldGrid {
 
    private final int cellSize;
    private HashMap<String,HashSet<CollidableInterface>> contents;
    
    
    public WorldGrid(int cellSize){
    this.cellSize = cellSize;
    contents = new HashMap();
    }
    
    public int getCellSize(){
        return cellSize;
    }
    
    public String hash(Vector3f point){
    int[] loc = new int[2];
    loc[0] = cellSize*(int)(Math.floor(point.getX()/cellSize));


    loc[1] = cellSize*(int)(Math.floor(point.getZ()/cellSize));

    return loc[0]+"."+loc[1];

    }
    
    
    public void insert(CollidableInterface object){
        object.insert(this);
    }
    
    public HashSet<CollidableInterface> getNearby(CollidableInterface object,float radius){
        return object.getFromCellsImIn(this);
    }
    
    

  
    
    
    public HashSet<CollidableInterface> getInsideCell(Vector3f vec){
        HashSet<CollidableInterface> output = new HashSet<>();
        if(contents.get( hash(vec)) != null){
    output.addAll( contents.get( hash(vec)) );
        }
        return output;
    }
    
   
    
    public HashMap<String,HashSet<CollidableInterface>> getContents(){
    return contents;
    }
        
}
    
