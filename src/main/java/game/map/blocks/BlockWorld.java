/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.blocks;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import java.util.HashMap;
import java.util.Random;
import jme3tools.optimize.TextureAtlas;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class BlockWorld {

    @Getter
    private final int mapSizeX;
    @Getter
    private final int mapSizeY;
    @Getter
    private final int mapSizeZ;

    private final int BLOCK_SIZE;
    private final int CHUNK_SIZE;

    private Block[][][] map;  // contains block data - generated based on logic map
    private final Map logicMap; // contains info if a block is present (!=0) needed for culling

    private TextureAtlas textureAtlas;
    private final AssetManager asm;

    private HashMap<String, Chunk> chunks;
    private Node worldNode;



    public BlockWorld(int BLOCK_SIZE, int CHUNK_SIZE, int mapSizeX,int mapSizeY,int mapSizeZ, Map logicMap, AssetManager asm, Node rootNode) {
        this.BLOCK_SIZE = BLOCK_SIZE;
        this.CHUNK_SIZE = CHUNK_SIZE;
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.mapSizeZ =  mapSizeZ;

        this.asm = asm;
        map = new Block[this.mapSizeX][this.mapSizeY][this.mapSizeZ];
        this.logicMap = logicMap;

        chunks = createChunks();

        worldNode = new Node("Block World Node");
        textureAtlas = new TextureAtlas(32 * 16, 32 * 16);
        textureAtlas.addTexture(asm.loadTexture(BlockType.STONE.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.DIRT_STONES.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.DIRT.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.WATER.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.OFFICE_BOTTOM.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.OFFICE_TOP.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.OFFICE_FLOOR.textureName), "DiffuseMap");

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

        if (logicMap.isPositionNotEmpty(x,y,z)) {
            throw new IllegalArgumentException("Block [" + x + "][" + y + "][" + z + "] already exists!");
        }

        logicMap.setBlockIdAtPosition(x,y,z,(byte)1);
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
//       Block   b = VoxelAdding.addBox((x * BLOCK_SIZE)-c.getChunkPos().getX()*BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE)-c.getChunkPos().getY()*BLOCK_SIZE, BLOCK_SIZE,  c.getBlocksCount(),  asm,  bt);
        Block b = VoxelAdding.AddOptimizedBox(c, map, logicMap, x, y, z, BLOCK_SIZE, asm, bt, (x * BLOCK_SIZE) - c.getChunkPos().getX() * BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE) - c.getChunkPos().getY() * BLOCK_SIZE);
        map[x][y][z] = b;
        c.attachBlock(b, asm.loadTexture(bt.textureName));
        return b;
    }

    public Block removeBlock(int x, int y, int z) {
        logicMap.setBlockIdAtPosition(x,y,z,(byte)0);
        Block b = map[x][y][z];
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
        c.detachBlock(b);
        return b;
    }

    public Block addBlockDataToChunk(int x, int y, int z, BlockType bt) { // o
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
//       Block   b = VoxelAdding.addBox((x * BLOCK_SIZE)-c.getChunkPos().getX()*BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE)-c.getChunkPos().getY()*BLOCK_SIZE, BLOCK_SIZE,  c.getBlocksCount(),  asm,  bt);
        Block b = VoxelAdding.AddOptimizedBox(
                c,
                map,
                logicMap,
                x,
                y,
                z,
                BLOCK_SIZE,
                asm,
                bt,
                (x * BLOCK_SIZE) - c.getChunkPos().getX() * BLOCK_SIZE,
                y * BLOCK_SIZE,
                (z * BLOCK_SIZE) - c.getChunkPos().getY() * BLOCK_SIZE
        );

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

    public Map getLogicMap() {
        return logicMap;
    }

    private void initializeBlocks() {
        // do not change below code - it performantly creates blockData in places where logicMap != 0
        // because it writes to chunk and batches it only once
        Random r = new Random();

        for (int index = 0; index < logicMap.getLength(); index++) {
                    int x = logicMap.getXFromIndex(index);
                    int y = logicMap.getYFromIndex(index);
                    int z = logicMap.getZFromIndex(index);
            System.out.println("X Y Z "+x + " "+ y + " "+z);
                    if (logicMap.getBlockIdAtIndex(index) == 1 || logicMap.getBlockIdAtIndex(index) == 9) {
                        addBlockDataToChunk(x, y, z, BlockType.STONE);
                    } else if (logicMap.getBlockIdAtIndex(index) == 2) {
                        var bt = BlockType.DIRT;
                        if (r.nextInt(5) == 1) {
                            bt = BlockType.DIRT_STONES;
                        }

                        addBlockDataToChunk(x, y, z, bt);

                    } else if (logicMap.getBlockIdAtIndex(index) == 3){
                        addBlockDataToChunk(x, y, z, BlockType.OFFICE_BOTTOM);
                    }else if (logicMap.getBlockIdAtIndex(index) == 4){
                        addBlockDataToChunk(x, y, z, BlockType.OFFICE_TOP);
                    }else if (logicMap.getBlockIdAtIndex(index) == 5){
                        addBlockDataToChunk(x, y, z, BlockType.OFFICE_FLOOR);
                    }

        }

        for (Chunk c : chunks.values()) {
            c.attach();
        }
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
}
