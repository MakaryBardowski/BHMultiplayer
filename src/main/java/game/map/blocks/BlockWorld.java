/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.blocks;

import client.ClientGameAppState;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.DecorationTemplates;
import game.entities.LevelExit;
import static game.map.blocks.VoxelLighting.setupModelLight;
import java.util.HashMap;
import java.util.Random;
import jme3tools.optimize.TextureAtlas;
import lombok.Getter;
import static game.entities.DestructibleUtils.setupModelShootability;

/**
 *
 * @author 48793
 */
public class BlockWorld {

    @Getter
    private final int MAP_HEIGHT = 20;
    @Getter
    private final int MAP_SIZE;

    private final int BLOCK_SIZE;
    private final int CHUNK_SIZE;

    private Block[][][] map;  // contains block data - generated based on logic map
    private final byte[][][] logicMap; // contains info if a block is present (!=0) needed for culling

    private TextureAtlas textureAtlas;
    private final AssetManager asm;

    private HashMap<String, Chunk> chunks;
    private Node worldNode;

    private int temp1;
    private int temp2;
    private int temp3;

    public BlockWorld(int BLOCK_SIZE, int CHUNK_SIZE, int MAP_SIZE, byte[][][] logicMap, AssetManager asm, Node rootNode) {
        this.BLOCK_SIZE = BLOCK_SIZE;
        this.CHUNK_SIZE = CHUNK_SIZE;
        this.MAP_SIZE = MAP_SIZE;
        this.asm = asm;
        map = new Block[MAP_SIZE][MAP_HEIGHT][MAP_SIZE];
        this.logicMap = logicMap;

        chunks = createChunks();

        worldNode = new Node("Block World Node");
        textureAtlas = new TextureAtlas(128, 128);
        textureAtlas.addTexture(asm.loadTexture(BlockType.STONE.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.DIRT_STONES.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.DIRT.textureName), "DiffuseMap");

        initializeBlocks();
        rootNode.attachChild(worldNode);
    }

    private HashMap<String, Chunk> createChunks() {
        HashMap<String, Chunk> cs = new HashMap<>();
        for (int x = 0; x < map.length; x += CHUNK_SIZE) { // 4 == chunkSize
            for (int z = 0; z < map[x][0].length; z += CHUNK_SIZE) {

                String s = "" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE;

                Chunk chunk = new Chunk(this, (x / CHUNK_SIZE) * CHUNK_SIZE, (z / CHUNK_SIZE) * CHUNK_SIZE);

                cs.put(s, chunk);

            }
        }
        return cs;
    }

    private void deleteChunks() {
        chunks = null;
    }

    public Block placeBlock(int x, int y, int z, BlockType bt) {

        if (logicMap[x][y][z] != 0) {
            throw new IllegalArgumentException("Block [" + x + "][" + y + "][" + z + "] already exists!");
        }

        logicMap[x][y][z] = 1;
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
//       Block   b = VoxelAdding.addBox((x * BLOCK_SIZE)-c.getChunkPos().getX()*BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE)-c.getChunkPos().getY()*BLOCK_SIZE, BLOCK_SIZE,  c.getBlocksCount(),  asm,  bt);
        Block b = VoxelAdding.AddOptimizedBox(c, map, logicMap, x, y, z, BLOCK_SIZE, c.getBlocksCount(), asm, bt, (x * BLOCK_SIZE) - c.getChunkPos().getX() * BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE) - c.getChunkPos().getY() * BLOCK_SIZE);
        map[x][y][z] = b;
        c.attachBlock(b, asm.loadTexture(bt.textureName));
        return b;
    }

    public Block removeBlock(int x, int y, int z) {
        logicMap[x][y][z] = 0;
        Block b = map[x][y][z];
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
        c.detachBlock(b);
        return b;
    }

    public Block addBlockDataToChunk(int x, int y, int z, BlockType bt) { // o
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
//       Block   b = VoxelAdding.addBox((x * BLOCK_SIZE)-c.getChunkPos().getX()*BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE)-c.getChunkPos().getY()*BLOCK_SIZE, BLOCK_SIZE,  c.getBlocksCount(),  asm,  bt);
        Block b = VoxelAdding.AddOptimizedBox(c, map, logicMap, x, y, z, BLOCK_SIZE, c.getBlocksCount(), asm, bt, (x * BLOCK_SIZE) - c.getChunkPos().getX() * BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE) - c.getChunkPos().getY() * BLOCK_SIZE);
        map[x][y][z] = b;
        c.addBlockData(b, asm.loadTexture(bt.textureName));

        return b;
    }

