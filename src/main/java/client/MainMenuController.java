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
import game.items.ItemTemplates;
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

    public static void startServerAndJoinLobby() {
        goToLobbyScreenHost();

        Main instance = Main.getInstance();
        ServerMain sm = new ServerMain(instance.getAssetManager(), instance.getRenderManager());
        instance.getStateManager().attach(sm);

        var clientGameState = new ClientGameAppState(Main.getInstance(), "localhost");
        instance.getStateManager().attach(clientGameState);
        MainMenuAppState.setClient(clientGameState);

        var teamViewer = new LobbyTeamViewAppState();
        instance.getStateManager().attach(teamViewer);

        System.out.println("server started");
    }

    public static void joinLobby() {
        
        MainMenuController.goToLobbyScreenClient();

        System.out.println("joining lobby");
        Element textFieldElement = MainMenuAppState.getNifty().getCurrentScreen().findElementById("ip-textfield");
        TextField textFieldControl = textFieldElement.getNiftyControl(TextField.class);
        String serverIpAddress = textFieldControl.getDisplayedText();

        Main instance = Main.getInstance();
        var clientGameState = new ClientGameAppState(Main.getInstance(), serverIpAddress);
        instance.getStateManager().attach(clientGameState);
        MainMenuAppState.setClient(clientGameState);

        var teamViewer = new LobbyTeamViewAppState();
        instance.getStateManager().attach(teamViewer);
        System.out.println("attaching team viewer");

    }

    public static void createLobby() {
        startServerAndJoinLobby();
    }

    public static void goToLobbyScreenClient() {
        var nifty = MainMenuAppState.getNifty();
        nifty.gotoScreen("LobbyClient");
    }

    public static void goToLobbyScreenHost() {
        var nifty = MainMenuAppState.getNifty();
        nifty.gotoScreen("LobbyHost");
    }

    // LOBBY
    public static String getClassPanelWidth() {
        var nifty = MainMenuAppState.getNifty();
        int screenHeight = nifty.getRenderEngine().getHeight();

        int buttonHeight = (int) (screenHeight * 0.075);
        return buttonHeight * 3 + "px";
    }

    public static String getClassPanelHeight() {
        var nifty = MainMenuAppState.getNifty();
        int screenHeight = nifty.getRenderEngine().getHeight();

        int buttonHeight = (int) (screenHeight * 0.075);
        return buttonHeight + "px";
    }

    public static void startGame() {
        ServerMain.getInstance().startGame();
    }

    public static void leaveLobby() {
        Main instance = Main.getInstance();

        MainMenuAppState m = instance.getStateManager().getState(MainMenuAppState.class);
        instance.getStateManager().detach(m);
    }

    public static void chooseAssault() {
        LobbyTeamViewAppState.changeClass(0);
        System.out.println("chosen assault.");
    }

    public static void chooseMedic() {
        LobbyTeamViewAppState.changeClass(1);

        System.out.println("chosen medic.");
    }

    public static void chooseCombatEngineer() {
        LobbyTeamViewAppState.changeClass(2);
        System.out.println("chosen combat engineer.");
    }
}
