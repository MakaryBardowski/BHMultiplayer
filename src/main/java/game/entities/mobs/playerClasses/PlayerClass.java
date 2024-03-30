/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs.playerClasses;

import game.items.Item;
import game.items.ItemTemplates;
import game.map.Map;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author 48793
 */
public abstract class PlayerClass {
    
    
    
    public static final PlayerClass getClassByIndex(int index){
    if(index == 0)
        return new AssaultClass();
    if(index == 1)
        return new MedicClass();
    if(index == 2)
        return new CombatEngineerClass();
    return null;
    }
    
    public abstract String getDescription();
    public abstract List<ItemTemplates.ItemTemplate> getStartingEquipmentTemplates();
}
