/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import game.items.Equippable;
import game.items.Item;
import game.items.ItemTemplates;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author 48793
 */
public abstract class Armor extends Item implements Equippable {

    public Armor(ItemTemplates.ItemTemplate template) {
        super(template);
    }

    public Armor(ItemTemplates.ItemTemplate template, boolean droppable) {
        super(template, droppable);
    }



}
