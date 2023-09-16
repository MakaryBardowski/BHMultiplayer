package server;

import com.jme3.app.Application;
import game.entities.mobs.Mob;
import game.entities.mobFactories.PlayerFactory;
import game.entities.mobs.Player;
import messages.messageListeners.ServerMessageListener;
import messages.SystemHealthUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.NewMobMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;

import com.jme3.app.SimpleApplication;
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
import com.jme3.system.JmeContext;
import networkingUtils.NetworkingInitialization;
import com.jme3.network.Filters;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import game.entities.Chest;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.armor.Vest;
import game.items.factories.ItemFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.NewChestMessage;
import messages.items.ItemInteractionMessage;
import messages.items.ItemInteractionMessage.ItemInteractionType;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class ServerMain extends AbstractAppState implements ConnectionListener, MessageListener<HostedConnection> {

    private static final String SERVER_IP = "localhost";
    private Server server;
    private final ConcurrentHashMap<Integer, InteractiveEntity> mobs = new ConcurrentHashMap<>();
    private float tickTimer;
    private final float TIME_PER_TICK = 0.033f;
    private int currentMaxId = 0;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private Node rootNode;
    
    public ServerMain(AssetManager assetManager,RenderManager renderManager){
    this.assetManager = assetManager;
    this.renderManager = renderManager;
    this.rootNode = new Node("server rootNode");
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

        for (int row = 5; row < 8; row++) {
            for (int x = 5; x < 8; x++) {
                registerPlayer().getNode().move(10 + row * 2, 0, 10 + (x - 5) * 4);
            }
        }

        for (int i = 0; i < 10; i++) {
            registerRandomChest();
        }

    }

    @Override
    public void update(float tpf) {
        //glowna petla serwera, 30dw111w tickow (wiadomosci od serwera) na sekunde, co 0.033s kazda
        tickTimer += tpf;
        if (tickTimer >= TIME_PER_TICK) {
            tickTimer = 0;
            mobs.entrySet().stream().forEach(i -> {
                //to be seriously optimized
                if (i.getValue() instanceof Destructible d) {
//                    server.broadcast(new SystemHealthUpdateMessage(d.getId(), d.getHealth()));
                    if (d instanceof Mob x) {
                        server.broadcast(new MobPosUpdateMessage(x.getId(), x.getNode().getWorldTranslation()));
                        server.broadcast(new MobRotUpdateMessage(x.getId(), x.getNode().getLocalRotation()));
                    }
                }
            }
            );

        }

    }

    @Override
    public void connectionAdded(Server server, HostedConnection hc) {

        mobs.entrySet().forEach(x -> {
            if (x.getValue() instanceof Item item) {
                AbstractMessage msg = item.createNewEntityMessage();
                msg.setReliable(true);
                server.broadcast(Filters.in(hc), msg);
            } else if (x.getValue() instanceof Mob mob) {
                AbstractMessage msg = mob.createNewEntityMessage();
                msg.setReliable(true);
                server.broadcast(Filters.in(hc), msg);
                sendNewMobEquipmentInfo(mob, Filters.in(hc));
            } else if (x.getValue() instanceof Chest chest) {
                server.broadcast(Filters.in(hc), chest.createNewEntityMessage());
            }
        });

        Mob newPlayer = registerPlayer();
        SetPlayerMessage messageToNewPlayer = new SetPlayerMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        messageToNewPlayer.setReliable(true);
        server.broadcast(Filters.in(hc), messageToNewPlayer);

        // send info about new player eq
        PlayerJoinedMessage msg = new PlayerJoinedMessage(newPlayer.getId(), newPlayer.getNode().getWorldTranslation());
        msg.setReliable(true);
        server.broadcast(Filters.notEqualTo(hc), msg);

        for (Item i : newPlayer.getEquipment()) {
            if (i != null) {
                server.broadcast(i.createNewEntityMessage());
            }
        }
        sendNewMobEquipmentInfo(newPlayer, null);

    }

    @Override
    public void connectionRemoved(Server server, HostedConnection hc) {
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
    }

//    @Override
//    public void destroy() {
//        for (HostedConnection h : server.getConnections()) {
//            h.close("server shutdown");
//        }
//        server.close();
//        super.destroy();
//    }

    public Server getServer() {
        return server;
    }

    public ConcurrentHashMap<Integer, InteractiveEntity> getMobs() {
        return mobs;
    }

    public Player registerPlayer() {
        /*
        adding equipment to a mob goes as below:
        1. Item i = registerItem( template)
        2. Mob m = ...
        m. addItem( i )
        3. send interactionMessage ( i, m , ItemInteractionType.PICKUP/EQUIP )
         */
        Item playerVest = registerItem(ItemTemplates.VEST_TRENCH, true);
        Item playerHead = registerItem(ItemTemplates.HEAD_1,false);
        Item playerBoots = registerItem(ItemTemplates.BOOTS_TRENCH, true);
        Item playerRifle = registerItem(ItemTemplates.RIFLE_MANNLICHER_95,true);
        Item playerGloves = registerItem(ItemTemplates.GLOVES_TRENCH,false);
        Player player = new PlayerFactory(currentMaxId++, assetManager, rootNode, renderManager).createServerSide();
        player.addToEquipment(playerVest);
        player.addToEquipment(playerBoots);
        player.addToEquipment(playerRifle);
        player.addToEquipment(playerHead);
        player.addToEquipment(playerGloves);

//        System.out.print("adding player " + player.getId() + "\n Their Equipment: ");
//        System.out.println(Arrays.toString(player.getEquipment()));
        return registerEntity(player);
    }

    public Item registerItem(ItemTemplate template, boolean droppable) {
        ItemFactory ifa = new ItemFactory(null);
        Item item = ifa.createItem(currentMaxId++, template, droppable);
        return registerEntity(item);
    }

    public Chest registerRandomChest() {
        Random r = new Random();
        Vector3f offset = new Vector3f(r.nextFloat() * 30 * 4, 4, r.nextFloat() * 30 * 4);
        Chest chest = Chest.createRandomChestServer(currentMaxId++, rootNode, offset, assetManager);
        return registerEntity(chest);
    }

    private <T extends InteractiveEntity> T registerEntity(T entity) {
        this.mobs.put(entity.getId(), entity);
        return entity;
    }

    private void sendNewMobEquipmentInfo(Mob mob, Filter<HostedConnection> filter) {
        for (Item i : mob.getEquipment()) {
            if (i != null) {
                ItemInteractionMessage pmsg = new ItemInteractionMessage(i, mob, ItemInteractionType.PICK_UP);
                pmsg.setReliable(true);
                sendMessage(pmsg, filter);

                ItemInteractionMessage emsg = new ItemInteractionMessage(i, mob, ItemInteractionType.EQUIP);
                emsg.setReliable(true);
                sendMessage(emsg, filter);
            }
        }
    }

    private void sendMessage(AbstractMessage imsg, Filter<HostedConnection> filter) {
        imsg.setReliable(true);
        if (filter == null) {
            server.broadcast(imsg);
        } else {
            server.broadcast(filter, imsg);
        }
    }
}
