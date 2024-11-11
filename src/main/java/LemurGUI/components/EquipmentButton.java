/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LemurGUI.components;

import LemurGUI.LemurPlayerEquipment;
import LemurGUI.dragDrop.DragAndDropControl;
import client.ClientGameAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorMotionEvent;
import java.util.Arrays;
import messages.items.MobItemInteractionMessage;

/**
 *
 * @author 48793
 */
public class EquipmentButton extends DraggableButton {

    private final LemurPlayerEquipment lemurPlayerEquipment;

    public EquipmentButton(String s, LemurPlayerEquipment lemurPlayerEquipment, DragAndDropControl dragAndDropControl, TooltipMouseListener tooltipListener) {
        super(s, dragAndDropControl, tooltipListener);
        this.lemurPlayerEquipment = lemurPlayerEquipment;
    }

    @Override
    public void onDragStart(CursorButtonEvent event, CollisionResult collision, Spatial capture) {
        clickDisabled = true;
        var item = lemurPlayerEquipment.getPlayer().getEquipment()[Integer.parseInt(capture.getName())];
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
        var draggedItemIndexInEquipment = Integer.parseInt(this.getName());
        var item = lemurPlayerEquipment.getPlayer().getEquipment()[draggedItemIndexInEquipment];
        if (item == null) {
            return;
        }

        if (dragTarget == null) {
            var currentIconSize = ((IconComponent) this.getIcon()).getIconSize();
            var emptyIcon = new IconComponent("Textures/GUI/equipmentSlotEmpty.png");
            emptyIcon.setIconSize(currentIconSize);
            this.setIcon(emptyIcon);

            var miim = new MobItemInteractionMessage(item.getId(), lemurPlayerEquipment.getPlayer().getId(), MobItemInteractionMessage.ItemInteractionType.DROP);
            ClientGameAppState.getInstance().getClient().send(miim);
            lemurPlayerEquipment.getPlayer().getHotbar()[Integer.parseInt(draggedSpatial.getName())] = null;

            return;
        }

        if (dragTarget instanceof EquipmentButton dragTargetEquipmentButton) {

            var targetIcon = (IconComponent) dragTargetEquipmentButton.getIcon();
            var targetIconSize = targetIcon.getIconSize();

            var captureIcon = (IconComponent) this.getIcon();
            var captureIconSize = captureIcon.getIconSize();

            captureIcon.setIconSize(targetIconSize);
            targetIcon.setIconSize(captureIconSize);

            dragTargetEquipmentButton.setIcon(captureIcon);
            this.setIcon(targetIcon);

            lemurPlayerEquipment.getPlayer().equipmentSwapItems(Integer.parseInt(dragTargetEquipmentButton.getName()), draggedItemIndexInEquipment);
            System.out.println("event "+event.getX() + " "+event.getY());
            tooltipListener.mouseEntered(new MouseMotionEvent((int) event.getX(), (int) event.getY(), 0, 0, 0, 0), dragTarget, dragTarget);
        }

        if (dragTarget instanceof HotbarButton dragTargetHotbarButton) {
            var captureIcon = (IconComponent) this.getIcon().clone();
            dragTargetHotbarButton.setIcon(captureIcon);

            var hotbarIndex = Integer.parseInt(dragTarget.getName());
            lemurPlayerEquipment.getPlayer().getHotbar()[hotbarIndex] = lemurPlayerEquipment.getPlayer().getEquipment()[draggedItemIndexInEquipment];
            dragTargetHotbarButton.getTooltipListener().mouseEntered(new MouseMotionEvent((int) event.getX(), (int) event.getY(), 0, 0, 0, 0), dragTarget, dragTarget);

        }

    }

}
