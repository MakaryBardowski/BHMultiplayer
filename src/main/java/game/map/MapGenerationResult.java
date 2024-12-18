package game.map;

import game.map.proceduralGeneration.Room;
import java.util.ArrayList;
import java.util.List;
import jme3utilities.math.Vector3i;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 *
 * @author 48793
 */
@Getter
@AllArgsConstructor
public class MapGenerationResult {
    private byte[][][] map;
    private List<Room> rooms;
}
