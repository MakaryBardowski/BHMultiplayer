/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package networkingUtils;

import messages.items.*;
import messages.messageListeners.ServerMessageListener;
import messages.SystemHealthUpdateMessage;
import messages.EntityUpdateMessage;
import messages.MobPosRotUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.NewMobMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import messages.AnimationPlayedMessage;
import messages.DeleteEntityMessage;
import messages.DestructibleDamageReceiveMessage;
import messages.DestructibleHealReceiveMessage;
import messages.EntitySetFloatAttributeMessage;
import messages.EntitySetIntegerAttributeMessage;
import messages.GrenadePosUpdateMessage;
import messages.GrenadeThrownMessage;
import messages.HitscanTrailMessage;
import messages.NewChestMessage;
import messages.PlayerPosUpdateRequest;
import messages.InstantEntityPosCorrectionMessage;
import messages.NewDestructibleDecorationMessage;
import messages.NewIndestructibleDecorationMessage;
import messages.ThrownGrenadeExplodedMessage;
import messages.bulkMessages.BulkMessage;
import messages.gameSetupMessages.GameInfoOnStartMessage;
import messages.gameSetupMessages.NextLevelMessage;
import messages.lobby.GameStartedMessage;
import messages.lobby.HostChangedNicknameMessage;
import messages.lobby.HostChangedPlayerClassMessage;
import messages.lobby.HostJoinedLobbyMessage;

/**
 *
 * @author 48793
 */
public class NetworkingInitialization {

    public static final int PORT = 6000;

    public static void initializeSerializables() {
        Serializer.registerClass(HostJoinedLobbyMessage.class);
        Serializer.registerClass(HostChangedNicknameMessage.class);
        Serializer.registerClass(GameStartedMessage.class);
        Serializer.registerClass(HostChangedPlayerClassMessage.class);
        Serializer.registerClass(GameInfoOnStartMessage.class);

        Serializer.registerClass(EntityUpdateMessage.class);
        Serializer.registerClass(MobPosRotUpdateMessage.class);
        Serializer.registerClass(MobRotUpdateMessage.class);
        Serializer.registerClass(SystemHealthUpdateMessage.class);
        Serializer.registerClass(PlayerJoinedMessage.class);
        Serializer.registerClass(SetPlayerMessage.class);
        Serializer.registerClass(NewMobMessage.class);

        Serializer.registerClass(ServerMessageListener.class);
        Serializer.registerClass(NewChestMessage.class);
        Serializer.registerClass(DestructibleHealReceiveMessage.class);

        Serializer.registerClass(DestructibleDamageReceiveMessage.class);
        Serializer.registerClass(GrenadePosUpdateMessage.class);
        Serializer.registerClass(EntitySetIntegerAttributeMessage.class);
        Serializer.registerClass(EntitySetFloatAttributeMessage.class);
        Serializer.registerClass(NextLevelMessage.class);
        Serializer.registerClass(BulkMessage.class);
        Serializer.registerClass(AnimationPlayedMessage.class);

        Serializer.registerClass(NewMiscItemMessage.class);
        Serializer.registerClass(NewItemMessage.class);
        Serializer.registerClass(NewHelmetMessage.class);
        Serializer.registerClass(NewVestMessage.class);
        Serializer.registerClass(NewBootsMessage.class);
        Serializer.registerClass(NewGlovesMessage.class);
        Serializer.registerClass(NewRangedWeaponMessage.class);
        Serializer.registerClass(NewGrenadeMessage.class);
        Serializer.registerClass(NewAmmoPackMessage.class);
        Serializer.registerClass(GrenadeThrownMessage.class);
        Serializer.registerClass(NewMeleeWeaponMessage.class);
        Serializer.registerClass(MobItemInteractionMessage.class);
        Serializer.registerClass(ChestItemInteractionMessage.class);
        Serializer.registerClass(HitscanTrailMessage.class);
        Serializer.registerClass(SetDefaultItemMessage.class);
        Serializer.registerClass(PlayerPosUpdateRequest.class);
        Serializer.registerClass(InstantEntityPosCorrectionMessage.class);
        Serializer.registerClass(NewDestructibleDecorationMessage.class);
        Serializer.registerClass(NewIndestructibleDecorationMessage.class);
        Serializer.registerClass(DeleteEntityMessage.class);

        Serializer.registerClass(ThrownGrenadeExplodedMessage.class);

    }

}
