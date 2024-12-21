/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LemurGUI;

import static LemurGUI.components.LemurUtils.getWidthRatio;
import LemurGUI.components.EquipmentButton;
import LemurGUI.components.HotbarButton;
import LemurGUI.components.TooltipMouseListener;
import LemurGUI.components.TooltipMouseListener.TooltipSource;

import LemurGUI.dragDrop.DragAndDropControl;
import client.ClientGameAppState;
import client.Main;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.IconComponent;
import game.entities.mobs.player.Player;
import game.items.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import messages.items.MobItemInteractionMessage;

/**
 *
 * @author 48793
 */
public class LemurPlayerInventoryGui {

    @Getter
    private final Player player;
    private Container tooltipContainer;
    private Container dragItemIconContainer;
    private Label dragItemIconLabel;

    private final List<Spatial> guiNodeChildren = new ArrayList<>();
    private final Map<Integer, EquipmentButton> equipmentButtonsByIndex = new HashMap<>();
    private final Map<Integer, HotbarButton> hotbarButtonsByIndex = new HashMap<>();
    @Getter
    private float equipmentTileSizePx;
    private boolean enabled;

    public LemurPlayerInventoryGui(Player player) {
        this.player = player;
        initialize();
    }

    public final void initialize() {
        var main = Main.getInstance();
        var settings = main.getAppSettings();
        var guiNode = main.getGuiNode();
        tooltipContainer = new Container();  // spring grid layout by default depending on styling
        tooltipContainer.setLayout(new BoxLayout());
        guiNodeChildren.add(tooltipContainer);

        dragItemIconContainer = new Container();
        tooltipContainer.setLayout(new BoxLayout());
        guiNodeChildren.add(dragItemIconContainer);

        equipmentTileSizePx = settings.getWidth() * 0.04f;
        makeEquipmentTab(equipmentTileSizePx, guiNode, settings);
    }

