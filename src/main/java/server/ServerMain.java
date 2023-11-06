package server;

import com.jme3.app.Application;
import game.entities.mobs.Mob;
import game.entities.factories.PlayerFactory;
import game.entities.mobs.Player;
import messages.messageListeners.ServerMessageListener;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;

import com.jme3.network.ConnectionListener;
import com.jme3.network.Filter;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import networkingUtils.NetworkingInitialization;
import com.jme3.network.Filters;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import game.entities.Chest;
import game.entities.Collidable;
import game.entities.DecorationTemplates;
import game.entities.DecorationTemplates.DecorationTemplate;
import game.entities.Destructible;
import game.entities.DestructibleDecoration;
import game.entities.InteractiveEntity;
import game.entities.StatusEffectContainer;
import game.entities.factories.DestructibleDecorationFactory;
import game.entities.grenades.ThrownGrenade;
import game.entities.mobs.HumanMob;
import game.items.AmmoPack;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.factories.ItemFactory;
import game.items.weapons.Grenade;
import game.items.weapons.Knife;
import game.items.weapons.Pistol;
import game.items.weapons.Rifle;
import game.map.MapGenerator;
import game.map.collision.WorldGrid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import messages.GrenadePosUpdateMessage;
import messages.items.ChestItemInteractionMessage;
import static messages.items.ChestItemInteractionMessage.ChestItemInteractionType.INSERT;
import messages.items.MobItemInteractionMessage;
import messages.items.MobItemInteractionMessage.ItemInteractionType;
import messages.items.SetDefaultItemMessage;

public class ServerMain extends AbstractAppState implements ConnectionListener, MessageListener<HostedConnection> {

    @Getter
    private Server server;

    @Getter
    private static ServerMain instance;
    private static final byte MAX_PLAYERS = 4;
    private final float TIME_PER_TICK = 0.0156f;

    @Getter
    private final ConcurrentHashMap<Integer, InteractiveEntity> mobs = new ConcurrentHashMap<>();
    private float tickTimer;

    private int currentMaxId = 0;
    private final AssetManager assetManager;
    private final RenderManager renderManager;
    private final Node rootNode;

    @Getter
    private WorldGrid grid;

    @Getter
    private final HashMap<Integer, HostedConnection> connectionsById = new HashMap<>(MAX_PLAYERS);

    @Getter
    private final int BLOCK_SIZE = 4;

    @Getter
    private final int COLLISION_GRID_CELL_SIZE = 16;

    @Getter
    private final int MAP_SIZE = 39;

    @Getter
    private byte[][][] map;

    public ServerMain(AssetManager assetManager, RenderManager renderManager) {
        this.assetManager = assetManager;
        this.renderManager = renderManager;
        this.rootNode = new Node("server rootNode");
        instance = this;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        createMap();
        initializeCollisionGrid();
        startServer();
        pupulateMap();
    }

    @Override
    public void update(float tpf) {
        rootNode.updateLogicalState(tpf);
        tickTimer += tpf;

        if (tickTimer >= TIME_PER_TICK) {
            tickTimer = 0;
            mobs.entrySet().stream().forEach(i -> {
                //to be seriously optimized
                if (i.getValue() instanceof Destructible d) {
                    if (d instanceof StatusEffectContainer c) {
                        c.updateTemporaryEffectsServer();
                    }
                    if (d instanceof Mob x) {
                        server.broadcast(new MobPosUpdateMessage(x.getId(), x.getNode().getWorldTranslation()));
                        server.broadcast(new MobRotUpdateMessage(x.getId(), x.getNode().getLocalRotation()));
                    }
                } else if (i.getValue() instanceof ThrownGrenade x) {
                    server.broadcast(new GrenadePosUpdateMessage(x.getId(), x.getNode().getWorldTranslation()));
                }
            }
            );
        }

    }

