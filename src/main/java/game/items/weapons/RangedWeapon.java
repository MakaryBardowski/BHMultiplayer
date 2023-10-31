/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.scene.Node;
import game.items.ItemTemplates.ItemTemplate;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */

@Getter
@Setter
public abstract class RangedWeapon extends Weapon {

    protected int ammo;
    protected int maxAmmo;
    protected float attackCooldown;
    protected float currentAttackCooldown = 0;
    protected FirerateControl firerateControl;

    public RangedWeapon(int id, float damage, ItemTemplate template, String name, Node node, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node);
        this.maxAmmo = maxAmmo;
        this.ammo = maxAmmo;
        attackCooldown = (1f / roundsPerSecond);
    }

    public RangedWeapon(int id, float damage, ItemTemplate template, String name, Node node, boolean droppable, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node, droppable);
        this.maxAmmo = maxAmmo;
        this.ammo = maxAmmo;
        attackCooldown = (1f / roundsPerSecond);
    }

}
