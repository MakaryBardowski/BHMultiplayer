/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Networking.Client;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author 48793
 */
public class PlayerHUDController implements ScreenController{
    private static ClientMain gs;

    public PlayerHUDController(ClientMain gs) {
        this.gs =gs;
    }
    
    
    
    

  @Override
    public void bind(Nifty nifty, Screen screen) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
    @Override
    public void onStartScreen() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onEndScreen() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public static void playerEquipItem(String strIndex){
        int i = Integer.parseInt(strIndex);
        gs.getPlayer().equipItem(gs.getPlayer().getEquipment()[i]); 
    }
    
    
}
