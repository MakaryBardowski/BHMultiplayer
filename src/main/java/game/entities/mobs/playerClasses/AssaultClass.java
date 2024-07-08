/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs.playerClasses;

import game.items.ItemTemplates;
import static game.items.ItemTemplates.*;
import java.util.List;

/**
 *
 * @author 48793
 */
public class AssaultClass extends PlayerClass {

    @Override
    public String getDescription() {
        return "In the grim and unforgiving battlegrounds of The Great War, "
                + "the Trench Stormer emerges as the relentless force armed "
                + "with a knife and a M1895 rifle. "
                + "These soldiers specialize in breaching enemy defenses and wreaking havoc in "
                + "the midst of combat.";
    }

    @Override
    public List<ItemTemplates.ItemTemplate> getStartingEquipmentTemplates() {
        return List.of(
                KNIFE,
                RIFLE_MANNLICHER_95,
                RIFLE_AMMO_PACK,
                SMOKE_GRENADE,
                BOOTS_TRENCH
        );

    }

}
