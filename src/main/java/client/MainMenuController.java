/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import game.items.Equippable;
import game.items.Item;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import messages.MobRotUpdateMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.MobItemInteractionMessage.ItemInteractionType;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class MainMenuController implements ScreenController {

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

    public static void startServer() {
        Element textFieldElement = MainMenuAppState.getNifty().getCurrentScreen().findElementById("host");
        Button textFieldControl = textFieldElement.getNiftyControl(Button.class);
        textFieldControl.setText("server hosted (use your ip)");

        Main instance = Main.getInstance();
        ServerMain sm = new ServerMain(instance.getAssetManager(), instance.getRenderManager());
        instance.getStateManager().attach(sm);

        instance.getStateManager().attach(new ClientGameAppState(Main.getInstance(), "localhost"));

        MainMenuAppState m = instance.getStateManager().getState(MainMenuAppState.class);
        instance.getStateManager().detach(m);
    }

    public static void joinGame() {
        Element textFieldElement = MainMenuAppState.getNifty().getCurrentScreen().findElementById("ip-textfield");
        TextField textFieldControl = textFieldElement.getNiftyControl(TextField.class);
        String serverIpAddress = textFieldControl.getDisplayedText();

        Main instance = Main.getInstance();
        var clientGameState = new ClientGameAppState(Main.getInstance(), serverIpAddress);
        instance.getStateManager().attach(clientGameState);

        MainMenuAppState m = instance.getStateManager().getState(MainMenuAppState.class);
        instance.getStateManager().detach(m);
    }

//    public static void playerEquipItem(String strIndex) {
//        int i = Integer.parseInt(strIndex);
//        Item item = gs.getPlayer().getEquipment()[i];
//        gs.getPlayer().equip(item);
//        sendEquipMessageToServer(item);
//
//    }
//        
//    private static void sendEquipMessageToServer(Item item){
//            if (item != null) {
//            MobItemInteractionMessage imsg = new MobItemInteractionMessage(item, gs.getPlayer(), ItemInteractionType.EQUIP);
//            gs.getClient().send(imsg);
//        }
//    }
}
