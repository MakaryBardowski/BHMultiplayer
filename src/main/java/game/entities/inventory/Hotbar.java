package game.entities.inventory;

import game.items.Item;

public class Hotbar {
    private final Item[] items;

    public Hotbar(Item[] items){
        this.items = items;
    }

    public synchronized Item getItemAt(int index){
        return items[index];
    }

    public synchronized void swapItems(int index, int otherIndex){
        var temp = items[index];
        items[index] = items[otherIndex];
        items[otherIndex] = temp;
    }

    /***
     * fails to put an item if the slot is not empty
     * @param item to put in
     * @param index in the hotbar to put the item in
     */
    public synchronized void addItemAt(Item item, int index){
        if(items[index] != null){
            return;
        }
        items[index] = item;
    }

    public synchronized void removeItemAt(int index){
        items[index] = null;
    }

    public synchronized boolean isEmptySlot(int index){
        return items[index] == null;
    }
}
