/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioRenderer;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Ray;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ElementBuilder;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DraggableBuilder;
import de.lessvoid.nifty.controls.dragndrop.builder.DroppableBuilder;
import de.lessvoid.nifty.controls.label.LabelControl;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.controls.textfield.TextFieldControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.Player;
import game.items.Item;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class PlayerHUD extends BaseAppState {

    private static final int HP_BAR_TOP_MARGIN = -92;
    private final double EQ_MARGIN = 0;
    private final int ROWS = 6;

    private Nifty nifty;
    private ClientGameAppState gs;
    private int cnt = 0;
    private int hotBarCnt = 0;
    private double eqSlotSizePx;
    private int equipmentSlotAdded = 0;

    private float percentMobHealthbarLength = 0.33f;
    private float percentMobHealthbarHeight = 0.01f;
    private float playerHealthbarWidth = 0.15f;

    // item drop tooltip
    public static Node itemDropTooltipNode = new Node();
    private Geometry itemDropTooltipGeom;
    private Material itemDropTooltipMaterial;
    private NiftyJmeDisplay niftyDisplay;
    private ViewPort niftyView;
    private Nifty tooltipNifty;
    private final int texSize = 128;
    private final int texW = (int) (texSize * 2.5f);
    private final int texH = texSize;
    private final FrameBuffer fb = new FrameBuffer(texW, texH, 0);
    private final Texture2D niftytex = new Texture2D(texW, texH, Format.RGB8);

    public PlayerHUD(ClientGameAppState gs) {
        this.gs = gs;
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
        gs.setNifty(nifty);
        getApplication().getGuiViewPort().addProcessor(niftyDisplay);
        ((SimpleApplication) getApplication()).getFlyByCamera().setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        // <screen>
        nifty.addScreen("Screen_ID", new ScreenBuilder("Hello Nifty Screen") {
            {
                controller(new PlayerHUDController(gs)); // Screen properties

                layer(new LayerBuilder("Chat") {
                    {
                        childLayoutHorizontal();
                        paddingTop("75%");
//                        paddingLeft("1%");

                        paddingLeft("" + ((nifty.getRenderEngine().getNativeWidth() - (0.08 * nifty.getRenderEngine().getNativeHeight() * gs.getPlayer().getHotbar().length)) / 2) + "px");

                        control(new LabelBuilder("Chat", "") {
                            {
                                alignCenter();
                                valignCenter();
                                height("85%");
//                                width("25%");
                                width(gs.getPlayer().getHotbar().length * nifty.getRenderEngine().getNativeHeight() * 0.08 + gs.getPlayer().getHotbar().length * 2 + "px");
//                                backgroundColor("#A30000");
                                visibleToMouse(false);
                                textVAlign(ElementBuilder.VAlign.Top);
                                textHAlign(ElementBuilder.Align.Left);
                                this.font("Interface/Fonts/pixelCC0_noOutline.fnt");

                                wrap(true);

//                                 onActiveEffect(new EffectBuilder("textSize") {
//                                    {
//                                        effectParameter("endSize", "0.8");
//
//                                    }
//                                });
                            }
                        });
                    }
                });


                layer(new LayerBuilder("ammo") {
                    {
                        childLayoutHorizontal();

                        panel(new PanelBuilder("ammo") {
                            {
                                childLayoutHorizontal(); // panel properties, add more...
                                marginLeft("70%"); // 3.1%
                                paddingTop("90%");

                                // GUI elements
                                control(new LabelBuilder("ammo_label", "") {
                                    {
                                        this.font("Interface/Fonts/pixel25.fnt");
                                        alignCenter();
                                        valignCenter();
                                        height("50%");
                                        width("25%");
//                                        backgroundColor("#A30000");
                                        visibleToMouse(false);
                                    }
                                });

                                //.. add more GUI elements here
                            }
                        });

                    }
                });

                layer(new LayerBuilder("mobHealthBarGray") {
                    {
                        childLayoutHorizontal();
                        paddingTop(HP_BAR_TOP_MARGIN + "%");
                        paddingLeft("" + ((100 - percentMobHealthbarLength * 100) / 2) + "%");
                        final float length = ((percentMobHealthbarLength * nifty.getRenderEngine().getNativeWidth()));

                        control(new LabelBuilder("hp_bar_target_gray", "") {
                            {
                                visible(false);
                                alignCenter();
                                valignCenter();
                                height("" + (percentMobHealthbarHeight * 100) + "%");
                                width("" + length + "px");
                                backgroundColor("#525252");
                                visibleToMouse(false);
                            }
                        });

                    }
                });

                layer(new LayerBuilder("mobHealthBarYellow") {
                    {
                        childLayoutHorizontal();

                        paddingTop(HP_BAR_TOP_MARGIN + "%");
                        paddingLeft("" + ((100 - percentMobHealthbarLength * 100) / 2) + "%");
                        final float length = ((percentMobHealthbarLength * nifty.getRenderEngine().getNativeWidth()));

                        control(new LabelBuilder("hp_bar_target_yellow", "") {
                            {
                                alignCenter();
                                valignCenter();
                                height("" + (percentMobHealthbarHeight * 100) + "%");
                                width("" + 0 + "px");
                                backgroundColor("#FFBF00");
                                visibleToMouse(false);
                            }
                        });

                    }
                });

                layer(new LayerBuilder("mobHealthBarRed") {
                    {
                        childLayoutHorizontal();
                        paddingTop(HP_BAR_TOP_MARGIN + "%");
                        paddingLeft("" + ((100 - percentMobHealthbarLength * 100) / 2) + "%");

                        control(new LabelBuilder("hp_bar_target", "") {
                            {
                                alignCenter();
                                valignCenter();
                                height("1%");
                                width("0%");
                                backgroundColor("#A30000");
                                visibleToMouse(false);
                            }
                        });

                    }
                });

                layer(new LayerBuilder("mobHealthBarLabel") {
                    {
                        childLayoutHorizontal();
                        paddingTop(HP_BAR_TOP_MARGIN - 0.2 + "%");
                        paddingLeft("" + ((100 - percentMobHealthbarLength * 100) / 2) + "%");
                        final float length = ((percentMobHealthbarLength * nifty.getRenderEngine().getNativeWidth()));

                        control(new LabelBuilder("hp_bar_target_label", "Scavenger") {
                            {
                                alignCenter();
                                valignCenter();
                                height("1%");
                                this.font("Interface/Fonts/pixelCC0_noOutline.fnt");

                                width("" + length + "px");
//                                backgroundColor("#A30000");
                                visibleToMouse(false);
                            }
                        });

                    }
                });

                // DRAW HOTBAR
                layer(new LayerBuilder("hotbar") {
                    {
                        childLayoutHorizontal();
                        paddingLeft("" + ((nifty.getRenderEngine().getNativeWidth() - (0.08 * nifty.getRenderEngine().getNativeHeight() * gs.getPlayer().getHotbar().length)) / 2) + "px");
                        paddingTop("60%");

                        for (int slotNumber = 0; slotNumber < gs.getPlayer().getHotbar().length; slotNumber++) {
                            final int i = slotNumber;
                            image(new ImageBuilder("hotbarslot" + i) {
                                {
                                    visible(true);
                                    filename("Textures/GUI/equipmentSlotEmpty.png");
//      

                                    height(nifty.getRenderEngine().getNativeHeight() * 0.08 + "px");
                                    width(nifty.getRenderEngine().getNativeHeight() * 0.08 + "px");
                                    visibleToMouse(true);
                                    // kod od ustawianai okienek
                                    marginLeft("" + (EQ_MARGIN + 2) + "px");
                                    marginTop("80%");

                                    // kod od ustawiania okienek
                                }
                            });

                        }

                    }
                });

                // DRAW EQUIPPED SLOTS
                layer(new LayerBuilder("equippedLayer") {
                    {
                        childLayoutHorizontal();
                        paddingTop("10%");
                        paddingLeft("2%");

                    }
                });

                // DRAW EQUIPMENT SLOTS
                eqSlotSizePx = nifty.getRenderEngine().getNativeHeight() * 0.08;

                for (int j = 0; j < ROWS; j++) {

                    layer(new LayerBuilder("playerEquipmentLayer") {
                        {

                            childLayoutHorizontal();
                            paddingLeft("80%");
                            paddingTop("30%");

                            cnt++;
                            final double marginTop = cnt * eqSlotSizePx;
                            for (int slotNumber = 0; slotNumber < gs.getPlayer().getEquipment().length / ROWS; slotNumber++) {
                                final int i = slotNumber;
//                                cnt = slotNumber;

                                image(new ImageBuilder("slot" + equipmentSlotAdded) {
                                    {
                                        visible(false);
                                        filename("Textures/GUI/equipmentSlotEmpty.png");
                                        height(nifty.getRenderEngine().getNativeHeight() * 0.08 + "px");
                                        width(nifty.getRenderEngine().getNativeHeight() * 0.08 + "px");
                                        visibleToMouse(true);
//                                        interactOnClick("playerEquipItem(" + equipmentSlotAdded++ + ")");
                                        interactOnRelease("playerEquipItem(" + equipmentSlotAdded++ + ")");
                                        onStartHoverEffect(new HoverEffectBuilder("border") {
                                            {
                                                effectParameter("hoverHeight", "100%");
                                                effectParameter("hoverWidth", "100%");
                                                effectParameter("color", "#000");
                                                effectParameter("border", "3px");
                                                effectParameter("onStartEffect", "getItemInfo(" + equipmentSlotAdded + ")");

                                            }
                                        });
                                        // kod od ustawianai okienek
                                        marginLeft("" + (EQ_MARGIN + 2) + "px");
                                        marginTop("" + marginTop + "px");

                                        // kod od ustawiania okienek
                                    }
                                });

                            }

                        }
                    });

                }

                // <layer>
                layer(new LayerBuilder("staticLayer") {
                    {
                        childLayoutAbsolute(); // layer properties, add more...

                        panel(new PanelBuilder("itemTooltip") {
                            {

                                childLayoutVertical(); // layer properties, add more...
                                height("8%");
                                width("12%");

                                visible(false);

                                image(new ImageBuilder("s") {
                                    {
                                        filename("Textures/GUI/tooltipFrame.png");
                                        childLayoutVertical(); // layer properties, add more...
//                   label("item");
                                    }
                                });

                            }
                        });

                    }
                });
                layer(new LayerBuilder("age") {
                    {
                        childLayoutAbsolute(); // layer properties, add more...

                        panel(new PanelBuilder("TooltipLabel") {
                            {
                                childLayoutVertical(); // layer properties, add more...
                                height("8%");
                                width("8%");

                                visible(false);

                                control(new LabelBuilder("TooltipLabelControl", "") {
                                    {
                                        
                                        wrap(true);
//                         filename("Textures/GUI/tooltipFrame.png");
                                        childLayoutVertical(); // layer properties, add more...
//                   label("item");
                                    }
                                });
                            }
                        });
                    }
                });
                // </layer>
            }
        }.build(nifty));
        // </screen>

        initializeDropTooltip();

        nifty.gotoScreen("Screen_ID"); // start the screen   
        gs.getFlyCam().setDragToRotate(false);
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        checkTargetedEntity(gs, gs.getPickableNode());

//        System.out.println(                    tooltipNifty.getCurrentScreen().findControl("hp_bar_target_label", LabelControl.class).getText() );
        if (gs.getPlayer().getCurrentTarget() != null) {
            float length = ((gs.getPlayer().getCurrentTarget().getHealth() / gs.getPlayer().getCurrentTarget().getMaxHealth()) * (percentMobHealthbarLength * nifty.getRenderEngine().getNativeWidth()));
            nifty.getCurrentScreen().findElementById("hp_bar_target").setWidth((int) (length));
//            tooltipNifty.getCurrentScreen().findControl("hp_bar_target", LabelControl.class).setText(Float.toString(gs.getPlayer().getCurrentTarget().getHealth()));
            if (nifty.getCurrentScreen().findElementById("hp_bar_target_yellow").getWidth() > nifty.getCurrentScreen().findElementById("hp_bar_target").getWidth()) {
                nifty.getCurrentScreen().findElementById("hp_bar_target_yellow").setWidth((int) (nifty.getCurrentScreen().findElementById("hp_bar_target_yellow").getWidth() - 300 * tpf));
            }
        }

        if (nifty.getCurrentScreen().findElementById("hp_bar_target_yellow").getWidth() <= 0) {
            nifty.getCurrentScreen().findElementById("hp_bar_target_gray").setVisible(false);
            nifty.getCurrentScreen().findElementById("hp_bar_target_label").setVisible(false);

        }

    }

    private void checkTargetedEntity(ClientGameAppState gs, Node nodeToCheckCollisionOn) {
        CollisionResults results = new CollisionResults();

        Ray ray = new Ray(gs.getCamera().getLocation(), gs.getCamera().getDirection());
        float distanceToFirstWall = Float.MAX_VALUE;
        if (gs.getMapNode().collideWith(ray, results) > 0) {
            distanceToFirstWall = results.getClosestCollision().getDistance();
        }
        results = new CollisionResults();
        nodeToCheckCollisionOn.collideWith(ray, results);

        if (results.size() > 0) {

            float distanceToFirstTarget = results.getClosestCollision().getDistance();

            if (distanceToFirstTarget < distanceToFirstWall) {
                CollisionResult closest = results.getClosestCollision();
                String hit = closest.getGeometry().getName();

                if (hit.equals("item tooltip")) {
                    return;
                }

                InteractiveEntity entityHit = gs.getMobs().get(Integer.valueOf(hit));
                if (entityHit instanceof Destructible enemyHit) {
                    if (enemyHit != null) {
                        boolean switched = gs.getPlayer().getCurrentTarget() != enemyHit;
                        gs.getPlayer().setCurrentTarget(enemyHit);
                        if (switched) {
                            updateTargetHealthbar();
                        }

                    }
                } else if (entityHit instanceof Item itemHit && distanceToFirstTarget <= Player.IDENTIFY_RANGE) {
                    boolean switched = gs.getPlayer().getLastTargetedItem() != itemHit;
                    gs.getPlayer().setLastTargetedItem(itemHit);
                    if (switched) {
                        tooltipNifty.resolutionChanged();
                        updateItemDropTooltipMaterial(itemHit);
                    }

                }
            }

        } else if (gs.getPlayer().getLastTargetedItem() != null) {
            gs.getPlayer().setLastTargetedItem(null);
            itemDropTooltipNode.removeFromParent();
        }
    }

    public Nifty getNifty() {
        return nifty;
    }

    public void setNifty(Nifty nifty) {
        this.nifty = nifty;
    }

    public ClientGameAppState getClient() {
        return gs;
    }

    public void setClient(ClientGameAppState gs) {
        this.gs = gs;

    }

    private void updateTargetHealthbar() {
        nifty.getCurrentScreen().findControl("hp_bar_target_label", LabelControl.class
        ).setText(gs.getPlayer().getCurrentTarget().getName());
        nifty.getCurrentScreen().findElementById("hp_bar_target_yellow").setWidth((int) ((gs.getPlayer().getCurrentTarget().getHealth() / gs.getPlayer().getCurrentTarget().getMaxHealth()) * (percentMobHealthbarLength * nifty.getRenderEngine().getNativeWidth())));
        nifty.getCurrentScreen().findElementById("hp_bar_target_gray").setVisible(true);
        nifty.getCurrentScreen().findElementById("hp_bar_target_label").setVisible(true);
    }

    private void initializeDropTooltip() {

        var cs = ClientGameAppState.getInstance();
        itemDropTooltipGeom = new Geometry("item tooltip", new Quad(2.5f, 1.f));
        itemDropTooltipNode.attachChild(itemDropTooltipGeom);
        itemDropTooltipGeom.center();
        itemDropTooltipGeom.move(0, 1f, -0.1f);
        itemDropTooltipNode.setQueueBucket(RenderQueue.Bucket.Transparent);
        BillboardControl billboard = new BillboardControl();
        billboard.setAlignment(BillboardControl.Alignment.AxialY);
        itemDropTooltipNode.addControl(billboard);

        var assetManager = cs.getAssetManager();
        var renderManager = cs.getRenderManager();
        InputManager inputManager = null;
        AudioRenderer audioRenderer = null;

        itemDropTooltipMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        itemDropTooltipMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        itemDropTooltipMaterial.getAdditionalRenderState().setDepthTest(false);

        itemDropTooltipGeom.setMaterial(itemDropTooltipMaterial);

        niftyView = renderManager.createPreView("NiftyView", new Camera(texW, texH));
        niftyView.setClearFlags(true, true, true);

        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, niftyView);

        tooltipNifty = niftyDisplay.getNifty();
        tooltipNifty.fromXml("Interface/ItemDropTooltip.xml", "tooltipScreen");
        fb.setDepthBuffer(Format.Depth);
        niftyView.setOutputFrameBuffer(fb);

    }

    private void updateItemDropTooltipMaterial(Item itemHit) {
        if(itemHit.getDroppedItemNode() == null)
            return;
        itemHit.getDroppedItemNode().attachChild(itemDropTooltipNode);

        Element textElement = tooltipNifty.getCurrentScreen().findElementByName("itemName");

        TextRenderer textRenderer = textElement.getRenderer(TextRenderer.class
        );
        textRenderer.setText(itemHit.getTemplate().getName()); // Set the text to something

        textElement = tooltipNifty.getCurrentScreen().findElementByName("itemDescription");

        textRenderer
                = textElement.getRenderer(TextRenderer.class
                );
        textRenderer.setText(itemHit.getDescription()); // Set the text to something

        niftyView.removeProcessor(niftyDisplay);
        niftyView.addProcessor(niftyDisplay);

        fb.resetObject();
        fb.setColorTexture(niftytex);

        FrameBuffer.FrameBufferTextureTarget buffTar = FrameBuffer.FrameBufferTarget.newTarget((Texture) niftytex);
        Texture texture = buffTar.getTexture();
        texture.getImage().setFormat(Format.RGBA8);
        fb.addColorTarget(buffTar);

        niftytex.setMagFilter(Texture.MagFilter.Nearest);
        itemDropTooltipMaterial.setTexture("ColorMap", niftytex);

    }

}
