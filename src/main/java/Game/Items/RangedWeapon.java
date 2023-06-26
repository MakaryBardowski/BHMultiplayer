/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Mobs.Mob;

/**
 *
 * @author tomasz potoczko
 */
public abstract class RangedWeapon extends Weapon{
    
    @Override
    public float getDamage(){
        //todo: math function for damage drop-off
        return this.damage;
    }
    
}
