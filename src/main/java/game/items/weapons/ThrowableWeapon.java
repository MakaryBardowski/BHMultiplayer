/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.scene.Node;
import game.items.ItemTemplates;

/**
 *
 * @author 48793
 */
public abstract class ThrowableWeapon extends Weapon{
    
    public ThrowableWeapon(int id,float damage, ItemTemplates.ItemTemplate template,String name,Node node) {
        super(id,damage,template,name,node);
    }

    public ThrowableWeapon(int id,float damage, ItemTemplates.ItemTemplate template,String name,Node node, boolean droppable) {
        super(id,damage,template,name,node,droppable);
    }
}
