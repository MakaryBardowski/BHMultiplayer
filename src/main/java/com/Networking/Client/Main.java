package com.Networking.Client;

import com.Networking.Client.GUI.MainMenuAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.network.ClientStateListener;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import java.util.Random;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
        
    }

    @Override
    public void simpleInitApp() {
//        stateManager.attach(new MainMenuAppState());
        
        AppSettings settings1 = new AppSettings(true);
        settings1.setResolution(1920, 1080);
        settings1.setFullscreen(true);
        settings1.setCenterWindow(false);
        if (new Random().nextInt(2) == 0) {
            settings1.setWindowXPosition(0);
        } else {
            settings1.setWindowXPosition(1000);
        }
        this.setPauseOnLostFocus(false);
        this.setSettings(settings1);
        this.start(JmeContext.Type.Display);

        stateManager.attach(new ClientGamAppState(this));
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public FlyByCamera getFlyCam() {
        return flyCam;
    }

    public AppSettings getAppSettings() {
        return settings;
    }
}
