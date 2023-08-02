package client;

import clientGui.MainMenuAppState;
import com.jme3.app.DetailedProfilerState;
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
    private static Main instance;
    
    private static final short STARTING_RESOLUTION_WIDTH = 1920;
    private static final short STARTING_RESOLUTION_HEIGHT = 1080;

    
    public static void main(String[] args) {
        Main app = new Main();
        setupSettings(app);
        app.start();

    }

    @Override
    public void simpleInitApp() {
        speed = 1f;
        instance = this;
//        stateManager.attach(new MainMenuAppState());

        this.start(JmeContext.Type.Display);
        stateManager.attach(new ClientGameAppState(this));
        
        
        DetailedProfilerState dps = new DetailedProfilerState();
//        stateManager.attach(dps);
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

    private static void setupSettings(SimpleApplication app) {
        AppSettings settings1 = new AppSettings(true);
        settings1.setResolution(STARTING_RESOLUTION_WIDTH, STARTING_RESOLUTION_HEIGHT);
        settings1.setFullscreen(true);
        settings1.setFrameRate(2000);
        
        
//                settings1.setResolution(800, 800);
//        app.setDisplayFps(false);
//        app.setDisplayStatView(false);
//        settings1.setCenterWindow(false);
//asd
//        if (new Random().nextInt(2) == 0) {
//            settings1.setWindowXPosition(0);
//        } else {
//            settings1.setWindowXPosition(1000);
//        }
//        
        settings1.setTitle("BH");
        app.setSettings(settings1);
        System.out.println(settings1);
        app.setPauseOnLostFocus(false);
        app.setSettings(settings1);
    }

    public static Main getInstance() {
        return instance;
    }
    
    }
