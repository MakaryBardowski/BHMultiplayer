package LevelLoadSystem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import game.map.blocks.Map;

import java.util.List;

@JsonDeserialize(using = LevelLoadResultDeserializer.class)
public class LevelLoadResult {
    private Map map;
    private List<EntitySpawnData> savedEntityData;
    public LevelLoadResult(byte[] logicMap,int mapSizeX, int mapSizeY, int mapSizeZ, int blockSize, List<EntitySpawnData> savedEntityData){
        this.map = new Map(logicMap,mapSizeX,mapSizeY,mapSizeZ,blockSize);
        this.savedEntityData = savedEntityData;
    }

    public Map getMap(){
        return map;
    }

    public List<EntitySpawnData> getSavedEntityData(){
        return savedEntityData;
    }
}
