/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import game.items.Equippable;
import game.items.Item;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import messages.MobRotUpdateMessage;
import messages.items.ItemInteractionMessage;
import messages.items.ItemInteractionMessage.ItemInteractionType;

/**
 *
 * @author 48793
 */
public class PlayerHUDController implements ScreenController {

    private static ClientGameAppState gs;

    public PlayerHUDController(ClientGameAppState gs) {
        this.gs = gs;
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

    public static void playerEquipItem(String strIndex) {
        int i = Integer.parseInt(strIndex);
        Item item = gs.getPlayer().getEquipment()[i];
        gs.getPlayer().equip(item);
        sendEquipMessageToServer(item);

    }
        
    private static void sendEquipMessageToServer(Item item){
            if (item != null) {
            ItemInteractionMessage imsg = new ItemInteractionMessage(item, gs.getPlayer(), ItemInteractionType.EQUIP);
            gs.getClient().send(imsg);
        }
    }

}
