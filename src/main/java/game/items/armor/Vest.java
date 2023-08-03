/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.Player;
import client.Main;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;

/**
 *
 * @author 48793
 */
public class Vest extends Armor {

    private boolean autoEquipsGloves;

    public Vest(ItemTemplates.ItemTemplate template) {
        super(template);
    }

    public Vest(ItemTemplates.ItemTemplate template, boolean droppable) {
        super(template, droppable);
    }

    public Vest(ItemTemplates.ItemTemplate template, boolean droppable, boolean autoEquipsGloves) {
        this(template, droppable);
        this.autoEquipsGloves = autoEquipsGloves;
    }

    @Override
    public void playerEquip(Player m) {
        Node n = m.getSkinningControl().getAttachmentsNode("Spine");
        n.detachAllChildren();
        Node vest = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath());
        setupModelLight(vest);
        n.attachChild(vest);
        setupModelShootability(vest, m.getId());
        equipGlovesIfAutoEquips(m);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void equipGlovesIfAutoEquips(Player m) {
        if (autoEquipsGloves) {
            m.equip(new Gloves(ItemTemplates.GLOVES_TRENCH,false));
        }
    }

}
