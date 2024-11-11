/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LemurGUI.components;

import LemurGUI.dragDrop.DragAndDropControl;
import com.simsilica.lemur.Button;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
public abstract class DraggableButton extends Button implements Draggable {

    @Getter
    @Setter
    protected boolean clickDisabled; // disables the button temporarily

    protected TooltipMouseListener tooltipListener;
    
    @Getter
    protected DragAndDropControl dragAndDropControl;
    public DraggableButton(String s,DragAndDropControl dragAndDropControl, TooltipMouseListener tooltipListener) {
        super(s);
        this.addMouseListener(tooltipListener);
        this.addControl(dragAndDropControl);
        this.tooltipListener = tooltipListener;
        this.dragAndDropControl = dragAndDropControl;
    }
}
