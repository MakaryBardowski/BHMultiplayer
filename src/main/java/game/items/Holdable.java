/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.items;

import game.entities.mobs.player.Player;

/**
 *
 * @author 48793
 */
public interface Holdable extends Equippable{
    void playerHoldInRightHand(Player p);
    void playerUseInRightHand(Player p);
}