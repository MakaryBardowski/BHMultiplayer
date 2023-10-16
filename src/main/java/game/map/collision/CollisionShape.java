/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.collision;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@Getter
public abstract class CollisionShape {

    protected Vector3f position;
    protected float width;
    protected float height;
    protected float length;

    public CollisionShape(Vector3f position) {
        this.position = position;
    }

    public abstract boolean wouldCollideAtPosition(CollisionShape other, Vector3f newPos);


    @Override
    public String toString() {
        return "CollisionShape{" + "position=" + position + '}';
    }

}