///-----------------------------------------GETTERS, SETTERS AND INITS-----------------------------------------------------------
    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public void setTextureAtlas(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public AssetManager getAsm() {
        return asm;
    }

    public Node getWorldNode() {
        return worldNode;
    }

    public void setWorldNode(Node worldNode) {
        this.worldNode = worldNode;
    }

    public int getBLOCK_SIZE() {
        return BLOCK_SIZE;
    }

    public Block[][][] getMap() {
        return map;
    }

    public void setMap(Block[][][] map) {
        this.map = map;
    }

    public HashMap<String, Chunk> getChunks() {
        return chunks;
    }

    public byte[][][] getLogicMap() {
        return logicMap;
    }

    private void initializeBlocks() {
        // do not change below code - it performantly creates blockData in places where logicMap != 0
        // because it writes to chunk and batches it only once
        Random r = new Random();

        for (int x = 0; x < logicMap.length; x++) {
            for (int y = 0; y < logicMap[x].length; y++) {
                for (int z = 0; z < logicMap[x][y].length; z++) {

                    if (logicMap[x][y][z] == 1 || logicMap[x][y][z] == 9) {
                        addBlockDataToChunk(x, y, z, BlockType.STONE);
                    } else if (logicMap[x][y][z] == 2) {
                        var bt = BlockType.DIRT;
                        if (r.nextInt(5) == 1) {
                            bt = BlockType.DIRT_STONES;
                        }

                        addBlockDataToChunk(x, y, z, bt);

                    }
                }
            }
        }

        for (Chunk c : chunks.values()) {
            c.generateGeometry(c.generateMesh());
        }
    }

    // from now on only use below functions instead of explicit array access
    public byte getBlockIndexByCellCoords(int x, int y, int z) {
        return logicMap[x][y][z];
    }

    public byte getBlockTypeByPosition(Vector3f position) {
        temp1 = (int) (Math.floor(position.x / BLOCK_SIZE));
        temp2 = (int) (Math.floor(position.y / BLOCK_SIZE));
        temp3 = (int) (Math.floor(position.z / BLOCK_SIZE));
        return logicMap[temp1][temp2][temp3];
    }

    private void clear() {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                for (int z = 0; z < map[0][0].length; z++) {
                    map[x][y][z] = null;
                }
            }
        }

        deleteChunks();
        worldNode.detachAllChildren();
    }

    public void updateAfterLogicMapChange() {
        double time = (double) System.currentTimeMillis();
        clear();
        chunks = createChunks();
        initializeBlocks();
        double time1 = (double) System.currentTimeMillis();
        double totalTime = (time1 - time) / 1000d;
        System.out.println("time for regeneration = " + totalTime + "s");
    }

//    public void spawnCarAtLocation() {
//        var template = DecorationTemplates.EXIT_CAR;
//        var car = (Node) Main.getInstance().getAssetManager().loadModel(template.getModelPath());
//        car.getChild(0).scale(template.getScale());
//        setupModelLight(car);
//        setupModelShootability(car, -1);
//        LevelExit exit = new LevelExit(-1, "Exit Car", car, template);
//        ClientGameAppState.getInstance().getPickableNode().attachChild(car);
//        ClientGameAppState.getInstance().registerEntity(exit);
//        car.move(BLOCK_SIZE * x + 0.5f * BLOCK_SIZE, BLOCK_SIZE * y, BLOCK_SIZE * z + 0.5f * BLOCK_SIZE);
//
//        ClientGameAppState.getInstance().getGrid().insert(exit);
//
//    }

}
