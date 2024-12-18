/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import events.EventPublisher;
import game.entities.mobs.Mob;
import game.map.collision.WorldGrid;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import messages.DeleteEntityMessage;
import messages.EntitySetFloatAttributeMessage;
import messages.EntitySetIntegerAttributeMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Getter
public abstract class InteractiveEntity extends EventPublisher{
    private static final DeleteEntityMessage dem = new DeleteEntityMessage();
    @Getter
    protected ConcurrentHashMap<Integer, Attribute> attributes = new ConcurrentHashMap<>(0);

    protected int id;
    @Setter
    protected String name;
    protected Node node;

    public InteractiveEntity(int id, String name, Node node) {
        this.id = id;
        this.name = name;
        this.node = node;
    }

    public abstract void onShot(Mob shooter, float damage);

    public abstract void onInteract();

    public abstract void setPosition(Vector3f newPos);

    public abstract void setPositionServer(Vector3f newPos);

    public final void destroyAndNotifyClients() {
        destroyServer();
//        var dem = new DeleteEntityMessage(id);
        dem.setId(id);
        ServerMain.getInstance().getServer().broadcast(dem);
    }
    // this should be abstract!
    public void destroyClient(){};
    // this should be abstract!

    public void destroyServer(){};

    public abstract AbstractMessage createNewEntityMessage();

    public void attributeChangedNotification(int attributeId, Attribute copyOfAttribute) {}

    public void setFloatAttributeAndNotifyClients(int attributeId, float val) {
        setFloatAttribute(attributeId, val);
        var msg = new EntitySetFloatAttributeMessage(this, attributeId, val);
        ServerMain.getInstance().getServer().broadcast(msg);
    }

    public void setIntegerAttributeAndNotifyClients(int attributeId, int val) {
        setIntegerAttribute(attributeId, val);
        var msg = new EntitySetIntegerAttributeMessage(this, attributeId, val);
        ServerMain.getInstance().getServer().broadcast(msg);
    }

    public void setFloatAttribute(int attributeId, float val) {
        getFloatAttribute(attributeId).setValue(val);
    }

    public void setIntegerAttribute(int attributeId, int val) {
        getIntegerAttribute(attributeId).setValue(val);
    }

    public FloatAttribute getFloatAttribute(int attributeId) {
        return ((FloatAttribute) attributes.get(attributeId));
    }

    public IntegerAttribute getIntegerAttribute(int attributeId) {
        return ((IntegerAttribute) attributes.get(attributeId));
    }

    @Override
    public int hashCode() {
        return id;
    }
    
}
