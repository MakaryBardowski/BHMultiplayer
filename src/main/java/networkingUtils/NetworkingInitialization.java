/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package networkingUtils;

import messages.messageListeners.ServerMessageListener;
import messages.MobHealthUpdateMessage;
import messages.MobUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.MobsInGameMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;

/**
 *
 * @author 48793
 */
public class NetworkingInitialization {
    public static final int PORT = 6000;
    
    
    public static void initializeSerializables(){
    Serializer.registerClass(MobUpdateMessage.class);
    Serializer.registerClass(MobPosUpdateMessage.class);
    Serializer.registerClass(MobRotUpdateMessage.class);
    Serializer.registerClass(MobHealthUpdateMessage.class);
    Serializer.registerClass(PlayerJoinedMessage.class);
    Serializer.registerClass(SetPlayerMessage.class);
    Serializer.registerClass(MobsInGameMessage.class);
    Serializer.registerClass(ServerMessageListener.class);
    }
    
    

    
}
