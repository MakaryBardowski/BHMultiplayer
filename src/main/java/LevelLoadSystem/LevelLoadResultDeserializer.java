package LevelLoadSystem;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.jme3.math.Vector3f;
import game.map.blocks.Map;

import java.io.IOException;
import java.util.ArrayList;

public class LevelLoadResultDeserializer extends StdDeserializer<LevelLoadResult> {
    protected LevelLoadResultDeserializer(){
        super(LevelLoadResult.class);
    }

    @Override
    public LevelLoadResult deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        var dimensionNode = node.get(LevelLoadKeys.MAP_DIMENSIONS_KEY);
        var blockSizeNode = node.get(LevelLoadKeys.MAP_BLOCK_SIZE_KEY);

        int mapSizeX = dimensionNode.get(0).asInt();
        int mapSizeY = dimensionNode.get(1).asInt();
        int mapSizeZ = dimensionNode.get(2).asInt();

        byte[] logicMap = new byte[mapSizeX   *   mapSizeY   *   mapSizeZ];
        var savedEntitiesData = new ArrayList<EntitySpawnData>();
        var blocksNode = node.get(LevelLoadKeys.MAP_BLOCK_LAYER_KEY);
        for(var layerData : blocksNode){
            var height = layerData.get(LevelLoadKeys.MAP_BLOCK_POSITION_Y_KEY).asInt();

            int index = 0;
            for(var blockNode : layerData.get(LevelLoadKeys.MAP_BLOCK_DATA_KEY)){
                byte blockId = (byte) blockNode.asInt();
                logicMap[Map.positionToIndex(index % mapSizeX, height, index/mapSizeZ, mapSizeY, mapSizeZ)] = blockId;
                index++;
            }
        }

        var entitesNode = node.get(LevelLoadKeys.ENTITIES_KEY);
        for(var entityNode : entitesNode){
            var entityTypeIndex = entityNode.get(LevelLoadKeys.ENTITY_TYPE_INDEX_KEY).asInt();
            var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
            var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
            var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
            var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
            var savedEntityData = new EntitySpawnData(entityTypeIndex,entityTemplateIndex,new Vector3f(positionX,positionY,positionZ));
            savedEntitiesData.add(savedEntityData);
        }

        return new LevelLoadResult(logicMap,mapSizeX,mapSizeY,mapSizeZ,blockSizeNode.asInt(), savedEntitiesData);
    }
}
