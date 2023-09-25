/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package networkingUtils;

import messages.messageListeners.ServerMessageListener;
import messages.SystemHealthUpdateMessage;
import messages.MobUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.NewMobMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import messages.DestructibleDamageReceiveMessage;
import messages.HitscanTrailMessage;
import messages.NewChestMessage;
import messages.PlayerPosUpdateRequest;
import messages.InstantEntityPosCorrectionMessage;
import messages.items.ChestItemInteractionMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.NewBootsMessage;
import messages.items.NewGlovesMessage;
import messages.items.NewHelmetMessage;
import messages.items.NewItemMessage;
import messages.items.NewRifleMessage;
import messages.items.NewVestMessage;
import messages.items.SetDefaultItemMessage;

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
        Serializer.registerClass(SystemHealthUpdateMessage.class);
        Serializer.registerClass(PlayerJoinedMessage.class);
        Serializer.registerClass(SetPlayerMessage.class);
        Serializer.registerClass(NewMobMessage.class);
        Serializer.registerClass(ServerMessageListener.class);
        Serializer.registerClass(NewChestMessage.class);
        Serializer.registerClass(DestructibleDamageReceiveMessage.class);

        Serializer.registerClass(NewItemMessage.class);
        Serializer.registerClass(NewHelmetMessage.class);
        Serializer.registerClass(NewVestMessage.class);
        Serializer.registerClass(NewBootsMessage.class);
        Serializer.registerClass(NewGlovesMessage.class);
        Serializer.registerClass(NewRifleMessage.class);

        Serializer.registerClass(MobItemInteractionMessage.class);
        Serializer.registerClass(ChestItemInteractionMessage.class);
        Serializer.registerClass(HitscanTrailMessage.class);
        Serializer.registerClass(SetDefaultItemMessage.class);
        Serializer.registerClass(PlayerPosUpdateRequest.class);
        Serializer.registerClass(InstantEntityPosCorrectionMessage.class);

    }

}
