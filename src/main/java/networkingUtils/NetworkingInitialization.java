/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package networkingUtils;

import messages.messageListeners.ServerMessageListener;
import messages.DestructibleHealthUpdateMessage;
import messages.MobUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.NewMobMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import messages.NewChestMessage;
import messages.items.ItemInteractionMessage;
import messages.items.NewBootsMessage;
import messages.items.NewGlovesMessage;
import messages.items.NewHelmetMessage;
import messages.items.NewItemMessage;
import messages.items.NewVestMessage;

/**
 *
 * @author 48793
 */
public class NetworkingInitialization {

    public static final int PORT = 6000;

    public static void initializeSerializables() {
        Serializer.registerClass(MobUpdateMessage.class);
        Serializer.registerClass(MobPosUpdateMessage.class);
        Serializer.registerClass(MobRotUpdateMessage.class);
        Serializer.registerClass(DestructibleHealthUpdateMessage.class);
        Serializer.registerClass(PlayerJoinedMessage.class);
        Serializer.registerClass(SetPlayerMessage.class);
        Serializer.registerClass(NewMobMessage.class);
        Serializer.registerClass(ServerMessageListener.class);
        Serializer.registerClass(NewChestMessage.class);

        Serializer.registerClass(NewItemMessage.class);
        Serializer.registerClass(NewHelmetMessage.class);
        Serializer.registerClass(NewVestMessage.class);
        Serializer.registerClass(NewBootsMessage.class);
        Serializer.registerClass(NewGlovesMessage.class);
        Serializer.registerClass(ItemInteractionMessage.class);

    }

}
