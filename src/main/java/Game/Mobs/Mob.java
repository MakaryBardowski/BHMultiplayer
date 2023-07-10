/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs;

import Game.Items.Item;
import Game.Map.Collision.CollidableInterface;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public abstract class Mob implements CollidableInterface, MobInterface {
    protected static final int EQUIPMENT_SIZE = 18;

    private static float DEFAULT_SPEED = 10f; //20
    protected int id;
    protected Node node;

    //mob stats
    protected String name;
    protected float speed = DEFAULT_SPEED;
    protected float health = 10;
    protected float maxHealth = 10;
    protected Item[] equipment = new Item[EQUIPMENT_SIZE]; // 6 rows 3 cols

    //mob ai variables
    protected Mob currentTarget;

    //sync
    protected MobType mobType;
    protected Vector3f serverLocation; // updated by the server
    protected Quaternion serverRotation;
    protected float posInterpolationValue;
    protected float rotInterpolationValue;

    public Mob(int id, Node node, String name) {
        this.id = id;
        this.node = node;
        this.name = name;
        this.serverLocation = node.getWorldTranslation();
        this.serverRotation = node.getLocalRotation();
    }

    public int getId() {
        return id;
    }

    public Node getNode() {
        return node;
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

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Mob getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Mob currentTarget) {
        this.currentTarget = currentTarget;
    }

    public Item[] getEquipment() {
        return equipment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


}
