/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author 48793
 */
    @Serializable
    public class MobUpdateMessage extends AbstractMessage{
        
        protected int id;
        
        public MobUpdateMessage(){}
        
        public MobUpdateMessage(int id){
        this.id = id;
        }

    public int getId() {
        return id;
    }
        
        
        
    }