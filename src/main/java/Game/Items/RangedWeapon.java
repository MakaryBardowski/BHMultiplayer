/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Items.ItemTemplates.ItemTemplate;
import Game.Mobs.Mob;

/**
 *
 * @author tomasz potoczko
 */
public abstract class RangedWeapon extends Weapon {

    
    protected RangedWeapon(float damage,ItemTemplate template) {
        super(damage,template);
    }
}
