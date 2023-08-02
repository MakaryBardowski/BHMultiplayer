/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map;

import game.map.blocks.BlockWorld;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public class Map {
    private BlockWorld blockWorld;
    
    public Map(int blockSize, int chunkSize, int mapSize, byte[][][] logicMap, AssetManager a, Node mapNode){
        blockWorld = new BlockWorld(blockSize,chunkSize,mapSize, logicMap,a,mapNode);
    }
    
    public BlockWorld getBlockWorld(){
        return blockWorld;
    }
    
}
