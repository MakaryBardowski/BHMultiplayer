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
public class MeleeWeapon extends Weapon{

    @Override
    public void equip(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequip(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void equipRight() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void equipLeft() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
<<<<<<< HEAD
=======

    @Override
    public void useRight() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void useLeft() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
>>>>>>> c2ad0c1136f6473af79cc5ba8753a2fa1b95973c
    
    public static class Builder{
        private MeleeWeapon weapon;

        public Builder(){
            this.reset();
        }

        public void reset(){
            this.weapon = new MeleeWeapon();
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
