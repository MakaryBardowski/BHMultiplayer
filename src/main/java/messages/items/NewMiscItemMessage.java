package messages.items;

import client.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import server.ServerMain;

@Serializable
public class NewMiscItemMessage extends NewItemMessage{

    public NewMiscItemMessage() {
    }

    public NewMiscItemMessage(Item item) {
        super(item);
    }

    @Override
    public void handleServer(ServerMain server, HostedConnection sender) {
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Item i = (Item) ifa.createItem(id, getTemplate(), droppable);
        client.registerEntity(i);
    }
}
