/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Items.ItemTemplates.ItemTemplate;

/**
 *
 * @author 48793
 */
public abstract class Item{
    protected String name;
    protected String description;

    // odkomentuj
    protected ItemTemplate template;
    
    protected Item (ItemTemplate template){
    this.template = template;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemTemplate getTemplate() {
        return template;
    }
    
    
    
}
