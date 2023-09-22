/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.Chest;
import game.entities.mobs.Mob;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import java.util.ArrayList;
import lombok.Getter;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new item.
 */
@Serializable
public class ChestItemInteractionMessage extends AbstractMessage {

    @Getter
    protected int itemId;
    
    @Getter
    protected int chestId;
    
    protected int interactionTypeIndex;

    public ChestItemInteractionMessage() {
    }

    public ChestItemInteractionMessage(Item item, Chest chest, ChestItemInteractionType type) {
        this.itemId = item.getId();
        this.chestId = chest.getId();
        this.interactionTypeIndex = type.ordinal();
    }

    public enum ChestItemInteractionType {
        INSERT,
        TAKE_OUT
    }

    public ChestItemInteractionType getInteractionType() {
        return ChestItemInteractionType.values()[interactionTypeIndex];
    }

}
