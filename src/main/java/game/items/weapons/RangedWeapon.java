/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import game.items.ItemTemplates.ItemTemplate;
import game.mobs.Mob;

/**
 *
 * @author tomasz potoczko
 */
public abstract class RangedWeapon extends Weapon {

    protected RangedWeapon(float damage, ItemTemplate template) {
        super(damage, template);
    }

    protected RangedWeapon(float damage, ItemTemplate template, boolean droppable) {
        super(damage, template, droppable);
    }
}
