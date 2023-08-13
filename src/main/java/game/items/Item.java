/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items;

import client.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.effects.ParticleUtils;
import game.entities.InteractiveEntity;
import game.items.ItemTemplates.ItemTemplate;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@Getter
public abstract class Item extends InteractiveEntity {

    protected boolean droppable;
    protected String description;
    protected ItemTemplate template;

    protected Item(int id, ItemTemplate template,String name,Node node) {
        super(id, name, node);
        this.template = template;
        this.droppable = true;
    }

    protected Item(int id, ItemTemplate template,String name,Node node, boolean droppable) {
        super(id, name, node);
        this.template = template;
        this.droppable = droppable;
    }

    public void drop(Vector3f itemSpawnpoint) {
        if (!droppable) {
            return;
        }
        Node parentNode = new Node();
        applyInitialDropRotation(node);
        parentNode.attachChild(node);
        parentNode.scale(template.getDropData().getScale());
        parentNode.setLocalTranslation(itemSpawnpoint);
        ClientGameAppState.getInstance().getPickableNode().attachChild(parentNode);
        ParticleUtils.spawnItemPhysicalParticleShaded(parentNode, itemSpawnpoint, this);
    }



    private void applyInitialDropRotation(Node childNode) {
        Vector3f dr = template.getDropData().getRotation();
        float[] angles = {dr.getX(), dr.getY(), dr.getZ()};
        childNode.setLocalRotation(new Quaternion().fromAngles(angles));
    }

}
