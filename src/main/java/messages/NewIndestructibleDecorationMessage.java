/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import client.Main;
import com.jme3.math.Vector3f;
import messages.items.*;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.DecorationTemplates;
import game.entities.DestructibleDecoration;
import game.entities.IndestructibleDecoration;
import game.entities.factories.DecorationFactory;
import game.items.Item;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new decoration.
 */
@Serializable
public class NewIndestructibleDecorationMessage extends TwoWayMessage {

    @Getter
    protected int id;
    protected int templateIndex;
    protected float x;
    protected float y;
    protected float z;

    public NewIndestructibleDecorationMessage() {
    }

    public NewIndestructibleDecorationMessage(IndestructibleDecoration decoration) {
        this.id = decoration.getId();
        this.templateIndex = decoration.getTemplate().getTemplateIndex();
        this.x = decoration.getNode().getWorldTranslation().getX();
        this.y = decoration.getNode().getWorldTranslation().getY();
        this.z = decoration.getNode().getWorldTranslation().getZ();
        setReliable(true);
    }

    public DecorationTemplates.DecorationTemplate getTemplate() {
        return DecorationTemplates.templates.get(templateIndex);
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        addNewIndestructibleDecoration(this);
    }

    private void addNewIndestructibleDecoration(NewIndestructibleDecorationMessage nmsg) {

        if (entityNotExistsLocallyClient(nmsg.getId())) {
            enqueueExecution(() -> {
                var d = DecorationFactory.createIndestructibleDecoration(nmsg.getId(), ClientGameAppState.getInstance().getDestructibleNode(), nmsg.getPos(), nmsg.getTemplate(), ClientGameAppState.getInstance().getAssetManager());
                ClientGameAppState.getInstance().getMobs().put(d.getId(), d);
                ClientGameAppState.getInstance().getGrid().insert(d);
            });
        }
    }
}
