/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import lombok.Getter;

/**
 *  
 * @author 48793
 * this type of message is sent by the server and informs about a new item.
 */
@Serializable
@Getter
public class NewItemMessage extends AbstractMessage {

    protected int id;
    protected int templateIndex;
    protected boolean droppable;

    public NewItemMessage() {
    }

    public NewItemMessage(Item item) {
        this.id = item.getId();
        this.templateIndex = item.getTemplate().getTemplateIndex();
        this.droppable = item.isDroppable();
    }
    
    public ItemTemplate getTemplate(){
    return ItemTemplates.templates.get(templateIndex);
    }
    

}