    public void makeEquipmentTab(float sizePx, Node guiNode, AppSettings settings) {
        var rows = 5;
        var cols = 4;

        Container equipmentColumn1 = new Container();
        equipmentColumn1.setLocalTranslation(settings.getWidth() * 0.8f, settings.getHeight() * 0.1f + rows * sizePx + 4 * sizePx, 0);

        Container characterPreviewContainer = new Container();
        characterPreviewContainer.setLocalTranslation(settings.getWidth() * 0.8f + 1f * getWidthRatio() + sizePx, settings.getHeight() * 0.1f + rows * sizePx + 4 * sizePx, 0);

        Container equipmentColumn2 = new Container();
        equipmentColumn2.setLocalTranslation(settings.getWidth() * 0.8f + 4f * getWidthRatio() + 3 * sizePx, settings.getHeight() * 0.1f + rows * sizePx + 4 * sizePx, 0);

        var emptySlotPath = "Textures/GUI/EquipmentIcons/unknown.png";

        equipmentColumn1.addChild(createEquipmentLabel(sizePx, "Textures/GUI/EquipmentIcons/equippedIconHelmet.png"), 0, 0);
        equipmentColumn1.addChild(createEquipmentLabel(sizePx, "Textures/GUI/EquipmentIcons/equippedIconVest.png"), 1, 0);
        equipmentColumn1.addChild(createEquipmentLabel(sizePx, "Textures/GUI/EquipmentIcons/equippedIconGloves.png"), 2, 0);
        equipmentColumn1.addChild(createEquipmentLabel(sizePx, "Textures/GUI/EquipmentIcons/equippedIconBoots.png"), 3, 0);

        characterPreviewContainer.addChild(createCharacterPreviewLabel(sizePx), 0);

        equipmentColumn2.addChild(createEquipmentLabel(sizePx, emptySlotPath), 0, 0);
        equipmentColumn2.addChild(createEquipmentLabel(sizePx, emptySlotPath), 1, 0);
        equipmentColumn2.addChild(createEquipmentLabel(sizePx, emptySlotPath), 2, 0);
        equipmentColumn2.addChild(createEquipmentLabel(sizePx, emptySlotPath), 3, 0);

        Container c = new Container();  // spring grid layout by default depending on styling
        c.setLocalTranslation(settings.getWidth() * 0.8f, settings.getHeight() * 0.1f + rows * sizePx, 0);

        var cnt = 0;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                var equipmentButton = c.addChild(createEquipmentTile(cnt, sizePx), row - 1, col - 1);
                equipmentButtonsByIndex.put(cnt, equipmentButton);
                cnt++;
            }
        }

        var hotbarButtonsCount = 10;
        var hotbarButtonSize = sizePx;
        Container hotbarContainer = new Container();

        hotbarContainer.setLocalTranslation(settings.getWidth() - (hotbarButtonsCount * hotbarButtonSize) / 2 - settings.getWidth() * 0.5f, settings.getHeight() * 0.01f + hotbarButtonSize, 0);
        for (int i = 0; i < hotbarButtonsCount; i++) {
            var hotbarButtonIndex = (i+1) % 10;
            var hotbarButton = hotbarContainer.addChild(createHotbarButton(hotbarButtonIndex, hotbarButtonSize), 0, i);
            hotbarButtonsByIndex.put(hotbarButtonIndex,hotbarButton);
        }

        guiNodeChildren.add(c);
        guiNodeChildren.add(equipmentColumn1);
        guiNodeChildren.add(characterPreviewContainer);
        guiNodeChildren.add(equipmentColumn2);

        guiNode.attachChild(hotbarContainer);
    }

    public EquipmentButton createEquipmentTile(int buttonIndex, float sizePx) {
        EquipmentButton clickMe = new EquipmentButton("", this,new DragAndDropControl(this), new TooltipMouseListener(this,TooltipSource.EQUIPMENT));
        clickMe.setName("" + buttonIndex);
        setEquipmentButtonIcon(buttonIndex, clickMe, sizePx);
        clickMe.addClickCommands((Button source) -> {
            var equipmentButtonSource = (EquipmentButton) source;
            if (equipmentButtonSource.isClickDisabled()) {
                return;
            }
            var item = player.getEquipment().getItemAt(buttonIndex);
//            if (item instanceof Armor) {
//                return;
//            }
            playerEquipItem(item, player);
        });

        

        return clickMe;
    }

    public Label createEquipmentLabel(float sizePx, String path) {
        Label clickMe = new Label("");
        clickMe.setName("4");

        var ic = new IconComponent(path);
        ic.setIconSize(new Vector2f(sizePx, sizePx));
        clickMe.setIcon(ic);

        clickMe.addMouseListener(new TooltipMouseListener(this,TooltipSource.EQUIPMENT));
        clickMe.addControl(new DragAndDropControl(this));

        return clickMe;
    }

    public Label createCharacterPreviewLabel(float sizePx) {
        Label clickMe = new Label("");
        clickMe.setPreferredSize(new Vector3f(sizePx * 2, sizePx * 4, 2));
//        clickMe.setName("4");
//        var path = Equipment.getItemByIndex(4).getIconPath();
//        var ic = new IconComponent(path);
//        ic.setIconSize(new Vector2f(sizePx * 2, sizePx * 4));
//        clickMe.setIcon(ic);
//        clickMe.addMouseListener(new TooltipMouseListener());
        return clickMe;
    }

    public HotbarButton createHotbarButton(int hotbarButtonIndex, float sizePx) {
        HotbarButton clickMe = new HotbarButton("",this,new DragAndDropControl(this), new TooltipMouseListener(this,TooltipSource.HOTBAR));
        clickMe.setName("" + hotbarButtonIndex);

        var path = "Textures/GUI/equipmentSlotEmpty.png";

        var c = new IconComponent(path);
        c.setIconSize(new Vector2f(sizePx, sizePx));
        clickMe.setIcon(c);
        clickMe.addClickCommands((Button source) -> {
            System.out.println("Hotbar . " + hotbarButtonIndex);
        });
        return clickMe;
    }

    public Container getTooltipContainer() {
        return tooltipContainer;
    }

    public Container getDragItemIconContainer() {
        return dragItemIconContainer;
    }

    public Label getDragIcon() {
        if (dragItemIconLabel == null) {
            dragItemIconLabel = new Label("");
        }
        return dragItemIconLabel;
    }

    public void toggle() {
        if (enabled) {
            for (var child : guiNodeChildren) {
                child.removeFromParent();
            }
            if (DragAndDropControl.isDragging()) {
                DragAndDropControl.setDragging(false);
            }
            getDragIcon().removeFromParent();
            TooltipMouseListener.getTooltipInnerContainer().removeFromParent();
        } else {
            for (var buttonIndex : equipmentButtonsByIndex.keySet()) {
                var button = equipmentButtonsByIndex.get(buttonIndex);
                updateEquipmentButton(buttonIndex);
                button.setClickDisabled(false);
                button.getDragAndDropControl().enableDrag();
            }

            for (var child : guiNodeChildren) {
                Main.getInstance().getGuiNode().attachChild(child);
            }
        }
        enabled = !enabled;
        player.setViewingEquipment(enabled);
        player.setHoldsTrigger(false);
        GuiGlobals.getInstance().setCursorEventsEnabled(enabled);
    }

    public void updateEquipmentButton(int index){
        var button = equipmentButtonsByIndex.get(index);
        synchronized (button) {
            setEquipmentButtonIcon(index,button , equipmentTileSizePx);
        }
    }

    public void updateHotbarButton(int index){
        var button = hotbarButtonsByIndex.get(index);
        synchronized (button) {
            setHotbarButtonIcon(index,button , equipmentTileSizePx);
        }
    }

    public synchronized void updateEquipmentGui(){
        for (var buttonIndex : equipmentButtonsByIndex.keySet()) {
            var button = equipmentButtonsByIndex.get(buttonIndex);
            setEquipmentButtonIcon(buttonIndex, button, equipmentTileSizePx);
        }
    }

    public void setEquipmentButtonIcon(int buttonIndex, Button button, float sizePx) {
        var itemInSlot = player.getEquipment().getItemAt(buttonIndex);
        var path = "Textures/GUI/equipmentSlotEmpty.png";
        if (itemInSlot != null) {
            path = itemInSlot.getTemplate().getIconPath();
        }
        var c = new IconComponent(path);
        c.setIconSize(new Vector2f(sizePx, sizePx));
        button.setIcon(c);
    }

    public void setHotbarButtonIcon(int buttonIndex, Button button, float sizePx) {
        var itemInSlot = player.getHotbar().getItemAt(buttonIndex);
        var path = "Textures/GUI/equipmentSlotEmpty.png";
        if (itemInSlot != null) {
            path = itemInSlot.getTemplate().getIconPath();
        }
        var c = new IconComponent(path);
        c.setIconSize(new Vector2f(sizePx, sizePx));
        button.setIcon(c);
    }

    public EquipmentButton getEquipmentButtonByIndex(int index){
      return  equipmentButtonsByIndex.get(index);
    }

    public HotbarButton getHotbarButtonByIndex(int index){
        return  hotbarButtonsByIndex.get(index);
    }


    public static void playerEquipItem(Item item, Player player) {
        if (item != null) {
            player.equip(item);
            sendEquipMessageToServer(item, player);
        }
    }

    public static void sendEquipMessageToServer(Item item, Player player) {
        var gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(item, player, MobItemInteractionMessage.ItemInteractionType.EQUIP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }
}
