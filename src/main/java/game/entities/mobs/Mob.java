/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs;

import game.entities.Destructible;
import game.items.Item;
import game.map.collision.CollidableInterface;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.Random;

/**
 *
 * @author 48793
 */
public abstract class Mob extends Destructible implements CollidableInterface, MobInterface {

    protected static final int EQUIPMENT_SIZE = 18;
    private static final float DEFAULT_SPEED = 10f; //20

    //mob stats
    protected float speed = DEFAULT_SPEED;
    protected Item[] equipment = new Item[EQUIPMENT_SIZE]; // 6 rows 3 cols

    //mob ai variables
    protected Destructible currentTarget;

    //sync
    protected MobType mobType;
    protected Vector3f serverLocation; // updated by the server
    protected Quaternion serverRotation;
    protected float posInterpolationValue;
    protected float rotInterpolationValue;

    public Mob(int id, Node node, String name) {
        super(id, name, node);
        this.serverLocation = node.getWorldTranslation();
        this.serverRotation = node.getLocalRotation();
    }

    public Vector3f getServerLocation() {
        return serverLocation;
    }

    public void setServerLocation(Vector3f serverLocation) {
        this.serverLocation = serverLocation;
        this.posInterpolationValue = 0;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getPosInterpolationValue() {
        return posInterpolationValue;
    }

    public void setPosInterpolationValue(float posInterpolationValue) {
        this.posInterpolationValue = posInterpolationValue;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Destructible getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Destructible currentTarget) {
        this.currentTarget = currentTarget;
    }

    public Item[] getEquipment() {
        return equipment;
    }

    public Quaternion getServerRotation() {
        return serverRotation;
    }

    public void setServerRotation(Quaternion serverRotation) {
        this.serverRotation = serverRotation;
        this.rotInterpolationValue = 0;

    }

    public float getRotInterpolationValue() {
        return rotInterpolationValue;
    }

    public void setRotInterpolationValue(float rotInterpolationValue) {
        this.rotInterpolationValue = rotInterpolationValue;
    }

    protected void dropEquipment() {
        Random r = new Random();
        for (int i = 0; i < equipment.length; i++) {
            Item item = equipment[i];
            if (item != null) {
                item.drop(node.getWorldTranslation().add(r.nextFloat(-0.25f, 0.25f), 2 + r.nextFloat(-1, 1), r.nextFloat(-0.25f, 0.25f)));
                equipment[i] = null;
            }
        }
    }

    public Item addToEquipment(Item item) {
        for (int i = 0; i < equipment.length; i++) {
            if (equipment[i] == null) {
                equipment[i] = item;
                break;
            }
        }
        return item;
    }

}
