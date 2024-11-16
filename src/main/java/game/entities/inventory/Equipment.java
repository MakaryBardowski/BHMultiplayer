package game.entities.inventory;

import game.items.Item;

import java.util.ArrayList;
import java.util.List;

public class Equipment {
    private final Item[] items;

    public Equipment(Item[] items){
        this.items = items;
    }

    public synchronized void swapItems(int index, int otherIndex){
        var temp = items[index];
        items[index] = items[otherIndex];
        items[otherIndex] = temp;
    }

    public synchronized Item getItemAt(int index){
        return items[index];
    }

    /***
     *
     * @param index of item in equipment to be removed
     * @return item at index (can be null)
     */
    public synchronized Item removeItem(int index){
        var item = items[index];
        items[index] = null;
        return null;
    }

    /***
     *
     * @param item to be removed from equipment if present
     * @return null if item is not present, else return item
     */
    public synchronized Item removeItem(Item item){
        if(item == null){
            return null;
        }
        for(int i = 0; i < items.length; i++){
            if(items[i] != null && items[i].getId() == item.getId()){
                items[i] = null;
                return item;
            }
        }
        return null;
    }

    /***
     * removes all items from equipment
     * @return list of all non-null items from equipment
     */
    public synchronized List<Item> removeAllItems(){
        var itemsRemoved = new ArrayList<Item>(items.length);
        for(int i = 0;i < items.length; i++){
            if(items[i] != null){
                var removedItem = removeItem(i);
                if(removedItem != null) {
                    itemsRemoved.add(removedItem);
                }
            }
        }
        return itemsRemoved;
    }

    /***
     * gets all items from equipment
     * @return list of all non-null items from equipment
     */
    public synchronized List<Item> getAllItems(){
        var allItems = new ArrayList<Item>(items.length);
        for(int i = 0;i < items.length; i++){
            if(items[i] != null){
                allItems.add(items[i]);
            }
        }
        return allItems;
    }

    /***
     * adds item to the first empty spot in equipment. Does not add the item if the item is already in equipment.
     * @param item to be added
     * @return true if item was successfully added, false if not
     */
    public synchronized boolean addItem(Item item){
        int firstFreeIndex = -1;
        for(int i = 0; i < items.length;i++ ){
            var itemChecked = items[i];

            if(itemChecked == null){
                // if found a free equipment slot, remember it
                if(firstFreeIndex == -1) {
                    firstFreeIndex = i;
                }
                // keep checking further items, to be sure that item isnt in equipment already
                continue;
            }

            // if item is present in equipment already return false, and dont add it
            if(itemChecked.getId() == item.getId()){
                return false;
            }

        }

        // if there was a free slot, fill it
        if(firstFreeIndex != -1) {
            items[firstFreeIndex] = item;
            return true;
        }

        // fail to add item, because all spots were populated
        return false;
    }

}