package game.map;

import LevelLoadSystem.EntitySpawnData;
import game.map.blocks.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class LevelGenerationResult {
    private Map logicMap;
    private List<EntitySpawnData> entitySpawnData;
}
