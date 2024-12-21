package game.map;

import LevelLoadSystem.LevelLoader;

import java.io.IOException;

public class LevelGenerator {
    private static final String LEVEL_FILE_EXTENSION = ".json";
    private static final String STARTING_LEVEL_FILEPATH = "assets/Maps/office"+LEVEL_FILE_EXTENSION;
    private static final String BOSS_LEVEL_FILEPATH_PREFIX = "assets/Maps/bossRoom";
    private static final String INVALID_MAP_TYPE_FOR_FILE_LOADING_PROVIDED_MESSAGE = "cannot load map of given type '%s'. Maps can be loaded with types ARMORY,BOSS";

    private final long levelSeed;
    private final MapType mapType;
    private final int levelIndex;
    private final MapGenerator mapGenerator;
//    private final MobGenerator mobGenerator;
    public LevelGenerator(long levelSeed, MapType mapType, int levelIndex){
        this.levelSeed = levelSeed;
        this.mapType = mapType;
        this.levelIndex = levelIndex;
        this.mapGenerator = new MapGenerator(levelSeed,mapType);
//        this.mobGenerator = new MobGenerator(levelSeed);
    }

    public LevelGenerationResult generateLevel() throws IOException {
        if(isMapProcedurallyGenerated(mapType)){
            return null;
        }

        var levelFilePath = getSavedLevelFilepath(mapType,levelIndex);
        var levelLoadResult = new LevelLoader().readLevelFile(levelFilePath);

        var savedLogicMap = levelLoadResult.getMap();
        var loadedEntityData = levelLoadResult.getSavedEntityData();

        return new LevelGenerationResult(savedLogicMap,loadedEntityData);
    }

    public boolean isMapProcedurallyGenerated(MapType mapType){
        return mapType == MapType.CASUAL;
    }

    public String getSavedLevelFilepath(MapType mapType, int levelIndex){
        if(mapType == MapType.ARMORY){
            return STARTING_LEVEL_FILEPATH;
        }
        if(mapType == MapType.BOSS) {
            return BOSS_LEVEL_FILEPATH_PREFIX + levelIndex + LEVEL_FILE_EXTENSION;
        }
        throw new IllegalArgumentException(String.format(INVALID_MAP_TYPE_FOR_FILE_LOADING_PROVIDED_MESSAGE,mapType));
    }
}
