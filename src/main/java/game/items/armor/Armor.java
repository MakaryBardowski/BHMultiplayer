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
import game.items.ItemTemplates.ItemTemplate;

/**
 *
 * @author 48793
 */
public abstract class Armor extends Item implements Equippable {

    public Armor(int id, ItemTemplate template,String name,Node node) {
        super(id,template,name,node);
    }

    public Armor(int id, ItemTemplate template,String name,Node node, boolean droppable) {
        super(id,template,name,node,droppable);
    }



}
