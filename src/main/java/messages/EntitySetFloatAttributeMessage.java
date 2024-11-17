/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.FloatAttribute;
import game.entities.InteractiveEntity;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class EntitySetFloatAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;
    @Getter
    private int attributeId;
    @Getter
    private float attributeValue;

    public EntitySetFloatAttributeMessage() {
    }

    public EntitySetFloatAttributeMessage(InteractiveEntity entity, int attributeId, float attributeValue) {
        this.entityId = entity.getId();
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        var entity = server.getLevelManagerMobs().get(entityId);
        entity.setFloatAttributeAndNotifyClients(attributeId, attributeValue);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setFloatAttribute(attributeId, attributeValue);

        entity.attributeChangedNotification(attributeId,new FloatAttribute(attributeValue));

    }

}
