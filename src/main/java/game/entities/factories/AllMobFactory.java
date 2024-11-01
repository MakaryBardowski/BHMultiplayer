/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities.factories;

import game.entities.mobs.Mob;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.network.Filter;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.DestructibleUtils;
import static game.entities.factories.MobSpawnType.HUMAN;
import static game.entities.factories.MobSpawnType.MUD_BEETLE;
import static game.entities.factories.MobSpawnType.TRAINING_DUMMY;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import game.entities.mobs.Player;
import game.entities.mobs.TrainingDummy;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.VestTemplate;
import game.items.armor.Armor;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import java.util.List;
import java.util.Random;
import server.ServerMain;
import statusEffects.EffectFactory;

/**
 *
 * @author 48793
 */
public class AllMobFactory extends MobFactory {

    public AllMobFactory(int id, AssetManager assetManager, Node mobNode) {
        super(id, assetManager, mobNode);
    }

    public AllMobFactory(int id, Node mobNode) {
        super(id, mobNode);

    }

    @Override
    public Mob createClientSide(MobSpawnType spawnType, Object... creationData) {
        if (spawnType == MUD_BEETLE) {
            MudBeetle p = createMudBeetle();
            return p;
        } else if (spawnType == HUMAN) {
            HumanMob p = createHumanMob();
            return p;
        } else if (spawnType == TRAINING_DUMMY) {
            TrainingDummy td = createTrainingDummy();
            return td;
        }
        return null;
    }

    @Override
    public Mob createServerSide(MobSpawnType spawnType, Object... creationData) {
        if (spawnType == MUD_BEETLE) {
            MudBeetle p = createMudBeetle();
            DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));
            return p;
        } else if (spawnType == HUMAN) {
            HumanMob p = createHumanMob();
            DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));

            var serverLevelManager = ServerMain.getInstance().getCurrentGamemode().getLevelManager();
            Helmet defaultHead = (Helmet) serverLevelManager.registerItemLocal(ItemTemplates.HEAD_1, false);
            Vest defaultVest = (Vest) serverLevelManager.registerItemLocal(ItemTemplates.TORSO_1, false);
            Gloves defaultGloves = (Gloves) serverLevelManager.registerItemLocal(ItemTemplates.HAND_1, false);
            Boots defaultBoots = (Boots) serverLevelManager.registerItemLocal(ItemTemplates.LEG_1, false);

            p.setDefaultHelmet(defaultHead);
            p.setDefaultVest(defaultVest);
            p.setDefaultGloves(defaultGloves);
            p.setDefaultBoots(defaultBoots);

            // player starts naked (equips bare body parts. overriden by starting eq later)
            p.equipServer(defaultHead);
            p.equipServer(defaultVest);
            p.equipServer(defaultGloves);
            p.equipServer(defaultBoots);

            var random = new Random();
            var randomNumber = random.nextFloat();

            if (randomNumber > 0.95f) {
                Item item = ServerMain.getInstance().getCurrentGamemode().getLevelManager().registerItemAndNotifyTCP(ItemTemplates.GAS_MASK, true, null);
                p.addToEquipment(item);
                p.equipServer(item);
            }
            if (randomNumber > 0.9f) {
                Item item = ServerMain.getInstance().getCurrentGamemode().getLevelManager().registerItemAndNotifyTCP(ItemTemplates.VEST_TRENCH, true, null);
                p.addToEquipment(item);
                p.equipServer(item);
            }
            if (randomNumber > 0.85f) {
                Item item = ServerMain.getInstance().getCurrentGamemode().getLevelManager().registerItemAndNotifyTCP(ItemTemplates.BOOTS_TRENCH, true, null);
                p.addToEquipment(item);
                p.equipServer(item);
            }

            if (randomNumber < 0.58f) {
                Item item = ServerMain.getInstance().getCurrentGamemode().getLevelManager().registerItemAndNotifyTCP(ItemTemplates.KNIFE, true, null);
                p.addToEquipment(item);
                p.equipServer(item);
            } else if (randomNumber < 0.78f) {
                Item item = ServerMain.getInstance().getCurrentGamemode().getLevelManager().registerItemAndNotifyTCP(ItemTemplates.AXE, true, null);
                p.addToEquipment(item);
                p.equipServer(item);
            } else if (randomNumber < 0.98f) {
                Item item = ServerMain.getInstance().getCurrentGamemode().getLevelManager().registerItemAndNotifyTCP(ItemTemplates.PISTOL_C96, true, null);
                p.addToEquipment(item);
                p.equipServer(item);
            } else if (randomNumber < 0.1f) {
                Item item = ServerMain.getInstance().getCurrentGamemode().getLevelManager().registerItemAndNotifyTCP(ItemTemplates.LMG_HOTCHKISS, true, null);
                p.addToEquipment(item);
                p.equipServer(item);
            }

            var procsEverySeconds = 1; //10
            var regenEffect = EffectFactory.createRegenerationEffect(p, 1, 64 * procsEverySeconds);
            p.addEffect(regenEffect);
            return p;
        } else if (spawnType == TRAINING_DUMMY) {
            var p = createTrainingDummy();
            DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));
            return p;
        }
        return null;
    }

    private MudBeetle createMudBeetle() {
        Node playerNode = loadBeetleModel();
        String name = "Mud Beetle " + id;
        SkinningControl skinningControl = getSkinningControl(playerNode);
        AnimComposer composer = getAnimComposer(playerNode);
//        System.out.println("[AnimalMobFactory] create mud beetle id " + id);
        return new MudBeetle(id, playerNode, name, skinningControl, composer);
    }

    private HumanMob createHumanMob() {
        Node playerNode = loadHumanModel();
        String name = "Human " + id;
        SkinningControl skinningControl = getSkinningControl(playerNode);
        AnimComposer composer = getAnimComposer(playerNode);

        var human = new HumanMob(id, playerNode, name, skinningControl, composer);

        return human;
    }

    private TrainingDummy createTrainingDummy() {
        Node playerNode = loadDummyModel();
        String name = "Training Dummy " + id;

        var human = new TrainingDummy(id, playerNode, name);

        return human;
    }

    private Node loadBeetleModel() {
        Node node = (Node) assetManager.loadModel("Models/Mobs/MudBeetle.j3o");
        return node;
    }

    private Node loadHumanModel() {
        Node node = (Node) assetManager.loadModel(HumanMob.HUMAN_SKELETON_RIG_PATH);
        return node;
    }

    private Node loadDummyModel() {
        Node node = (Node) assetManager.loadModel("Models/Mobs/Dummy.j3o");
        return node;
    }

    private SkinningControl getSkinningControl(Node node) {
        return node.getChild(0).getControl(SkinningControl.class);
    }

    private AnimComposer getAnimComposer(Node node) {
        return node.getChild(0).getControl(AnimComposer.class);
    }
}
