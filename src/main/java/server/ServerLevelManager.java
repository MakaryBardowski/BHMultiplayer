/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Filter;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Server;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.Chest;
import game.entities.Collidable;
import game.entities.DecorationTemplates;
import game.entities.DestructibleDecoration;
import game.entities.InteractiveEntity;
import game.entities.LevelExit;
import game.entities.factories.AllMobFactory;
import game.entities.factories.DecorationFactory;
import game.entities.factories.MobSpawnType;
import game.entities.factories.PlayerFactory;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.entities.mobs.MudBeetle;
import game.entities.mobs.Player;
import game.items.AmmoPack;
import game.items.Item;
import game.items.ItemTemplates;
import static game.items.ItemTemplates.SMOKE_GRENADE;
import game.items.armor.Armor;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.factories.ItemFactory;
import game.items.weapons.Knife;
import game.items.weapons.Rifle;
import game.map.MapGenerator;
import game.map.MapType;
import game.map.MobGenerator;
import lombok.Getter;
import game.map.collision.WorldGrid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import jme3utilities.math.Vector3i;
import messages.InstantEntityPosCorrectionMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;
import messages.gameSetupMessages.NextLevelMessage;
import messages.items.ChestItemInteractionMessage;
import static messages.items.ChestItemInteractionMessage.ChestItemInteractionType.INSERT;
import messages.items.MobItemInteractionMessage;
import messages.items.SetDefaultItemMessage;
import messages.NewIndestructibleDecorationMessage;
import pathfinding.AStar;
import static server.ServerMain.MAX_PLAYERS;

/**
 *
 * @author 48793
 */
public class ServerLevelManager extends LevelManager {

    private final Server server;
    private final Random RANDOM = new Random();
    private static final String LEVEL_INDEX_OUT_OF_BOUNDS_MESSAGE = "Level index out of bounds. Provided: ";
    private final AssetManager assetManager;
    private final RenderManager renderManager;
    private final List<Player> players = new ArrayList<>(MAX_PLAYERS);

    @Getter
    private final Node rootNode;

    private int currentMaxId = 0;

    @Getter
    private final int BLOCK_SIZE = 3;

    @Getter
    private final int COLLISION_GRID_CELL_SIZE = 18;

    @Getter
    private final int MAP_SIZE = 39;

    @Getter
    private WorldGrid grid;

    @Getter
    private byte[][][] map;

    @Getter
    private final ConcurrentHashMap<Integer, InteractiveEntity> mobs = new ConcurrentHashMap<>();

    public ServerLevelManager(int levelCount, Server server) {
        this.assetManager = Main.getInstance().getAssetManager();
        this.renderManager = Main.getInstance().getRenderManager();
        this.rootNode = new Node("server rootNode");
        this.rootNode.setCullHint(Spatial.CullHint.Always);
        Main.getInstance().getRootNode().attachChild(this.rootNode);
        this.server = server;
        levelSeeds = new long[levelCount];
        levelTypes = new MapType[levelCount];
    }

    public void setupLevelSeeds() {
        for (int i = 0; i < levelSeeds.length; i++) {
            levelSeeds[i] = RANDOM.nextLong();
        }

        levelTypes[0] = MapType.ARMORY;
        for (int i = 1; i < levelTypes.length; i++) {
            if (i % 5 == 0) {
                levelTypes[i] = MapType.BOSS;
                continue;
            }

            levelTypes[i] = MapType.CASUAL;
        }

    }

