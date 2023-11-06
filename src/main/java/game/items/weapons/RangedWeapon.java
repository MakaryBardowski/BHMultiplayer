/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import client.ClientGameAppState;
import com.jme3.scene.Node;
import de.lessvoid.nifty.controls.label.LabelControl;
import game.entities.IntegerAttribute;
import game.entities.mobs.Mob;
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

    public static int AMMO_ATTRIBUTE = 2;
    public static int MAX_AMMO_ATTRIBUTE = 3;

    public RangedWeapon(int id, float damage, ItemTemplate template, String name, Node node, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node, roundsPerSecond);
        attributes.put(AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
        attributes.put(MAX_AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
    }

    public RangedWeapon(int id, float damage, ItemTemplate template, String name, Node node, boolean droppable, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node, droppable, roundsPerSecond);
        attributes.put(AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
        attributes.put(MAX_AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
    }

    public int getAmmo() {
        return ((IntegerAttribute) attributes.get(AMMO_ATTRIBUTE)).getValue();
    }

    public void setAmmo(int ammo) {
        ((IntegerAttribute) attributes.get(AMMO_ATTRIBUTE)).setValue(ammo);
    }

    public int getMaxAmmo() {
        return ((IntegerAttribute) attributes.get(MAX_AMMO_ATTRIBUTE)).getValue();
    }

    @Override
    public void attributeChangedNotification(int attributeId) {
        String text = (int) getAmmo() + "/" + (int) getMaxAmmo();
        ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText(text);
    }

    public abstract void reload(Mob wielder);

}
