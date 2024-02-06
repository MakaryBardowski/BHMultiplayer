/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.Chest;
import game.entities.mobs.Mob;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import java.util.ArrayList;
import lombok.Getter;
import messages.TwoWayMessage;
import server.ServerMain;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new item.
 */
@Serializable
public class ChestItemInteractionMessage extends TwoWayMessage {

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

    @Override
    public void handleServer(ServerMain server) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        handleChestItemInteraction(this);
    }

    public enum ChestItemInteractionType {
        INSERT,
        TAKE_OUT
    }

    public ChestItemInteractionType getInteractionType() {
        return ChestItemInteractionType.values()[interactionTypeIndex];
    }

    private void handleChestItemInteraction(ChestItemInteractionMessage cimsg) {
        enqueueExecution(() -> {
            if (null != cimsg.getInteractionType()) {
                switch (cimsg.getInteractionType()) {
                    case INSERT:
                        Item inserted = getItemByIdClient(cimsg.getItemId());
                        getChestByIdClient(cimsg.getChestId()).addToEquipment(inserted);
                        break;
                    case TAKE_OUT:
                        Item takenOut = getItemByIdClient(cimsg.getItemId());
                        getChestByIdClient(cimsg.getChestId()).removeFromEquipment(takenOut);
                        break;

                    default:
                        break;
                }
            }
        });
    }

}