    @Override
    public void connectionAdded(Server server, HostedConnection hc) {
        Player newPlayer = registerPlayer(hc);
        connectionsById.put(newPlayer.getId(), hc);

        List<Item> itemsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Item)
                .map(entity -> (Item) entity.getValue())
                .toList();
        List<Mob> mobsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Mob && entry.getValue() != newPlayer)
                .map(entity -> (Mob) entity.getValue())
                .toList();
        List<Chest> chestsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Chest)
                .map(entity -> (Chest) entity.getValue())
                .toList();

        List<DestructibleDecoration> destructibleDecorationsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof DestructibleDecoration)
                .map(entity -> (DestructibleDecoration) entity.getValue())
                .toList();

        itemsInGame.forEach(item -> {
            AbstractMessage msg = item.createNewEntityMessage();
            msg.setReliable(true);
            server.broadcast(Filters.in(hc), msg);
        });

        mobsInGame.forEach(mob -> {
            AbstractMessage msg = mob.createNewEntityMessage();
            server.broadcast(Filters.in(hc), msg);
        });

        mobsInGame.forEach(mob -> {
            sendNewEntityEquipmentInfo(mob, Filters.in(hc));
        });

        destructibleDecorationsInGame.forEach(dd -> {
            AbstractMessage msg = dd.createNewEntityMessage();
            server.broadcast(Filters.in(hc), msg);
        });

        chestsInGame.forEach(chest -> {
            AbstractMessage chestMsg = chest.createNewEntityMessage();
            server.broadcast(Filters.in(hc), chestMsg);
            for (Item item : chest.getEquipment()) {
                if (item != null) {
                    ChestItemInteractionMessage msg = new ChestItemInteractionMessage(item, chest, INSERT);
                    msg.setReliable(true);
                    server.broadcast(Filters.in(hc), msg);
                }
            }
        });

        SetPlayerMessage messageToNewPlayer = new SetPlayerMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        messageToNewPlayer.setReliable(true);
        server.broadcast(Filters.in(hc), messageToNewPlayer);

        // send info about new player eq
        PlayerJoinedMessage msg = new PlayerJoinedMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        msg.setReliable(true);
        server.broadcast(Filters.notEqualTo(hc), msg);

        sendNewEntityEquipmentInfo(newPlayer, null);

    }

    @Override
    public void connectionRemoved(Server server, HostedConnection hc) {
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
    }

    private Item registerItemAndNotifyTCP(ItemTemplate template, boolean droppable, Filter<HostedConnection> notificationFilter) {
        Item i = registerItemLocal(template, droppable);
        sendMessageTCP(i.createNewEntityMessage(), notificationFilter);
        return i;
    }

    public Item registerItemLocal(ItemTemplate template, boolean droppable) {
        ItemFactory ifa = new ItemFactory(null);
        Item item = ifa.createItem(currentMaxId++, template, droppable);
        return registerEntityLocal(item);
    }

    private <T extends InteractiveEntity> T registerEntityLocal(T entity) {
        this.mobs.put(entity.getId(), entity);
        return entity;
    }

    private void sendNewEntityEquipmentInfo(Mob mob, Filter<HostedConnection> filter) {
        if (mob instanceof HumanMob hm) {
            SetDefaultItemMessage dhmsg = new SetDefaultItemMessage(hm.getDefaultHelmet(), hm);
            sendMessageTCP(dhmsg, filter);

            SetDefaultItemMessage dvmsg = new SetDefaultItemMessage(hm.getDefaultVest(), hm);
            sendMessageTCP(dvmsg, filter);

            SetDefaultItemMessage dgmsg = new SetDefaultItemMessage(hm.getDefaultGloves(), hm);
            sendMessageTCP(dgmsg, filter);

            SetDefaultItemMessage dbmsg = new SetDefaultItemMessage(hm.getDefaultBoots(), hm);
            sendMessageTCP(dbmsg, filter);

            Item[] initalEq = {hm.getHelmet(), hm.getVest(), hm.getGloves(), hm.getBoots()};

            for (Item i : initalEq) {
                if (i != null) {
                    MobItemInteractionMessage pmsg = new MobItemInteractionMessage(i, mob, ItemInteractionType.EQUIP);
                    sendMessageTCP(pmsg, filter);
                }
            }
        }

        for (Item i : mob.getEquipment()) {
            if (i != null) {
                MobItemInteractionMessage pmsg = new MobItemInteractionMessage(i, mob, ItemInteractionType.PICK_UP);
                sendMessageTCP(pmsg, filter);
            }
        }


    }

    private void sendMessageTCP(AbstractMessage imsg, Filter<HostedConnection> filter) {
        imsg.setReliable(true);
        if (filter == null) {
            server.broadcast(imsg);
        } else {
            server.broadcast(filter, imsg);
        }
    }

    public void populateMap() {
        registerRandomChest(new Vector3f(17, 4, 5));
        registerRandomChest(new Vector3f(17 + 2.3f * 0.8f, 4, 5));

    }

    public void insertIntoCollisionGrid(Collidable c) {
        grid.insert(c);
    }

    private void pupulateMap() {
        for (int i = 0; i < 40; i++) {
            Random r = new Random();
            registerRandomChest(new Vector3f(r.nextInt(38 * 4) + 4, 4, r.nextInt(38 * 4) + 4));
            registerPlayer(null).setPositionServer(new Vector3f(r.nextInt(38 * 4) + 4, 4, r.nextInt(38 * 4) + 4));
            registerRandomDestructibleDecoration(new Vector3f(r.nextInt(38 * 4) + 4, 4, r.nextInt(38 * 4) + 4));
        }
    }

    private void registerRandomDestructibleDecoration(Vector3f pos) {
        int randomNumber = new Random().nextInt(3);
        DecorationTemplate template = DecorationTemplates.TABLE;
        if (randomNumber == 0) {
            template = DecorationTemplates.TABLE;
        } else if (randomNumber == 1) {
            template = DecorationTemplates.BARBED_WIRE;
        } else if (randomNumber == 2) {
            template = DecorationTemplates.MINE;
        }

        DestructibleDecoration d = DestructibleDecorationFactory.createDecoration(currentMaxId++, rootNode, pos, template, assetManager);
        registerEntityLocal(d);
        insertIntoCollisionGrid(d);
    }

    public Player registerPlayer(HostedConnection hc) {
        Helmet playerHead = (Helmet) registerItemAndNotifyTCP(ItemTemplates.HEAD_1, false, Filters.notIn(hc));
        Vest playerVest = (Vest) registerItemAndNotifyTCP(ItemTemplates.TORSO_1, false, Filters.notIn(hc));
        Gloves playerGloves = (Gloves) registerItemAndNotifyTCP(ItemTemplates.HAND_1, false, Filters.notIn(hc));
        Boots playerBoots = (Boots) registerItemAndNotifyTCP(ItemTemplates.LEG_1, false, Filters.notIn(hc));
        Item playerRifle;

        int random = new Random().nextInt(3);
        if (random == 0) {
            playerRifle = (Rifle) registerItemAndNotifyTCP(ItemTemplates.RIFLE_MANNLICHER_95, true, Filters.notIn(hc));
        } else if (random == 1) {
            playerRifle = (Pistol) registerItemAndNotifyTCP(ItemTemplates.PISTOL_C96, true, Filters.notIn(hc));

        } else {
            playerRifle = (Grenade) registerItemAndNotifyTCP(ItemTemplates.SMOKE_GRENADE, true, Filters.notIn(hc));
        }
        Item playerGrenade = (Grenade) registerItemAndNotifyTCP(ItemTemplates.SMOKE_GRENADE, true, Filters.notIn(hc));
        Item playerKnife = (Knife) registerItemAndNotifyTCP(ItemTemplates.KNIFE, true, Filters.notIn(hc));

        Player player = new PlayerFactory(currentMaxId++, assetManager, rootNode, renderManager).createServerSide();

        player.addToEquipment(playerKnife);

        player.addToEquipment(playerRifle);
        player.equipServer(playerRifle);

        player.addToEquipment(playerGrenade);

        player.equipServer(playerHead);
        player.setDefaultHelmet(playerHead);

        player.equipServer(playerVest);
        player.setDefaultVest(playerVest);

        player.equipServer(playerGloves);
        player.setDefaultGloves(playerGloves);

        player.equipServer(playerBoots);
        player.setDefaultBoots(playerBoots);

        insertIntoCollisionGrid(player);

        return registerEntityLocal(player);
    }

    public Chest registerRandomChest(Vector3f offset) {
        Chest chest = Chest.createRandomChestServer(currentMaxId++, rootNode, offset, assetManager);
        Random r = new Random();
        int randomValue = r.nextInt(9);
        if (randomValue < 2) {
            Vest playerVest = (Vest) registerItemLocal(ItemTemplates.VEST_TRENCH, true);
            playerVest.setArmorValue(1.05f+r.nextFloat(0f, 0.25f));
            chest.addToEquipment(playerVest);
        }
        if (randomValue >= 1 && randomValue <= 4) {
            Boots playerBoots = (Boots) registerItemLocal(ItemTemplates.BOOTS_TRENCH, true);
            playerBoots.setArmorValue(0.65f+r.nextFloat(0f, 0.25f));
            chest.addToEquipment(playerBoots);
        }

        if (randomValue == 5) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.PISTOL_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        } else if (randomValue == 6) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.RIFLE_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        } else  if (randomValue == 7) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.SMG_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        } else if (randomValue == 8) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.SNIPER_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        }
        

        insertIntoCollisionGrid(chest);

        return registerEntityLocal(chest);
    }

    private void startServer() {
        try {
            server = Network.createServer(NetworkingInitialization.PORT);
            server.addConnectionListener(this);
            server.addMessageListener(new ServerMessageListener(this));
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeCollisionGrid() {
        grid = new WorldGrid(MAP_SIZE, BLOCK_SIZE, COLLISION_GRID_CELL_SIZE);

    }

    private void createMap() {
        map = new MapGenerator().createBossLogicMap(MAP_SIZE);
    }

    public int getAndIncreaseNextEntityId() {
        return currentMaxId++;
    }

    public int getNextEntityId() {
        return currentMaxId;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public static void removeEntityByIdServer(int id) {
        instance.getMobs().remove(id);
    }

}
