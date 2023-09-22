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
import game.entities.InteractiveEntity;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author 48793
 */
public class WorldGrid {
    
    private final int cellSize;
    private final HashMap<Integer, HashSet<InteractiveEntity>> contents;
    
    public WorldGrid(int cellSize) {
        this.cellSize = cellSize;
        contents = new HashMap();
    }
    
    public int getCellSize() {
        return cellSize;
    }
    
    public Integer hash(Vector3f point) {
        int x = cellSize * (int) (Math.floor(point.getX() / cellSize));
        int y = cellSize * (int) (Math.floor(point.getY() / cellSize));
        int z = cellSize * (int) (Math.floor(point.getZ() / cellSize));
        
        return x * 1000 + z + y * 1000000;
    }
    
    public void insert(InteractiveEntity entity) {
        contents.get(hash(entity.getNode().getWorldTranslation())).add(entity);
    }
    
    public void remove(InteractiveEntity entity) {
        contents.get(hash(entity.getNode().getWorldTranslation())).remove(entity);
    }
    
    public HashSet<InteractiveEntity> getInsideCell(Vector3f vec) {
        HashSet<InteractiveEntity> output = new HashSet<>();
        if (contents.get(hash(vec)) != null) {
            output.addAll(contents.get(hash(vec)));
        }
        return output;
    }
    
    public HashMap<Integer, HashSet<InteractiveEntity>> getContents() {
        return contents;
    }
    
}
