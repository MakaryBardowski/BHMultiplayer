/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import client.ClientGameAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.lessvoid.nifty.controls.label.LabelControl;
import game.entities.Attribute;
import game.entities.Destructible;
import game.entities.IntegerAttribute;
import game.entities.InteractiveEntity;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
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
    public void attributeChangedNotification(int attributeId, Attribute copyOfNewAttribute) {
        super.attributeChangedNotification(attributeId, copyOfNewAttribute); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        if (attributeId == AMMO_ATTRIBUTE || attributeId == MAX_AMMO_ATTRIBUTE) {
            String text = (int) getAmmo() + "/" + (int) getMaxAmmo();
            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText(text);
        }
    }

    public abstract float calculateDamage(float distance);

    public abstract void reload(Mob wielder);

    protected Vector3f hitscan(Player p) {
        var cs = ClientGameAppState.getInstance();
        CollisionResults results = new CollisionResults();
        Vector3f shotDirection = p.getMainCamera().getDirection();
        Vector3f shotOrigin = p.getMainCamera().getLocation();
        Ray ray = new Ray(shotOrigin, shotDirection);
        float distanceToFirstWall = Float.MAX_VALUE;

        Vector3f cp = null;

        if (cs.getMapNode().collideWith(ray, results) > 0) {
            distanceToFirstWall = results.getClosestCollision().getDistance();
            cp = results.getClosestCollision().getContactPoint();

        }

        results = new CollisionResults();
        cs.getDestructibleNode().collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            cp = closest.getContactPoint();

            float distanceToFirstTarget = closest.getDistance();
            if (distanceToFirstTarget < distanceToFirstWall) {

                Integer hitId = Integer.valueOf(closest.getGeometry().getName());
                InteractiveEntity mobHit = cs.getMobs().get(hitId);
                
                if (mobHit instanceof Destructible destructible) {
                    destructible.onShot(p, calculateDamage(distanceToFirstTarget));
                }
            }
        }
        return cp;
    }
}
