/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.HumanMob;
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
public class SetDefaultItemMessage extends AbstractMessage {

    @Getter
    protected int itemId;
    
    @Getter
    protected int mobId;
    
    protected int interactionTypeIndex;

    public SetDefaultItemMessage() {
    }

    public SetDefaultItemMessage(Item item, HumanMob mob) {
        this.itemId = item.getId();
        this.mobId = mob.getId();
    }

}