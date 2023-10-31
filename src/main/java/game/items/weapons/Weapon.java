/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.scene.Node;
import game.items.Equippable;
import game.items.Holdable;
import game.items.Item;
import game.items.ItemTemplates.ItemTemplate;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.items.ItemTemplates;

/**
 * @author tomasz potoczko
 */
public abstract class Weapon extends Item implements Attacks, Holdable {

    private static final DamageType DEFAULT_DAMAGE_TYPE = DamageType.PHYSICAL;
    protected float damage;
    protected float range;
    protected DamageType damageType;
    protected float attackSpeed;

    public Weapon(int id,float damage, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, template, name, node);
        this.damage = damage;
        damageType = DEFAULT_DAMAGE_TYPE;
    }

    public Weapon(int id,float damage, ItemTemplates.ItemTemplate template, String name, Node node,boolean droppable) {
        super(id, template, name, node,droppable);
        this.damage = damage;
        damageType = DEFAULT_DAMAGE_TYPE;
    }

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

    public float getDamage() {
        return damage;
    }

}
