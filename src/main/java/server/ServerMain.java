package server;

import client.ClientGameAppState;
import com.jme3.app.Application;
import game.entities.mobs.Mob;
import game.entities.mobFactories.PlayerFactory;
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
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.HumanMob;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.factories.ItemFactory;
import game.items.weapons.Rifle;
import game.map.collision.WorldGrid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import messages.items.ChestItemInteractionMessage;
import static messages.items.ChestItemInteractionMessage.ChestItemInteractionType.INSERT;
import messages.items.MobItemInteractionMessage;
import messages.items.MobItemInteractionMessage.ItemInteractionType;
import messages.items.SetDefaultItemMessage;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class ServerMain extends AbstractAppState implements ConnectionListener, MessageListener<HostedConnection> {
    
    @Getter
    private static ServerMain instance;
    
    private static final byte MAX_PLAYERS = 4;
    private static final String SERVER_IP = "localhost";
    private Server server;
    private final ConcurrentHashMap<Integer, InteractiveEntity> mobs = new ConcurrentHashMap<>();
    private float tickTimer;
    private final float TIME_PER_TICK = 0.033f;
    private int currentMaxId = 0;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private Node rootNode;
    
    @Getter
    private WorldGrid grid;
    
    @Getter
    private HashMap<Integer, HostedConnection> connectionsById = new HashMap<>(MAX_PLAYERS);
    
    public ServerMain(AssetManager assetManager, RenderManager renderManager) {
        this.assetManager = assetManager;
        this.renderManager = renderManager;
        this.rootNode = new Node("server rootNode");
        instance = this;
    }

//    public static void main(String[] args) {
////        ServerMain app = new ServerMain();
////        app.start(JmeContext.Type.Headless);
//    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        
        try {
            server = Network.createServer(NetworkingInitialization.PORT);
            server.addConnectionListener(this);
            server.addMessageListener(new ServerMessageListener(this));
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }


        grid = new WorldGrid(39, 4, 16);
        
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            registerRandomChestLocal(new Vector3f(r.nextInt(38 * 4) + 4, 4, r.nextInt(38 * 4) + 4));
            registerPlayer(null).setPositionServer(new Vector3f(r.nextInt(38 * 4) + 4, 4, r.nextInt(38 * 4) + 4));
            
        }

//        populateMap();
    }
    
    @Override
    public void update(float tpf) {
        tickTimer += tpf;
        
//        if(ClientGameAppState.getInstance() != null && ClientGameAppState.getInstance().getPlayer() != null){
//                    System.out.println("SERVER"+grid.getNearbyAfterMove(ClientGameAppState.getInstance().getPlayer()));
//
//        }
        
        if (tickTimer >= TIME_PER_TICK) {
            tickTimer = 0;
            mobs.entrySet().stream().forEach(i -> {
                //to be seriously optimized
                if (i.getValue() instanceof Destructible d) {
                    if (d instanceof Mob x) {
//                        System.out.println(x.getId() +" position "+x.getNode().getWorldTranslation());
                        server.broadcast(new MobPosUpdateMessage(x.getId(), x.getNode().getWorldTranslation()));
                        server.broadcast(new MobRotUpdateMessage(x.getId(), x.getNode().getLocalRotation()));
                    }
                }
            }
            );

//            System.err.println("server mobs " + mobs);
//            mobs.values().stream()
//                    .filter(x -> x instanceof HumanMob)
//                    .map(x -> (HumanMob) x)
//                    .forEach((x) -> {
//                        System.out.println("--------Player " + x.getId());
//                        System.out.println("equipped helmet =" + x.getHelmet() + " (default " + x.getDefaultHelmet());
//                        System.out.println("equipped vest =" + x.getVest() + " (default " + x.getDefaultVest());
//                        System.out.println("equipped gloves =" + x.getGloves() + " (default " + x.getDefaultGloves());
//                        System.out.println("equipped boots =" + x.getBoots() + " (default " + x.getDefaultBoots());
//
//                    });
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
        
        itemsInGame.forEach(item -> {
            AbstractMessage msg = item.createNewEntityMessage();
            msg.setReliable(true);
            server.broadcast(Filters.in(hc), msg);
        });
        
        mobsInGame.forEach(mob -> {
            AbstractMessage msg = mob.createNewEntityMessage();
            msg.setReliable(true);
            server.broadcast(Filters.in(hc), msg);
        });
        
        mobsInGame.forEach(mob -> {
            sendNewEntityEquipmentInfo(mob, Filters.in(hc));
        });
        
        chestsInGame.forEach(chest -> {
            AbstractMessage chestMsg = chest.createNewEntityMessage();
            chestMsg.setReliable(true);
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
    
    public Server getServer() {
        return server;
    }
    
    public ConcurrentHashMap<Integer, InteractiveEntity> getMobs() {
        return mobs;
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
    
    public Chest registerRandomChestLocal(Vector3f offset) {
        Chest chest = Chest.createRandomChestServer(currentMaxId++, rootNode, offset, assetManager);
        
        Random r = new Random();
        int randomValue = r.nextInt(6);
        if (randomValue < 2) {
            Vest playerVest = (Vest) registerItemLocal(ItemTemplates.VEST_TRENCH, true);
            playerVest.setArmorValue(0.45f);
            chest.addToEquipment(playerVest);
        }
        if (randomValue >= 1 && randomValue <= 4) {
            Boots playerBoots = (Boots) registerItemLocal(ItemTemplates.BOOTS_TRENCH, true);
            playerBoots.setArmorValue(0.25f);
            chest.addToEquipment(playerBoots);
        }
        
        insertIntoCollisionGrid(chest);
        
        return registerEntityLocal(chest);
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
    
    public Player registerPlayer(HostedConnection hc) {
        Helmet playerHead = (Helmet) registerItemAndNotifyTCP(ItemTemplates.HEAD_1, false, Filters.notIn(hc));
        Vest playerVest = (Vest) registerItemAndNotifyTCP(ItemTemplates.TORSO_1, false, Filters.notIn(hc));
        Gloves playerGloves = (Gloves) registerItemAndNotifyTCP(ItemTemplates.HAND_1, false, Filters.notIn(hc));
        Boots playerBoots = (Boots) registerItemAndNotifyTCP(ItemTemplates.LEG_1, false, Filters.notIn(hc));
        Rifle playerRifle = (Rifle) registerItemAndNotifyTCP(ItemTemplates.RIFLE_MANNLICHER_95, true, Filters.notIn(hc));
        
        Player player = new PlayerFactory(currentMaxId++, assetManager, rootNode, renderManager).createServerSide();
        
        player.addToEquipment(playerRifle);
        player.equipServer(playerRifle);
        
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
    
    public void populateMap() {
        registerRandomChestLocal(new Vector3f(17, 4, 5));
        registerRandomChestLocal(new Vector3f(17 + 2.3f * 0.8f, 4, 5));
        
    }
    
    public void insertIntoCollisionGrid(Collidable c) {
        grid.insert(c);
    }
    
}
