/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs;

import Game.Items.ItemInterface;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public abstract class Mob {

    private static float DEFAULT_SPEED = 20f;
    protected int id;
    protected Node node;
    
    
    //mob stats
    protected float speed = DEFAULT_SPEED;
    protected float health = 50;
    protected float maxHealth = 50;
    protected ItemInterface[] equipment = new ItemInterface[18]; // 6 rows 3 cols

    
    //mob ai variables
    protected Mob currentTarget;
    
    //sync
    protected MobType mobType;
    protected Vector3f serverLocation; // updated by the server
    protected float interpolationValue;

    public Mob(int id, Node node) {
        this.id = id;
        this.node = node;
        this.serverLocation = node.getWorldTranslation();
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
        this.interpolationValue = 0;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getInterpolationValue() {
        return interpolationValue;
    }

    public void setInterpolationValue(float interpolationValue) {
        this.interpolationValue = interpolationValue;
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
    
    public boolean isDead(){
    return health <= 0;
    }

    public Mob getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Mob currentTarget) {
        this.currentTarget = currentTarget;
    }

    public ItemInterface[] getEquipment() {
        return equipment;
    }
    
    
    public void equipItem(ItemInterface i){
    i.equip(this);
    }
    

}
