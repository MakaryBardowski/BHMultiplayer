/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LemurGUI.components;

import LemurGUI.LemurPlayerEquipment;
import LemurGUI.dragDrop.DragAndDropControl;
import com.jme3.collision.CollisionResult;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorMotionEvent;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class HotbarButton extends DraggableButton {

    private final LemurPlayerEquipment lemurPlayerEquipment;

    @Getter
    private final TooltipMouseListener tooltipListener;

    public HotbarButton(String s, LemurPlayerEquipment lemurPlayerEquipment, DragAndDropControl dragAndDropControl, TooltipMouseListener tooltipListener) {
        super(s, dragAndDropControl, tooltipListener);
        this.lemurPlayerEquipment = lemurPlayerEquipment;
        this.tooltipListener = tooltipListener;
    }

    @Override
    public void onDragStart(CursorButtonEvent event, CollisionResult collision, Spatial capture) {
        clickDisabled = true;
        var item = lemurPlayerEquipment.getPlayer().getHotbar()[Integer.parseInt(capture.getName())];
        if (item == null) {
            return;
        }

        var dragIcon = lemurPlayerEquipment.getDragIcon();

        var icon = new IconComponent(item.getTemplate().getIconPath());

        if (dragIcon.getIcon() != null) {
            icon.setIconSize(((IconComponent) dragIcon.getIcon()).getIconSize());
        } else {
            icon.setIconSize(
                    new Vector2f(
                            lemurPlayerEquipment.getEquipmentTileSizePx(),
                            lemurPlayerEquipment.getEquipmentTileSizePx()
                    )
            );
        }
        dragIcon.setIcon(icon);
        lemurPlayerEquipment.getDragItemIconContainer().addChild(dragIcon);
        lemurPlayerEquipment.getDragItemIconContainer().setLocalTranslation(capture.getWorldTranslation());

    }

    @Override
    public void drag() {
    }

    @Override
    public void onDragStop(CursorButtonEvent event, CursorMotionEvent lastMotion, Spatial dragTarget, Spatial draggedSpatial) {
        clickDisabled = false;
        if (draggedSpatial == null) {
            return;
        }
        if (dragTarget == null) {
            lemurPlayerEquipment.getPlayer().getHotbar()[Integer.parseInt(draggedSpatial.getName())] = null;
            var currentIconSize = ((IconComponent) this.getIcon()).getIconSize();
            var emptyIcon = new IconComponent("Textures/GUI/equipmentSlotEmpty.png");
            emptyIcon.setIconSize(currentIconSize);
            this.setIcon(emptyIcon);
            return;
        }

        var draggedItemIndexInHotbar = Integer.parseInt(this.getName());
        if (lemurPlayerEquipment.getPlayer().getHotbar()[draggedItemIndexInHotbar] == null) {
            return;
        }

        if (dragTarget instanceof HotbarButton dragTargetHotbarButton) {
            var targetIcon = (IconComponent) dragTargetHotbarButton.getIcon();
            var targetIconSize = targetIcon.getIconSize();

            var captureIcon = (IconComponent) this.getIcon();
            var captureIconSize = captureIcon.getIconSize();

            captureIcon.setIconSize(targetIconSize);
            targetIcon.setIconSize(captureIconSize);

            dragTargetHotbarButton.setIcon(captureIcon);
            this.setIcon(targetIcon);

            var hotbarIndex = Integer.parseInt(dragTarget.getName());
            var itemInTargetSlot = lemurPlayerEquipment.getPlayer().getHotbar()[hotbarIndex];
            lemurPlayerEquipment.getPlayer().getHotbar()[hotbarIndex] = lemurPlayerEquipment.getPlayer().getHotbar()[draggedItemIndexInHotbar];
            lemurPlayerEquipment.getPlayer().getHotbar()[draggedItemIndexInHotbar] = itemInTargetSlot;
            tooltipListener.mouseEntered(new MouseMotionEvent((int) event.getX(), (int) event.getY(), 0, 0, 0, 0), dragTarget, dragTarget);

        }

    }

}
