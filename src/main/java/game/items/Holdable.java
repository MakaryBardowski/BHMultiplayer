/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.items;

import game.entities.mobs.Player;

/**
 *
 * @author tomasz potoczko
 */
public interface Holdable {
    void playerHoldRight(Player p);
    void playerUseRight(Player p);
}