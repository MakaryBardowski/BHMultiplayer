package game.items.misc;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.mobs.Mob;
import game.items.Item;
import game.items.ItemTemplates;
import messages.items.MobItemInteractionMessage;
import messages.items.NewAmmoPackMessage;
import messages.items.NewMiscItemMessage;

public class Report extends Item {
    public Report(int id, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, template, name, node);
    }

    public Report(int id, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
    }

    @Override
    public void onShot(Mob shooter, float damage) {

    }

    @Override
    public void onInteract() {
        ClientGameAppState gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        var msg = new NewMiscItemMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
