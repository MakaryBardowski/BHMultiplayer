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
public class MedicClass extends PlayerClass {

    @Override
    public String getDescription() {
        return "In the heart of The Great War, the Field Medic is a beacon of hope amidst the chaos. "
                + "Armed with a trusty C96 Pistol and a life-saving Medpack, these selfless soldiers"
                + " prioritize healing comrades on the front lines. "
                + "Equipped to mend wounds, the Medic stands as a crucial lifeline, "
                + "turning the tide of battle with compassion and unwavering dedication to preserving life.";
    }

 @Override
    public List<ItemTemplates.ItemTemplate> getStartingEquipmentTemplates() {
        return List.of(
                KNIFE,
                PISTOL_C96,
                GAS_MASK,
                VEST_TRENCH,
                BOOTS_TRENCH,
                MEDPACK,
                MEDPACK
        );

    }

}
