/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.DestructibleUtils;
import game.entities.factories.MobSpawnType;
import game.entities.mobs.Mob;
import game.map.blocks.VoxelLighting;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class NewMobMessage extends TwoWayMessage {

    @Getter
    private MobSpawnType mobType;
    @Getter
    private int id;
    @Getter
    private float health;
    private float x;
    private float y;
    private float z;

    public NewMobMessage() {
    }

    public NewMobMessage(Mob mob, Vector3f pos, MobSpawnType mobType) {
        this.id = mob.getId();
        this.mobType = mobType;
        this.health = mob.getHealth();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void handleServer(ServerMain server) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        addMob(this);
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    private void addMob(NewMobMessage nmsg) {
        if (entityNotExistsLocallyClient(nmsg.getId())) {
            enqueueExecution(() -> {

                Mob p = ClientGameAppState.getInstance().registerMob(nmsg.getId(), nmsg.getMobType());
                VoxelLighting.setupModelLight(p.getNode());
                DestructibleUtils.setupModelShootability(p.getNode(), p.getId());
                placeMob(nmsg.getPos(), p);

                p.setHealth(nmsg.getHealth());
            }
            );
        }
    }

}
