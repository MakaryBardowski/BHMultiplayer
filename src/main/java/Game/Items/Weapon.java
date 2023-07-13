/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Items.ItemTemplates.ItemTemplate;
import Game.Mobs.Mob;
import Game.Mobs.Player;

/**
 * @author tomasz potoczko
 */
public abstract class Weapon extends Item implements Attacks,Holdable,Equippable{
    private static final DamageType DEFAULT_DAMAGE_TYPE = DamageType.PHYSICAL;
    protected float damage;
    protected float range;
    protected DamageType damageType;
    protected float attackSpeed;
    
    protected Weapon(float damage,ItemTemplate template){
    super(template);
    this.damage = damage;
    damageType = DEFAULT_DAMAGE_TYPE;
    }
    
//
    
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
