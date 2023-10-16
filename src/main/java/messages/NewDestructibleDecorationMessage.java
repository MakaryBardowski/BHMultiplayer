/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.math.Vector3f;
import messages.items.*;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.DecorationTemplates;
import game.entities.DestructibleDecoration;
import game.items.Item;
import lombok.Getter;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new decoration.
 */
@Serializable
public class NewDestructibleDecorationMessage extends AbstractMessage {

    @Getter
    protected int id;
    protected int templateIndex;
    protected float x;
    protected float y;
    protected float z;

    public NewDestructibleDecorationMessage() {
    }

    public NewDestructibleDecorationMessage(DestructibleDecoration decoration) {
        this.id = decoration.getId();
        this.templateIndex = decoration.getTemplate().getTemplateIndex();
        this.x = decoration.getNode().getWorldTranslation().getX();
        this.y = decoration.getNode().getWorldTranslation().getY();
        this.z = decoration.getNode().getWorldTranslation().getZ();

    }

    public DecorationTemplates.DecorationTemplate getTemplate() {
        return DecorationTemplates.templates.get(templateIndex);
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

}
