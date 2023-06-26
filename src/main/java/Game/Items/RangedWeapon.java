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
public class RangedWeapon extends Weapon{

    @Override
    public void equip(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequip(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public float getDamage(){
        //todo: math function for damage drop-off
        return this.damage;
    }
    
//    public static class Builder{
//        private RangedWeapon weapon;
//
//        public Builder(){
//            this.reset();
//        }
//
//        public void reset(){
//            this.weapon = new RangedWeapon();
//        }
//
//        public void setName(String name){
//            weapon.name = name;
//        }
//        
//        public void setDescription(String desc){
//            weapon.description = desc;
//        }
//
//        public void setSpeed(float attackSpeed) {
//            weapon.attackSpeed = attackSpeed;
//        }
//
//        public void setDamage(float damage) {
//            weapon.damage = damage;
//        }
//
//        public void setDamageType(DamageType damageType) {
//            weapon.damageType = damageType;
//        }
//        public void setRange(float range) {
//            weapon.range = range;
//        }
//
//        public RangedWeapon getWeapon(){
//            RangedWeapon w = weapon;
//            this.reset();
//            return w;
//        }
//    }
}