    public void createMap(long seed, MapType mapType) {
        System.out.println("SERVER: generating map of type " + mapType + " seed " + seed);
        System.out.println("grid is: " + grid);
        var mapGenResult = new MapGenerator(seed, mapType, MAP_SIZE).decideAndGenerateMapServer(new byte[MAP_SIZE][MAP_SIZE][MAP_SIZE]);

        map = mapGenResult.getMap();
        registerLevelExit(mapType);

        var rooms = mapGenResult.getRooms();
        if (rooms != null) {
            var rand = new Random(seed);
            var roomAmount = rooms.size();
            var startingRoom = rooms.get(rand.nextInt(roomAmount));

            var spawnpoints = new ArrayList<Vector3i>(); // use list because hashset can have collisions
            for (Player p : players) {
                Vector3i playerSpawnpoint;
                do {
                    playerSpawnpoint = new Vector3i(
                            rand.nextInt(startingRoom.getStartX() + 1, startingRoom.getEndX() - 1),
                            startingRoom.getStartY(),
                            rand.nextInt(startingRoom.getStartZ() + 1, startingRoom.getEndZ() - 1));
                } while (spawnpoints.contains(playerSpawnpoint));

                spawnpoints.add(playerSpawnpoint);

                Vector3f playerSpawnpointInWorld = new Vector3f(
                        playerSpawnpoint.x() * BLOCK_SIZE + 0.5f * BLOCK_SIZE,
                        BLOCK_SIZE,
                        playerSpawnpoint.z() * BLOCK_SIZE + 0.5f * BLOCK_SIZE
                );

                grid.remove(p);
                p.getNode().setLocalTranslation(playerSpawnpointInWorld);
                grid.insert(p);
                InstantEntityPosCorrectionMessage corrMsg = new InstantEntityPosCorrectionMessage(p, playerSpawnpointInWorld);
                corrMsg.setReliable(true);
                server.broadcast(corrMsg);

            }

        }
    }

    private void initializeCollisionGrid() {
        grid = new WorldGrid(MAP_SIZE, BLOCK_SIZE, COLLISION_GRID_CELL_SIZE);
    }

    private boolean levelIndexOutOfBounds(int levelIndex) {
        return levelIndex < 0 || levelIndex >= levelSeeds.length;
    }

    @Override
    public final void jumpToLevel(int levelIndex) {
        clearEntities();

        currentLevelIndex = levelIndex;
        if (levelIndexOutOfBounds(levelIndex)) {
            System.err.println(LEVEL_INDEX_OUT_OF_BOUNDS_MESSAGE + levelIndex);
        }

        if (levelIndex == 0) {
            initializeCollisionGrid();
        }

        var newLevelSeed = levelSeeds[levelIndex];
        var newLevelType = levelTypes[levelIndex];
        System.out.println("SERVER: level seed " + newLevelSeed);
        Main.getInstance().enqueue(() -> {

            createMap(newLevelSeed, newLevelType);
            AStar.setPathfindingMap(map);
            MobGenerator mg = new MobGenerator(newLevelSeed, levelIndex);
            mg.spawnMobs(map);
            notifyPlayersAboutNewLevelEntities();
        });
    }

    public void registerRandomDestructibleDecoration(Vector3f pos) {
        int randomNumber = new Random().nextInt(3);
        DecorationTemplates.DecorationTemplate template = DecorationTemplates.TABLE;
        if (randomNumber == 0) {
            template = DecorationTemplates.TABLE;
        } else if (randomNumber == 1) {
            template = DecorationTemplates.BARBED_WIRE;
        } else if (randomNumber == 2) {
            template = DecorationTemplates.MINE;
        }

        DestructibleDecoration d = DecorationFactory.createDestructibleDecoration(currentMaxId++, rootNode, pos, template, assetManager);
        registerEntityLocal(d);
        insertIntoCollisionGrid(d);
    }

    public Mob registerMob(MobSpawnType spawnType) {
        Mob mob = new AllMobFactory(currentMaxId++, assetManager, rootNode).createServerSide(spawnType);

        insertIntoCollisionGrid(mob);

        return registerEntityLocal(mob);
    }

