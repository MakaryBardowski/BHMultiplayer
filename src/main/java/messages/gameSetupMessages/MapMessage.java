package messages.gameSetupMessages;

import client.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.map.blocks.Map;
import messages.TwoWayMessage;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import server.ServerMain;

import java.io.UnsupportedEncodingException;

@Serializable
public class MapMessage extends TwoWayMessage {
    private static final LZ4Factory LZ4_FACTORY = LZ4Factory.fastestJavaInstance();
    private byte[] compressedLogicMap;
    private int decompressedLength;
    private int mapSizeX;
    private int mapSizeY;
    private int mapSizeZ;
    private int blockSize;

    public MapMessage(){}

    public MapMessage(Map map) throws UnsupportedEncodingException {
        byte[] uncompressedLogicMap = map.getLogicMap();
        this.decompressedLength = uncompressedLogicMap.length;
        this.mapSizeX = map.getMapSizeX();
        this.mapSizeY = map.getMapSizeY();
        this.mapSizeZ = map.getMapSizeZ();
        this.blockSize = map.getBlockSize();
        this.compressedLogicMap = compressLogicMap(uncompressedLogicMap);
        this.setReliable(true);
    }

    public byte[] getDecompressedLogicMap(){
        return decompressLogicMap();
    }

    public Map getMap(){
        byte[] decompressedPayload = getDecompressedLogicMap();
        return new Map(decompressedPayload,mapSizeX,mapSizeY,mapSizeZ,blockSize);
    }


    @Override
    public void handleServer(ServerMain server, HostedConnection sender) {}

    @Override
    public void handleClient(ClientGameAppState client) {
        System.out.println("RECEIVED A MAP");
        client.getCurrentGamemode().getLevelManager().setNextStaticMap(getMap());
    }

    private byte[] compressLogicMap(byte[] uncompressedLogicMap){
        LZ4Compressor compressor = LZ4_FACTORY.fastCompressor();

        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressedLogicMapWithPadding = new byte[maxCompressedLength];
        int actualCompressedLength = compressor.compress(uncompressedLogicMap, 0, decompressedLength, compressedLogicMapWithPadding, 0, maxCompressedLength);

        byte[] compressedLogicMap = new byte[actualCompressedLength];
        System.arraycopy(compressedLogicMapWithPadding, 0, compressedLogicMap, 0, actualCompressedLength);

        return compressedLogicMap;
    }


    private byte[] decompressLogicMap(){
        LZ4FastDecompressor decompressor = LZ4_FACTORY.fastDecompressor();
        byte[] decompressedLogicMap = new byte[decompressedLength];
        decompressor.decompress(compressedLogicMap, 0, decompressedLogicMap, 0, decompressedLength);
        return decompressedLogicMap;
    }

}
