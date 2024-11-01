/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.IntegerAttribute;
import game.entities.InteractiveEntity;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class EntitySetIntegerAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;
    @Getter
    private int attributeId;
    @Getter
    private int attributeValue;

    public EntitySetIntegerAttributeMessage() {
        setReliable(true);
    }

    public EntitySetIntegerAttributeMessage(InteractiveEntity entity, int attributeId, int attributeValue) {
        this.entityId = entity.getId();
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        var entity = server.getLevelManagerMobs().get(entityId);
        entity.setIntegerAttributeAndNotifyClients(attributeId, attributeValue);
        server.getServer().broadcast(this);

    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setIntegerAttribute(attributeId, attributeValue);
        entity.attributeChangedNotification(attributeId, new IntegerAttribute(attributeValue));
    }

}
