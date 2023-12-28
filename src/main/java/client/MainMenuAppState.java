/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import lombok.Getter;
import lombok.Setter;
import messages.lobby.HostChangedNicknameMessage;

/**
 *
 * @author 48793
 */
public class MainMenuAppState extends BaseAppState {
    
    @Getter
    private static Nifty nifty;
    
    private static NiftyJmeDisplay niftyDisplay;
    
    @Getter
    @Setter
    private static ClientGameAppState client;
    

    
    public MainMenuAppState(AssetManager assetManager, InputManager inputManager, AudioRenderer audioRenderer, ViewPort guiViewPort) {
    }
    
    @Override
    protected void initialize(Application app) {

        //It is technically safe to do all initialization and cleanup in the
        //onEnable()/onDisable() methods. Choosing to use initialize() and
        //cleanup() for this is a matter of performance specifics for the
        //implementor.
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
    }
    
    @Override
    protected void cleanup(Application app) {
        var lobbyState = app.getStateManager().getState(LobbyTeamViewAppState.class);
        app.getStateManager().detach(lobbyState);
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
    }

    //onEnable()/onDisable() can be used for managing things that should
    //only exist while the state is enabled. Prime examples would be scene
    //graph attachment or input listener attachment.
    public void bind(Nifty nifty, Screen screen) {
        
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void onStartScreen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void onEndScreen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    protected void onEnable() {
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                getApplication().getAssetManager(),
                getApplication().getInputManager(),
                getApplication().getAudioRenderer(),
                getApplication().getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/MainMenu.xml", "MainMenu");
        
        getApplication().getGuiViewPort().addProcessor(niftyDisplay);
        ((SimpleApplication) getApplication()).getFlyByCamera().setDragToRotate(true);
        
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

//        gs.getFlyCam().setDragToRotate(false);
    }
    
    @Override
    protected void onDisable() {
        if (nifty != null) {
            getApplication().getGuiViewPort().removeProcessor(niftyDisplay);
        }
    }
    
    @Override
    public void update(float tpf) {

        
    }
    

    
}
