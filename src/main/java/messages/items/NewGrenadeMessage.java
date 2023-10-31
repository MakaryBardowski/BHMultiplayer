/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import lombok.Getter;

/**
 *  
 * @author 48793
 * this type of message is sent by the server and informs about a new item.
 */
@Serializable
@Getter
public class NewGrenadeMessage extends NewItemMessage {


    public NewGrenadeMessage() {
    }

    public NewGrenadeMessage(Item item) {
        super(item);
    }

}
