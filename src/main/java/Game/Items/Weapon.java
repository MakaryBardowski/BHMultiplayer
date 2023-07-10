/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Mobs.Mob;

/**
 * @author tomasz potoczko
 */
public abstract class Weapon extends Item implements Attacks,Holdable,Equippable{
    protected float damage;
    protected float range;
    protected DamageType damageType;
    protected float attackSpeed;
    
    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public float getRange() {
        return range;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }
    
    public float getDamage(){
        return damage;
    }
    
    
}
