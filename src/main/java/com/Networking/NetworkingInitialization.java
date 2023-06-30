/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Networking;

import Messages.MessageListeners.ServerMessageListener;
import Messages.MobHealthUpdateMessage;
import Messages.MobUpdateMessage;
import Messages.MobUpdatePosRotMessage;
import Messages.MobsInGameMessage;
import Messages.PlayerJoinedMessage;
import Messages.SetPlayerMessage;
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
    Serializer.registerClass(MobUpdatePosRotMessage.class);
    Serializer.registerClass(MobHealthUpdateMessage.class);
    Serializer.registerClass(PlayerJoinedMessage.class);
    Serializer.registerClass(SetPlayerMessage.class);
    Serializer.registerClass(MobsInGameMessage.class);
    Serializer.registerClass(ServerMessageListener.class);
    }
    
    

    
}
