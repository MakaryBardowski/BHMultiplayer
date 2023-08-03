/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities.mobs;

import game.items.Equippable;
import game.items.Item;
import client.ClientGameAppState;

/**
 *
 * @author 48793
 */
public interface MobInterface extends Damageable{
    public void move(float tpf, ClientGameAppState cm);
    public void attack();
    public void dealDamage(float damage, Mob mob);
    public void equip(Item e);
}