    public Player registerPlayer(HostedConnection hc) {
        int playerClassIndex = (int) hc.getAttribute("class");
        Player player = new PlayerFactory(currentMaxId++, assetManager, rootNode, renderManager).createServerSide(null, playerClassIndex);

        player.setName(hc.getAttribute("nick"));
        System.out.println("ustawiono nazwe " + player.getName());

        Helmet defaultHead = (Helmet) registerItemAndNotifyTCP(ItemTemplates.HEAD_1, false, Filters.notIn(hc));
        Vest defaultVest = (Vest) registerItemAndNotifyTCP(ItemTemplates.TORSO_1, false, Filters.notIn(hc));
        Gloves defaultGloves = (Gloves) registerItemAndNotifyTCP(ItemTemplates.HAND_1, false, Filters.notIn(hc));
        Boots defaultBoots = (Boots) registerItemAndNotifyTCP(ItemTemplates.LEG_1, false, Filters.notIn(hc));

        player.setDefaultHelmet(defaultHead);
        player.setDefaultVest(defaultVest);
        player.setDefaultGloves(defaultGloves);
        player.setDefaultBoots(defaultBoots);

        // player starts naked (equips bare body parts. overriden by starting eq later)
        player.equipServer(defaultHead);
        player.equipServer(defaultVest);
        player.equipServer(defaultGloves);
        player.equipServer(defaultBoots);

        List<ItemTemplates.ItemTemplate> startingEquipmentTemplates = player.getPlayerClass().getStartingEquipmentTemplates();

        for (ItemTemplates.ItemTemplate template : startingEquipmentTemplates) {
            var otherPlayersFilter = Filters.notIn(hc);
            Item item = registerItemAndNotifyTCP(template, true, otherPlayersFilter);

            player.addToEquipment(item);

            if (item instanceof Armor) {
                player.equipServer(item);
            }
        }

        insertIntoCollisionGrid(player);

        players.add(player);

        return registerEntityLocal(player);
    }

    public Chest registerRandomChest(Vector3f offset) {
        Chest chest = Chest.createRandomChestServer(currentMaxId++, rootNode, offset, assetManager);
        Random r = new Random();
        int randomValue = r.nextInt(16);
        if (randomValue < 2) {
            Vest playerVest = (Vest) registerItemLocal(ItemTemplates.VEST_TRENCH, true);
            playerVest.setArmorValue(  1.05f + r.nextFloat(0f, 0.25f)   );
            chest.addToEquipment(playerVest);
        }
        if (randomValue >= 1 && randomValue <= 4) {
            Boots playerBoots = (Boots) registerItemLocal(ItemTemplates.BOOTS_TRENCH, true);
            playerBoots.setArmorValue(0.65f + r.nextFloat(0f, 0.25f));
            chest.addToEquipment(playerBoots);
        }

        if (randomValue == 5) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.PISTOL_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        } else if (randomValue == 6) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.RIFLE_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        } else if (randomValue == 7) {
//            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.SMG_AMMO_PACK, true);
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.LMG_AMMO_PACK, true);

            chest.addToEquipment(ammo);
        } else if (randomValue == 8) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.LMG_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        }
        if (randomValue == 11) {
            var lmg = registerItemLocal(ItemTemplates.LMG_HOTCHKISS, true);
            chest.addToEquipment(lmg);
        }
//        randomValue = 12;

        if (randomValue == 12) {
//            var helmet = registerItemLocal(ItemTemplates.TRENCH_HELMET, true);
//            chest.addToEquipment(helmet);
            var rifle = (Rifle) registerItemLocal(ItemTemplates.RIFLE_BORYSOV, true);
            chest.addToEquipment(rifle);
        }
