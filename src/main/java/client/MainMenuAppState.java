/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import lombok.AllArgsConstructor;

/**
 *
 * @author 48793
 */
@AllArgsConstructor
public class MainMenuAppState extends AbstractAppState {

    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final AudioRenderer audioRenderer;
    private final ViewPort guiViewPort;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {        // rejestrujemy klasy serializowalne (nie musicie rozumiec, architektura klient-serwer)
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/MainMenu.xml", "MainMenu");
//        nifty.setDebugOptionPanelColors(true);
        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
    }

}
