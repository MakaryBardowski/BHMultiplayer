package client;

import com.jme3.app.DetailedProfilerState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.ClientStateListener;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.Nifty;
import java.util.Random;
import com.jme3.niftygui.NiftyJmeDisplay;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import networkingUtils.NetworkingInitialization;
import server.ServerMain;

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
    private static final boolean FULLSCREEN = true;
//    private static final short STARTING_RESOLUTION_WIDTH = 1000;
//    private static final short STARTING_RESOLUTION_HEIGHT = 800;
//    private static final boolean FULLSCREEN = false;

    public static void main(String[] args) {
        Main app = new Main();
        setupSettings(app);

        app.start();

    }

    @Override
    public void simpleInitApp() {
        System.out.println("app camera" +cam); 
        flyCam.setMoveSpeed(0); //30
        flyCam.setRotationSpeed(0); //1.0
        speed = 1f;
        instance = this;
        NetworkingInitialization.initializeSerializables();

        DetailedProfilerState dps = new DetailedProfilerState();
//        stateManager.attach(dps);

        ScreenshotAppState screenShotState = new ScreenshotAppState();
        stateManager.attach(screenShotState);

        MainMenuAppState mms = new MainMenuAppState(assetManager, inputManager, audioRenderer, guiViewPort);
        stateManager.attach(mms);
    }

    @Override
    public void gainFocus() {
        if (ClientGameAppState.getInstance() != null && ClientGameAppState.getInstance().getPlayer() != null) {
            PlayerHUD.itemDropTooltipNode.removeFromParent();
            ClientGameAppState.getInstance().getPlayer().setLastTargetedItem(null);
        }
        super.gainFocus();
    }

    @Override
    public void loseFocus() {
        if (ClientGameAppState.getInstance() != null && ClientGameAppState.getInstance().getPlayer() != null) {
            PlayerHUD.itemDropTooltipNode.removeFromParent();
            ClientGameAppState.getInstance().getPlayer().setLastTargetedItem(null);
        }
        super.loseFocus();
    }

    @Override
    public void simpleUpdate(float tpf
    ) {
//        System.out.println("main cam "+cam);
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm
    ) {
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
        settings1.setFullscreen(FULLSCREEN);
        settings1.setVSync(true);
        settings1.setFrameRate(2000);
        settings1.setResizable(true);
        settings1.setRenderer(AppSettings.LWJGL_OPENGL45);

        
        // the camera and gui bug occurs when the player joins the game with minimized window
//        settings1.setFullscreen(false);
//        settings1.setResolution(950, 800);
//        app.setDisplayFps(false);
//        app.setDisplayStatView(false);
//        settings1.setCenterWindow(false);
//        settings1.setWindowXPosition(1000);

        settings1.setTitle("BH");
        app.setPauseOnLostFocus(false);
        app.setSettings(settings1);

    }

    public static Main getInstance() {
        return instance;
    }

}
