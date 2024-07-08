/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.serializing.Serializable;
import game.entities.Collidable;
import game.entities.mobs.Player;
import game.map.collision.MovementCollisionUtils;
import game.map.collision.WorldGrid;
import java.util.ArrayList;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class MobPosUpdateMessage extends EntityUpdateMessage {

    protected float x;
    protected float y;
    protected float z;

    public MobPosUpdateMessage() {
    }

    public MobPosUpdateMessage(int id, Vector3f pos) {
        super(id);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id=" + id + " x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public void handleServer(ServerMain server) {
        throw new UnsupportedOperationException("Server got an update - but the server is authoritative!");
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        if (entityExistsLocallyClient(id)) {
            getMobByIdClient(id).setServerLocation(getPos());
        }
    }

}
