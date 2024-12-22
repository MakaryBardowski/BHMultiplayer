package LevelLoadSystem;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class LevelLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    public LevelLoadResult readLevelFile(String filepath) throws IOException {
        return mapper.readValue(new File(filepath), LevelLoadResult.class);
    }
}
