/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientGui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author 48793
 */
public class MainMenuAppState extends BaseAppState {

    private Nifty nifty;

    public MainMenuAppState() {
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
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                getApplication().getAssetManager(),
                getApplication().getInputManager(),
                getApplication().getAudioRenderer(),
                getApplication().getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/MainMenuLayout.xml", "MainMenu");

        getApplication().getGuiViewPort().addProcessor(niftyDisplay);
        ((SimpleApplication) getApplication()).getFlyByCamera().setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        // <screen>
//        nifty.addScreen("MainMenu", new ScreenBuilder("Hello Nifty Screen") {
//            {
//                controller(new MainMenuScreenController()); // Screen properties
//                layer(new LayerBuilder("modifiableLayer") {
//                    {
//                        childLayoutVertical(); // layer properties, add more...
//
//                                // GUI elements
//                                control(new ButtonBuilder("Play", "Play") {
//                                    {
//                                        paddingLeft("72%");
//                                        paddingTop("30%");
////                                        alignCenter();
////                                        valignCenter();
//                                        height("10%");
//                                        width("25%");
//                                        color("#A9A9A9");
//                                        visibleToMouse(true);
//                                    }
//                                });
//
//                    }
//                });
//            }
//        }.build(nifty));
//        // </screen>

            

        nifty.gotoScreen("MainMenu"); // start the screen   
//        gs.getFlyCam().setDragToRotate(false);
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {

    }

}
