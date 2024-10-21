/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.scene.Node;
import game.entities.Attribute;
import game.entities.FloatAttribute;
import game.entities.mobs.HumanMob;
import game.items.Equippable;
import game.items.Holdable;
import game.items.Item;
import game.items.ItemTemplates.ItemTemplate;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.items.ItemTemplates;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tomasz potoczko
 */
public abstract class Weapon extends Item implements Attacks, Holdable {

    public static final int ATTACKS_PER_SEC_ATTRIBUTE = 0;
    public static final int DAMAGE_ATTRIBUTE = 1;

    private static final DamageType DEFAULT_DAMAGE_TYPE = DamageType.PHYSICAL;

    @Getter
    @Setter
    protected float attackCooldown;

    @Getter
    @Setter
    protected float currentAttackCooldown = 0;
    protected DamageType damageType;

    @Getter
    @Setter
    protected FirerateControl firerateControl;

    public Weapon(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node, float attacksPerSecond) {
        super(id, template, name, node);
        attackCooldown = (1f / attacksPerSecond);
        damageType = DEFAULT_DAMAGE_TYPE;

        attributes.put(DAMAGE_ATTRIBUTE, new FloatAttribute(damage));
        attributes.put(ATTACKS_PER_SEC_ATTRIBUTE, new FloatAttribute(attacksPerSecond));
    }

    public Weapon(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable, float attacksPerSecond) {
        super(id, template, name, node, droppable);
        attackCooldown = (1f / attacksPerSecond);
        damageType = DEFAULT_DAMAGE_TYPE;

        attributes.put(DAMAGE_ATTRIBUTE, new FloatAttribute(damage));
        attributes.put(ATTACKS_PER_SEC_ATTRIBUTE, new FloatAttribute(attacksPerSecond));
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public float getAttacksPerSecond() {
        return getFloatAttribute(ATTACKS_PER_SEC_ATTRIBUTE).getValue();
    }

    public float getDamage() {
        return getFloatAttribute(DAMAGE_ATTRIBUTE).getValue();
    }

    @Override
    public void attributeChangedNotification(int attributeId, Attribute copyOfAttribute) {
        super.attributeChangedNotification(attributeId, copyOfAttribute); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        if (attributeId == ATTACKS_PER_SEC_ATTRIBUTE) {
            attackCooldown = (1f / ((FloatAttribute) copyOfAttribute).getValue());
        }
    }

    @Override
    public void humanMobUnequip(HumanMob m) {
        throw new UnsupportedOperationException("mob weapon "+this+" equip not implemented yet");
    };

    @Override
    public void humanMobEquip(HumanMob m) {
        throw new UnsupportedOperationException("mob "+this+" equip not implemented yet");
    };

}
