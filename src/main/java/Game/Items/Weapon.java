/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Mobs.Mob;

/**
 * @author tomasz potoczko
 */
public abstract class Weapon extends Item{
    float damage;
    float range;
    DamageType damageType;
    float attackSpeed;

    
    public void dealDamage(Mob m){
        //m.setHealth(m.getHealth() - this.getDamage())
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public float getDamage(){
        return this.damage;
    }

}
