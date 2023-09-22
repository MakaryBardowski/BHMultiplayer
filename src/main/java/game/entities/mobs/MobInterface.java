/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities.mobs;

import game.items.Equippable;
import game.items.Item;
import client.ClientGameAppState;
import game.entities.Destructible;

/**
 *
 * @author 48793
 */
public interface MobInterface {

    public void move(float tpf, ClientGameAppState cm);

    public void attack();

    public void notifyServerAboutDealingDamage(float damage, Destructible mob);

    public void equip(Item e);

    public void unequip(Item e);

    public void equipServer(Item e);
    
    public void unequipServer(Item e);
}
