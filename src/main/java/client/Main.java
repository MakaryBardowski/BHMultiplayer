package client;

import com.jme3.app.DetailedProfilerState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import game.map.blocks.VoxelLighting;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import networkingUtils.NetworkingInitialization;

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
    public static final boolean WIREFRAME = false;
    public static final float CAM__MOVE_SPEED = 30;
    public static final float CAM_ROT_SPEED = 1.0f;

    private double used_mem_debug = 0;
    private double max_mem_debug = 0;
    private static double previous_max_mem_debug = 0;
    private double time_from_start_millis_debug = 0;

    public static void main(String[] args) {
        Main app = new Main();
        setupSettings(app);

        app.start();
        previous_max_mem_debug = Runtime.getRuntime().totalMemory() / (1024 * 1024);
    }

    @Override
    public void simpleInitApp() {
        setDisplayFps(false);
        setDisplayStatView(false);
        System.out.println("app camera" + cam);
        flyCam.setMoveSpeed(0); //30
        flyCam.setRotationSpeed(0); //1.0
        speed = 1f;
        instance = this;
        NetworkingInitialization.initializeSerializables();

        DetailedProfilerState dps = new DetailedProfilerState();
        stateManager.attach(dps);

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
//        time_from_start_millis_debug += tpf;
//
//        used_mem_debug = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
//        max_mem_debug = Runtime.getRuntime().totalMemory() / 1048576;
//        System.out.print(used_mem_debug);
//        System.out.print("/");
//        System.out.println(max_mem_debug);
//        if (previous_max_mem_debug != max_mem_debug) {
//            previous_max_mem_debug = max_mem_debug;
//            System.out.println("max mem changed this many seconds from start: " + (time_from_start_millis_debug));
//        }
//
//        if (used_mem_debug > 1500) {
//            System.err.println("\n\n\n\n\nMEMORY LEAK. CLOSING FROM MAIN\n\n\n\n\n");
//            System.exit(1);
//        }
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

//        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        DisplayMode[] modes = device.getDisplayModes();
//        int i = 0; // note: there are usually several, let's pick the first
//        settings1.setResolution(modes[i].getWidth(), modes[i].getHeight());
        settings1.setResolution(STARTING_RESOLUTION_WIDTH, STARTING_RESOLUTION_HEIGHT);
//        settings1.setResolution(1680, 1050);
        settings1.setFullscreen(FULLSCREEN);
        settings1.setVSync(true);
        settings1.setFrameRate(2000);
        settings1.setResizable(true);
        settings1.setRenderer(AppSettings.LWJGL_OPENGL45);
//        settings1.setVSync(false);
//        settings1.setFrameRate(144);

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