//        randomValue = 13;
        if (randomValue == 12 || randomValue == 13) {
            var helmet = registerItemLocal(ItemTemplates.GAS_MASK, true);

            chest.addToEquipment(helmet);

            var rifle = (Rifle) registerItemLocal(ItemTemplates.RIFLE_MANNLICHER_95, true);
            chest.addToEquipment(rifle);
        }
        if (randomValue == 14) {
            var medpack = registerItemLocal(ItemTemplates.MEDPACK, true);

            chest.addToEquipment(medpack);
        }

        if (randomValue == 15) {
            var knife = (Knife) registerItemLocal(ItemTemplates.KNIFE, true);
            chest.addToEquipment(knife);

            var grenade = registerItemLocal(ItemTemplates.SMOKE_GRENADE, true);
            chest.addToEquipment(grenade);
        }

        //test
        var axe = registerItemLocal(ItemTemplates.AXE, true);
        chest.addToEquipment(axe);
        //test
        insertIntoCollisionGrid(chest);

        return registerEntityLocal(chest);
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

            List<Item> initialEq = new ArrayList<>();

            initialEq.add(hm.getHelmet());
            initialEq.add(hm.getVest());
            initialEq.add(hm.getGloves());
            initialEq.add(hm.getBoots());

            if (hm.getEquippedRightHand() != null) { // hands are required to attach gun hence the order
                initialEq.add((Item) hm.getEquippedRightHand());
            }

            for (Item i : initialEq) {
                if (i != null) {
                    MobItemInteractionMessage pmsg = new MobItemInteractionMessage(i, mob, MobItemInteractionMessage.ItemInteractionType.EQUIP);
                    sendMessageTCP(pmsg, filter);
                }
            }
        }

        for (Item i : mob.getEquipment()) {
            if (i != null) {
                MobItemInteractionMessage pmsg = new MobItemInteractionMessage(i, mob, MobItemInteractionMessage.ItemInteractionType.PICK_UP);
                sendMessageTCP(pmsg, filter);
            }
        }

    }

    public void notifyPlayersAboutNewLevelEntities() {
        var hostsByPlayerId = ServerMain.getInstance().getHostsByPlayerId();

        List<Item> itemsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Item)
                .map(entity -> (Item) entity.getValue())
                .toList();

        List<Mob> mobsInGame = mobs.entrySet().stream()
                .filter(entry -> {
                    // temporary fix, because currently every mob is a player so you get a duplicate on players
                    var notRealPlayer = hostsByPlayerId.get(entry.getValue().getId()) == null;
                    return entry.getValue() instanceof Mob && notRealPlayer;
                })
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
            server.broadcast(msg);
        });

        mobsInGame.forEach(mob -> {
            AbstractMessage msg = mob.createNewEntityMessage();
            server.broadcast(msg);
        });

        mobsInGame.forEach(mob -> {
            sendNewEntityEquipmentInfo(mob, null);
        });

        destructibleDecorationsInGame.forEach(dd -> {
            AbstractMessage msg = dd.createNewEntityMessage();
            server.broadcast(msg);
        });

        chestsInGame.forEach(chest -> {
            AbstractMessage chestMsg = chest.createNewEntityMessage();
            server.broadcast(null, chestMsg);
            for (Item item : chest.getEquipment()) {
                if (item != null) {
                    ChestItemInteractionMessage msg = new ChestItemInteractionMessage(item, chest, INSERT);
                    msg.setReliable(true);
                    server.broadcast(msg);
                }
            }
        });

    }

    public void addPlayerToGame(HostedConnection hc) {
        var hostsByPlayerId = ServerMain.getInstance().getHostsByPlayerId();
        Player newPlayer = registerPlayer(hc);
        System.out.println("[SERVER] added player " + newPlayer.getId());
        hostsByPlayerId.put(newPlayer.getId(), hc);

        List<Item> itemsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Item)
                .map(entity -> (Item) entity.getValue())
                .toList();

        List<Mob> mobsInGame = mobs.entrySet().stream()
                .filter(entry -> {
                    // temporary fix, because currently every mob is a player so you get a duplicate on players
                    var notRealPlayer = hostsByPlayerId.get(entry.getValue().getId()) == null;
                    return entry.getValue() instanceof Mob && entry.getValue() != newPlayer && notRealPlayer;
                })
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

        int playerClassIndex = (int) hc.getAttribute("class");

        SetPlayerMessage messageToNewPlayer = new SetPlayerMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation(), newPlayer.getName(), playerClassIndex);
        messageToNewPlayer.setReliable(true);
        server.broadcast(Filters.in(hc), messageToNewPlayer);

        // send info about new player eq
        PlayerJoinedMessage msg = new PlayerJoinedMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation(), newPlayer.getName(), playerClassIndex);
        msg.setReliable(true);
        server.broadcast(Filters.notEqualTo(hc), msg);

        sendNewEntityEquipmentInfo(newPlayer, null);

    }

    private void registerLevelExit(MapType mapType) {
        var exitPositionOnMapIntArray = new int[3];
        exitPositionOnMapIntArray[1] = 1;
        do {
            if (mapType == MapType.ARMORY) { // hardcoded - armory will be read from file
                exitPositionOnMapIntArray[0] = 6;
                exitPositionOnMapIntArray[2] = 8;
            } else {
                exitPositionOnMapIntArray[0] = 4 + RANDOM.nextInt(MAP_SIZE - 4);
                exitPositionOnMapIntArray[2] = 4 + RANDOM.nextInt(MAP_SIZE - 4);
            }
        } while (canNotPlaceCar(exitPositionOnMapIntArray));

        var pos = new Vector3f(
                exitPositionOnMapIntArray[0] * BLOCK_SIZE + 0.5f * BLOCK_SIZE,
                exitPositionOnMapIntArray[1] * BLOCK_SIZE,
                exitPositionOnMapIntArray[2] * BLOCK_SIZE + 0.5f * BLOCK_SIZE
        );
        var template = DecorationTemplates.EXIT_CAR;
        System.out.println("posCAR " + pos);
        var id = DecorationFactory.createIndestructibleDecoration(currentMaxId++, rootNode, pos, template, assetManager);
        insertIntoCollisionGrid(id);
        registerEntityLocal(id);
        var idm = new NewIndestructibleDecorationMessage(id);
        server.broadcast(idm);
    }

    private boolean canNotPlaceCar(int[] exitPositionOnMapIntArray) {
        return map[exitPositionOnMapIntArray[0]][exitPositionOnMapIntArray[1]][exitPositionOnMapIntArray[2]] != 0;
    }

    public int getAndIncreaseNextEntityId() {
        return currentMaxId++;
    }

    public int getNextEntityId() {
        return currentMaxId;
    }

    private void clearEntities() {
        var itemsToKeep = getItemsTokeep();
        mobs.forEach((id, entity) -> {
            if (isNotItemToKeep(entity, itemsToKeep) && isNotPlayer(entity)) { // if not an item to keep
                System.out.println("destroying " + entity);
                entity.destroyAndNotifyClients();
            }
        });

    }

    private List<Item> getItemsTokeep() {
        List<Item> itemsToKeep = new ArrayList<>();
        for (Player player : players) {
            for (var itemInPlayerEquipment : player.getEquipment()) {
                itemsToKeep.add(itemInPlayerEquipment);
            }
            itemsToKeep.add(player.getDefaultHelmet());
            itemsToKeep.add(player.getDefaultVest());
            itemsToKeep.add(player.getDefaultGloves());
            itemsToKeep.add(player.getDefaultBoots());
        }
        return itemsToKeep;
    }

    private boolean isNotItemToKeep(InteractiveEntity entity, List<Item> itemsTokeep) {
        return !(entity instanceof Item item && itemsTokeep.contains(item));
    }

    private boolean isNotPlayer(InteractiveEntity entity) {
        return !(entity instanceof Player);
    }

    public void insertIntoCollisionGrid(Collidable c) {
        grid.insert(c);
    }

    public Item registerItemAndNotifyTCP(ItemTemplates.ItemTemplate template, boolean droppable, Filter<HostedConnection> notificationFilter) {
        Item i = registerItemLocal(template, droppable);
        sendMessageTCP(i.createNewEntityMessage(), notificationFilter);
        return i;
    }

    public Item registerItemLocal(ItemTemplates.ItemTemplate template, boolean droppable) {
        ItemFactory ifa = new ItemFactory();
        Item item = ifa.createItem(currentMaxId++, template, droppable);
        return registerEntityLocal(item);
    }

    private <T extends InteractiveEntity> T registerEntityLocal(T entity) {
        mobs.put(entity.getId(), entity);
        return entity;
    }

    private void sendMessageTCP(AbstractMessage imsg, Filter<HostedConnection> filter) {
        imsg.setReliable(true);
        if (filter == null) {
            server.broadcast(imsg);
        } else {
            server.broadcast(filter, imsg);
        }
    }
}
