/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Game.Mobs;

import Game.Items.Equippable;
import Game.Items.Item;
import com.Networking.Client.ClientGameAppState;

/**
 *
 * @author 48793
 */
public interface MobInterface {
    public void move(float tpf, ClientGameAppState cm);
    public void die();
    public void attack();
    public void receiveDamage(float damage);
    public void dealDamage(float damage, Mob mob);
    public void equip(Item e);
}
