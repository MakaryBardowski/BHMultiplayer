/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.map.collision.WorldGrid;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public abstract class Movable extends InteractiveEntity {

    @Getter
    protected AtomicBoolean positionChangedOnServer = new AtomicBoolean(true);
    @Getter
    protected AtomicBoolean rotationChangedOnServer = new AtomicBoolean(true);
    
    @Getter
    @Setter
    protected Vector3f movementVector = new Vector3f(0, 0, 0);

    public Movable(int id, String name, Node node) {
        super(id, name, node);
    }

    public abstract void move(float tpf);

    public abstract void moveServer(Vector3f moveVec);

    public boolean isAbleToMove() {
        return true;
    }
;
}
