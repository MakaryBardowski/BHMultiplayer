/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.scene.Node;
import game.items.ItemTemplates.ItemTemplate;
import game.entities.mobs.Mob;
import game.items.ItemTemplates;

/**
 *
 * @author tomasz potoczko
 */
public abstract class RangedWeapon extends Weapon {
    public RangedWeapon(int id,float damage, ItemTemplate template,String name,Node node) {
        super(id,damage,template,name,node);
    }

    public RangedWeapon(int id,float damage, ItemTemplate template,String name,Node node, boolean droppable) {
        super(id,damage,template,name,node,droppable);
    }
}
