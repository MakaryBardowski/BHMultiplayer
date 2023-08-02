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
import game.items.ItemTemplates.ItemTemplate;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@Getter
public abstract class Item {

    protected boolean droppable;
    protected String name;
    protected String description;
    protected ItemTemplate template;

    protected Item(ItemTemplate template) {
        this.template = template;
        this.droppable = true;
    }

    protected Item(ItemTemplate template, boolean droppable) {
        this.template = template;
        this.droppable = droppable;
    }

    public void drop(Vector3f itemSpawnpoint) {
        if (!droppable) {
            return;
        }
        Node node = new Node();
        Node childNode = createItemDropNode();
        applyInitialDropRotation(childNode);
        node.attachChild(childNode);
        node.scale(template.getDropData().getScale());
        node.setLocalTranslation(itemSpawnpoint);
        ClientGameAppState.getInstance().getPickableNode().attachChild(node);
        ParticleUtils.spawnItemPhysicalParticleShaded(node, itemSpawnpoint, this);
    }

    private Node createItemDropNode() {
        if (template.getDropPath() != null) {
            return (Node) ClientGameAppState.getInstance().getAssetManager().loadModel(template.getDropPath());
        } else {
            return (Node) ClientGameAppState.getInstance().getAssetManager().loadModel(ItemTemplates.RIFLE_MANNLICHER_95.getDropPath());
        }
    }

    private void applyInitialDropRotation(Node childNode) {
        Vector3f dr = template.getDropData().getRotation();
        float[] angles = {dr.getX(), dr.getY(), dr.getZ()};
        childNode.setLocalRotation(new Quaternion().fromAngles(angles));
    }

}
