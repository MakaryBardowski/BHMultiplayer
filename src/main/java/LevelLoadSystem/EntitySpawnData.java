package LevelLoadSystem;

import com.jme3.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class EntitySpawnData {
    private int entityType;
    private int templateIndex;
    private Vector3f position;


    public boolean isDestructibleDecoration(){
        return entityType == 0;
    }

    public boolean isIndestructibleDecoration(){
        return entityType == 1;
    }

    public boolean isMob(){
        return entityType == 2;
    }

    public boolean isItem(){
        return entityType == 3;
    }
}
