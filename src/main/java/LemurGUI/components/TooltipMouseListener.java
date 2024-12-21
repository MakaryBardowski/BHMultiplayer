/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LemurGUI.components;

import LemurGUI.LemurPlayerInventoryGui;
import static LemurGUI.components.LemurUtils.getHeightRatio;
import static LemurGUI.components.LemurUtils.getWidthRatio;
import LemurGUI.dragDrop.DragAndDropControl;
import client.Main;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.MouseListener;

/**
 *
 * @author 48793
 */
public class TooltipMouseListener implements MouseListener {

    private static Label tooltipLabel;
    private static Label tooltipDescription;
    private static Container tooltipInnerContainer;
    private static final int HORIZONTAL_OFFSET_PX = 10;
    private static final int VERTICAL_OFFSET_PX = -30;
    private final LemurPlayerInventoryGui lemurPlayerEquipmentGui;

    private final TooltipSource tooltipSource;

    public TooltipMouseListener(LemurPlayerInventoryGui lemurPlayerEquipmentGui, TooltipSource tooltipSource) {
        this.lemurPlayerEquipmentGui = lemurPlayerEquipmentGui;
        this.tooltipSource = tooltipSource;
    }

    public static Container getTooltipInnerContainer() {
        if (tooltipInnerContainer == null) {
            tooltipInnerContainer = new Container();
            tooltipInnerContainer.setLayout(new BoxLayout());
            var texture = Main.getInstance().getAssetManager().loadTexture("Textures/GUI/itemTooltip.png");

            var background = new QuadBackgroundComponent(texture);
            tooltipInnerContainer.setBackground(background);
            tooltipInnerContainer.setPreferredSize(
                    new Vector3f(
                            320 * getWidthRatio(),
                            128 * getHeightRatio(),
                            1
                    )
            );

            tooltipInnerContainer.addChild(getTooltipLabel());
            tooltipInnerContainer.addChild(getTooltipDescription());

        }
        return tooltipInnerContainer;
    }

    private static Label getTooltipLabel() {
        if (tooltipLabel == null) {
            tooltipLabel = new Label("");
            var font = Main.getInstance().getAssetManager().loadFont("Interface/Fonts/pixelFlat11.fnt");
            tooltipLabel.setFont(font);
            tooltipLabel.setFontSize(14 * getWidthRatio());

            tooltipLabel.setText("\\#ffA500# name ");
            tooltipLabel.setTextHAlignment(HAlignment.Center);
            tooltipLabel.setTextVAlignment(VAlignment.Center);
            tooltipLabel.setPreferredSize(
                    new Vector3f(
                            320 * getWidthRatio(),
                            20 * getHeightRatio(),
                            1
                    )
            );

        }
        return tooltipLabel;
    }

    private static Label getTooltipDescription() {
        if (tooltipDescription == null) {
            tooltipDescription = new Label("Damage: 7\nArmor: 3");
            var font = Main.getInstance().getAssetManager().loadFont("Interface/Fonts/pixelFlat11.fnt");
            var insets = new Insets3f(10 * getHeightRatio(), 10 * getWidthRatio(), 0, 0);
            tooltipDescription.setFont(font);
            tooltipDescription.setFontSize(12 * getWidthRatio());

            tooltipDescription.setInsets(insets);
            tooltipDescription.setPreferredSize(
                    new Vector3f(
                            320 * getWidthRatio(),
                            108 * getHeightRatio(),
                            1
                    )
            );
        }
        return tooltipDescription;
    }

    @Override
    public void mouseButtonEvent(MouseButtonEvent mbe, Spatial sptl, Spatial sptl1) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseEntered(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
        if (DragAndDropControl.isDragging()) {
            return;
        }

        var equipmentItemSource = lemurPlayerEquipmentGui.getPlayer().getEquipment();
        var hotbarItemSource = lemurPlayerEquipmentGui.getPlayer().getHotbar();
        if (tooltipSource == TooltipSource.EQUIPMENT) {
            var item =  equipmentItemSource.getItemAt(Integer.parseInt(sptl.getName()));
            if (item == null) {
                return;
            }
            String description = item.getDescription();
            String label = item.getName();
            getTooltipDescription().setText(description);
            getTooltipLabel().setText(label);

            if (lemurPlayerEquipmentGui.getTooltipContainer().getChildren().isEmpty()) {
                lemurPlayerEquipmentGui.getTooltipContainer().addChild(getTooltipInnerContainer());
            }
            var tooltipPos = getTooltipPosition(mme.getX(), mme.getY());

            lemurPlayerEquipmentGui.getTooltipContainer().setLocalTranslation(tooltipPos);
            return;
        }

        var item = hotbarItemSource.getItemAt(Integer.parseInt(sptl.getName()));
        if (item == null) {
            return;
        }
        String description = item.getDescription();
        String label = item.getName();
        getTooltipDescription().setText(description);
        getTooltipLabel().setText(label);

        if (lemurPlayerEquipmentGui.getTooltipContainer().getChildren().isEmpty()) {
            lemurPlayerEquipmentGui.getTooltipContainer().addChild(getTooltipInnerContainer());
        }
        var tooltipPos = getTooltipPosition(mme.getX(), mme.getY());

        lemurPlayerEquipmentGui.getTooltipContainer().setLocalTranslation(tooltipPos);
    }

    @Override
    public void mouseExited(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
        if (DragAndDropControl.isDragging()) {
            return;
        }
        lemurPlayerEquipmentGui.getTooltipContainer().removeChild(getTooltipInnerContainer());
    }

    @Override
    public void mouseMoved(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
        if (DragAndDropControl.isDragging()) {
            if (getTooltipInnerContainer().getParent() != null) {
                lemurPlayerEquipmentGui.getTooltipContainer().removeChild(getTooltipInnerContainer());
            }
            var dragItemContainer = lemurPlayerEquipmentGui.getDragItemIconContainer();
            var dragIcon = lemurPlayerEquipmentGui.getDragIcon();
            dragItemContainer.setLocalTranslation(new Vector3f(mme.getX() - dragIcon.getPreferredSize().x / 2, mme.getY() + dragIcon.getPreferredSize().y / 2, 2));

            return;
        }

        var tooltipPos = getTooltipPosition(mme.getX(), mme.getY());
        lemurPlayerEquipmentGui.getTooltipContainer().setLocalTranslation(tooltipPos);
    }

    public static Vector3f getTooltipPosition(float mouseX, float mouseY) {
        var settings = Main.getInstance().getAppSettings();
        var widthRatio = getWidthRatio();
        var heightRatio = getHeightRatio();
        var screenWidth = settings.getWidth();
//        var screenHeight = settings.getHeight();

        // detect collision only with bottom or right edge of screen
        var preferredSize = getTooltipInnerContainer().getPreferredSize();
        var v = new Vector3f(mouseX + HORIZONTAL_OFFSET_PX * widthRatio, mouseY + VERTICAL_OFFSET_PX * widthRatio, 2);

        if (mouseX + preferredSize.x + HORIZONTAL_OFFSET_PX * widthRatio > screenWidth - HORIZONTAL_OFFSET_PX * widthRatio) {
            v.setX(screenWidth - preferredSize.x - HORIZONTAL_OFFSET_PX * widthRatio);
        }
        if (mouseY - preferredSize.y + VERTICAL_OFFSET_PX * heightRatio < 0 + 16 * heightRatio) {
            v.setY(mouseY + preferredSize.y - VERTICAL_OFFSET_PX * heightRatio);

        }
        return v;
    }

    public static enum TooltipSource {
        HOTBAR, EQUIPMENT
    }
}
