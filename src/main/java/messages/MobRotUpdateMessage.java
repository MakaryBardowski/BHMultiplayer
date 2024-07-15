/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import client.Main;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class MobRotUpdateMessage extends EntityUpdateMessage {

    private float w;
    private float x;
    private float y;
    private float z;

    public MobRotUpdateMessage() {
    }

    public MobRotUpdateMessage(int id, Quaternion rot) {
        super(id);
        this.w = rot.getW();
        this.x = rot.getX();
        this.y = rot.getY();
        this.z = rot.getZ();
    }

    public Quaternion getRot() {
        return new Quaternion(x, y, z, w);
    }

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id=" + id + " ,rot=" + getRot() + '}';
    }

    @Override
    public void handleServer(ServerMain server) {
        if (entityExistsLocallyServer(id)) {
            Main.getInstance().enqueue(() -> {
            ServerMain.getInstance().getLevelManagerMobs().get(id).getNode().setLocalRotation(getRot());
            });
        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        if (entityExistsLocallyClient(id)) {
            getMobByIdClient(id).setServerRotation(getRot());
        }
    }

}
