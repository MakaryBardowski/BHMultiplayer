/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import game.items.armor.Armor;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new item.
 */
@Serializable
@Getter
public abstract class NewArmorMessage extends NewItemMessage {

    protected float armorValue;

    public NewArmorMessage() {
    }

    public NewArmorMessage(Armor item) {
        super(item);
        this.armorValue = item.getArmorValue();
    }

}
