/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import game.items.armor.Gloves;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new item.
 */
@Serializable
@Getter
public class NewGlovesMessage extends NewArmorMessage {

    public NewGlovesMessage() {
    }

    public NewGlovesMessage(Gloves item) {
        super(item);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Gloves i = (Gloves) ifa.createItem(id, getTemplate(), droppable);
        i.setArmorValue(armorValue);
        client.registerEntity(i);

    }

}
