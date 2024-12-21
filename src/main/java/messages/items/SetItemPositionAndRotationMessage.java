package messages.items;

import client.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;
import server.ServerMain;

@Serializable
public class SetItemPositionAndRotationMessage extends TwoWayMessage {
    public int id;
    public float x;
    public float y;
    public float z;

    public float qx;
    public float qy;
    public float qz;
    public float qw;

    public SetItemPositionAndRotationMessage(){}

    public SetItemPositionAndRotationMessage(Vector3f position, Quaternion rotation){
        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();

        this.qx = rotation.getX();
        this.qy = rotation.getY();
        this.qz = rotation.getZ();
        this.qw = rotation.getW();

    }


    @Override
    public void handleServer(ServerMain server, HostedConnection sender) {

    }

    @Override
    public void handleClient(ClientGameAppState client) {

    }

    public Vector3f getPostion(){
        return new Vector3f(x,y,z);
    }

    public Quaternion getRotation(){
        return new Quaternion(qx,qy,qz,qw);
    }
}
