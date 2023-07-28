/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.mobs.Mob;
import game.mobs.Player;

/**
 *
 * @author tomasz potoczko
 */
public class MeleeWeapon extends Weapon{

    protected MeleeWeapon(float damage,ItemTemplate template){
    super(damage,template);
    }
    
    @Override
    public void playerEquip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void attack(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerHoldRight(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    @Override
    public void playerUseRight(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerAttack(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    
    public static class Builder{
        private MeleeWeapon weapon;
        private final ItemTemplate DEFAULT_TEMPLATE = ItemTemplates.RIFLE_MANNLICHER_95;
        
        public Builder(){
            this.reset();
        }

        public void reset(){
            this.weapon = new MeleeWeapon(0,DEFAULT_TEMPLATE);
        }
        
        

        public void setName(String name){
            weapon.name = name;
        }
        
        public void setDescription(String desc){
            weapon.description = desc;
        }

        public void setSpeed(float attackSpeed) {
            weapon.attackSpeed = attackSpeed;
        }

        public void setDamage(float damage) {
            weapon.damage = damage;
        }

        public void setDamageType(DamageType damageType) {
            weapon.damageType = damageType;
        }
        public void setRange(float range) {
            weapon.range = range;
        }

        public MeleeWeapon getWeapon(){
            MeleeWeapon w = weapon;
            this.reset();
            return w;
        }
    }
    
}
