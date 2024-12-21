/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import static client.MainMenuController.chooseAssault;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.SizeValueType;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.items.ItemTemplates;
import game.items.ItemTemplates.HelmetTemplate;
import game.items.ItemTemplates.ItemTemplate;
import game.map.blocks.BlockType;
import static game.map.blocks.VoxelLighting.setupModelLight;
import java.nio.FloatBuffer;
import lombok.Setter;
import messages.lobby.HostChangedNicknameMessage;
import messages.lobby.HostChangedPlayerClassMessage;

/**
 *
 * @author 48793
 */
public class LobbyTeamViewAppState extends BaseAppState {

    private static AssetManager assetManager;
    private static final Node backgroundNode = new Node("Background Node");
    private static final Node teamNode = new Node("Team Node");
    private static float updateTimer = 0.1f;
    @Setter
    private static String currentNickname = "";
    private static ClientGameAppState client;

    @Override
    protected void initialize(Application app) {
        client = MainMenuAppState.getClient();
        assetManager = app.getAssetManager();
        setupTeamViewScene();
        System.out.println("TEAM VIEWRER!");

    }

    @Override
    protected void cleanup(Application app) {
        app.getViewPort().setBackgroundColor(ColorRGBA.Black);
        backgroundNode.removeFromParent();
        teamNode.removeFromParent();
    }

