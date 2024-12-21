package game.entities.inventory;

import LemurGUI.LemurPlayerInventoryGui;
import com.simsilica.lemur.component.IconComponent;
import game.items.Item;
import lombok.Getter;
import lombok.Setter;

public class Hotbar {
    private final Item[] items;

    @Setter
    @Getter
    private LemurPlayerInventoryGui inventoryGui;

    public Hotbar(Item[] items){
        this.items = items;
    }

    public synchronized Item getItemAt(int index){
        return items[index];
    }

    public synchronized void swapItems(int index, int otherIndex){
        if(inventoryGui != null){
            var hotbarButton = inventoryGui.getHotbarButtonByIndex(index);
            var otherHotbarButton = inventoryGui.getHotbarButtonByIndex(otherIndex);

            var targetIcon = (IconComponent) otherHotbarButton.getIcon();
            var targetIconSize = targetIcon.getIconSize();

            var captureIcon = (IconComponent) hotbarButton.getIcon();
            var captureIconSize = captureIcon.getIconSize();

            captureIcon.setIconSize(targetIconSize);
            targetIcon.setIconSize(captureIconSize);

            otherHotbarButton.setIcon(captureIcon);
            hotbarButton.setIcon(targetIcon);
        }

        var temp = items[index];
        items[index] = items[otherIndex];
        items[otherIndex] = temp;
    }

    /***
     * fails to put an item if the slot is not empty
     * @param item to put in
     * @param index in the hotbar to put the item in
     */
    public synchronized void setItemAt(Item item, int index){
        items[index] = item;
        if(inventoryGui != null){
            inventoryGui.updateHotbarButton(index);
        }
    }

    public synchronized void removeItemAt(int index){
        items[index] = null;
        if(inventoryGui != null){
            inventoryGui.updateHotbarButton(index);
        }
    }

    public synchronized boolean isEmptySlot(int index){
        return items[index] == null;
    }
}
