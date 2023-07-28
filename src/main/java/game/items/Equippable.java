/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.items;

import game.mobs.Player;

/**
 *
 * @author 48793
 */
public interface Equippable {

    public void playerEquip(Player m);

    public void playerUnequip(Player m);
}