    @Override
    protected void onEnable() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void onDisable() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void setupTeamViewScene() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.5f));
        backgroundNode.addLight(al);

        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Cyan);

        Main.getInstance().getRootNode().attachChild(backgroundNode);
        backgroundNode.attachChild(teamNode);

        generateBackground();

        var startCamLoc = getApplication().getCamera().getLocation().clone().setY(6).setZ(6.1f);
        getApplication().getCamera().setLocation(startCamLoc);

        changeClass(0);
    }

    private void generateBackground() {
        for (int i = -6; i < 6; i++) {
            for (int j = -6; j < 6; j++) {
                var wall = createWallCube();
                backgroundNode.attachChild(wall);
                wall.move(i * 8, 0, j * 8);
            }
        }

        for (int i = -6; i < 6; i++) {
            for (int j = -6; j < -5; j++) {
                for (int k = 1; k < 6; k++) {
                    var wall = createWallCube();
                    backgroundNode.attachChild(wall);
                    wall.move(i * 8, k * 8, j * 8);
                }
            }
        }
    }

    private Geometry createWallCube() {
        Box box = new Box(4, 4, 4);

        Geometry geom = new Geometry("TeamViewerWall", box);

        Texture t = assetManager.loadTexture(BlockType.STONE.textureName);
        t.setMagFilter(Texture.MagFilter.Nearest);

        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", t);
        mat1.setBoolean("VertexColor", true);

        geom.setMaterial(mat1);

        FloatBuffer buf = BufferUtils.createFloatBuffer(box.getVertexCount() * 4);

        for (int i = 0; i < box.getVertexCount() * 4; i += 4) {
            buf.put(0.3f);
            buf.put(0.3f);
            buf.put(0.3f);
            buf.put(1f);
        }
        buf.flip();
        box.setBuffer(VertexBuffer.Type.Color, 4, buf);

        return geom;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        updateTimer -= tpf;
        if (updateTimer <= 0
                && client != null
                && client.getClient().getId() != -1
                && MainMenuAppState.getNifty().getCurrentScreen().getScreenId().contains("Lobby")) {
            Element textFieldElement = MainMenuAppState.getNifty().getCurrentScreen().findElementById("name-textfield");
            TextField textFieldControl = textFieldElement.getNiftyControl(TextField.class);

            String currentNickname = textFieldControl.getText();

            if (!currentNickname.equals("") && !currentNickname.equals(this.currentNickname)) {
                var nickMsg = new HostChangedNicknameMessage(client.getClient().getId(), currentNickname);
                client.getClient().send(nickMsg);
            }
        }

    }

    public static void changeClass(int classIndex) {
        teamNode.detachAllChildren();
        Spatial player = null;
        switch (classIndex) {
            case 0: {
                ItemTemplates.HelmetTemplate helmetTemplate = (ItemTemplates.HelmetTemplate) ItemTemplates.HEAD_1;
                var vestTemplate = ItemTemplates.TORSO_1;
                var glovesTemplate = ItemTemplates.HAND_1;
                var bootsTemplate = ItemTemplates.BOOTS_TRENCH;
                player = LobbyTeamViewAppState.loadPlayerDummy(helmetTemplate, vestTemplate, glovesTemplate, bootsTemplate);
                teamNode.attachChild(player);
                player.move(0, 4, 0);
                break;
            }
            case 1: {
                ItemTemplates.HelmetTemplate helmetTemplate = (ItemTemplates.HelmetTemplate) ItemTemplates.GAS_MASK;
                var vestTemplate = ItemTemplates.VEST_TRENCH;
                var glovesTemplate = ItemTemplates.HAND_1;
                var bootsTemplate = ItemTemplates.BOOTS_TRENCH;
                player = LobbyTeamViewAppState.loadPlayerDummy(helmetTemplate, vestTemplate, glovesTemplate, bootsTemplate);
                teamNode.attachChild(player);
                player.move(0, 4, 0);
                break;
            }
            case 2: {
                ItemTemplates.HelmetTemplate helmetTemplate = (ItemTemplates.HelmetTemplate) ItemTemplates.TRENCH_HELMET;
                var vestTemplate = ItemTemplates.VEST_TRENCH;
                var glovesTemplate = ItemTemplates.HAND_1;
                var bootsTemplate = ItemTemplates.BOOTS_TRENCH;
                player = LobbyTeamViewAppState.loadPlayerDummy(helmetTemplate, vestTemplate, glovesTemplate, bootsTemplate);
                teamNode.attachChild(player);
                player.move(0, 4, 0);
                break;
            }
            default:
                throw new IllegalArgumentException("class index " + classIndex + " is invalid");
        }
        player.rotate(0, FastMath.DEG_TO_RAD * 30, 0);
        var classChangedMsg = new HostChangedPlayerClassMessage(client.getClient().getId(), classIndex);
        client.getClient().send(classChangedMsg);

    }

    public static Node loadPlayerDummy(HelmetTemplate helmetTemplate, ItemTemplate vestTemplate, ItemTemplate glovesTemplate, ItemTemplate bootsTemplate) {

        Node skeleton = loadPlayerSkeleton();
        var skinningControl = skeleton.getChild(0).getControl(SkinningControl.class);

        // attach vest
        Node spine = skinningControl.getAttachmentsNode("Spine");
        Node vest = (Node) Main.getInstance().getAssetManager().loadModel(vestTemplate.getFpPath());
        setupModelLight(vest);
        spine.attachChild(vest);

        // attach helmet
        Node head = skinningControl.getAttachmentsNode("Head");
        Node helmet = (Node) Main.getInstance().getAssetManager().loadModel(helmetTemplate.getFpPath());
        setupModelLight(helmet);
        head.attachChild(helmet);

        if (!helmetTemplate.isReplacesHead()) {
            helmet.setLocalScale(1.02f);
            Node face = (Node) Main.getInstance().getAssetManager().loadModel(ItemTemplates.HEAD_1.getFpPath());
            setupModelLight(face);
            head.attachChild(face);
            ((Geometry) face.getChild(0)).getMaterial().getAdditionalRenderState().setPolyOffset(5, 5);
        }

        // attach hands
        Node gloveR = (Node) Main.getInstance().getAssetManager().loadModel(glovesTemplate.getFpPath());
        setupModelLight(gloveR);
        skeleton.attachChild(gloveR);

        // attach boots
        var verticalOffset = 0.44f;
        Node r = skinningControl.getAttachmentsNode("LegR");
        Node l = skinningControl.getAttachmentsNode("LegL");
        r.detachAllChildren();
        l.detachAllChildren();

        Node bootR = (Node) Main.getInstance().getAssetManager().loadModel(bootsTemplate.getFpPath().replace("?", "R"));
        setupModelLight(bootR);

        bootR.move(0, verticalOffset, 0);

        r.attachChild(bootR);

        Node bootL = (Node) Main.getInstance().getAssetManager().loadModel(bootsTemplate.getFpPath().replace("?", "L"));
        setupModelLight(bootL);
        bootL.move(0, verticalOffset, 0);

        l.attachChild(bootL);
        bootR.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
        bootL.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);

        return skeleton;
    }

    private static Node loadPlayerSkeleton() {
        Node node = (Node) assetManager.loadModel(HumanMob.HUMAN_SKELETON_RIG_PATH);
        return node;
    }

}
