/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package debugging;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import behaviorTree.context.SimpleHumanMobContext;
import client.ClientGameAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.entities.mobs.HumanMob;
import java.util.ArrayList;
import java.util.List;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class HumanPathDebugControl extends AbstractControl {

    private final List<Node> waypointNodes = new ArrayList<>();
    private final List<Geometry> waypointArrows = new ArrayList<>();
    private final HumanMob human;
    private List<Vector3f> currentDrawnPath;

    public HumanPathDebugControl(HumanMob humanMob) {
        human = humanMob;
    }

    @Override
    protected void controlUpdate(float tpf) {
        try {
            var serverSideHumanMob = (HumanMob) ServerMain.getInstance().getLevelManagerMobs().get(human.getId());
            if (serverSideHumanMob == null) {
                human.getNode().removeControl(this);
            }
            if (waypointNodes.isEmpty() && waypointArrows.isEmpty()) {
                showHumanPathDebug(serverSideHumanMob);
            }
            var ctx = ((SimpleHumanMobContext) serverSideHumanMob.getBehaviorTree().getContext());
            if (ctx.getCurrentPath() == null || ctx.getCurrentPath().isEmpty() || ctx.getCurrentPath() != currentDrawnPath) {
                clearWaypointNodesAndArrows();
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);

    }

    public void showHumanPathDebug(HumanMob human) {
        clearWaypointNodesAndArrows();
        if (human == null) {
            return;
        }
        var behaviorTree = human.getBehaviorTree();
        System.err.println(human + " behaviorTree = " + behaviorTree);
        if (behaviorTree == null) {
            return;
        }

        var context = behaviorTree.getContext();
        if (context == null) {
            return;
        }

        var humanContext = (SimpleHumanMobContext) context;
        var path = humanContext.getCurrentPath();

        if (path == null) {
            return;
        }

        if (path.isEmpty()) {
            return;
        }
        var cs = ClientGameAppState.getInstance();
        var debugNode = cs.getDebugNode();
        for (int i = 0; i < path.size(); i++) {
            var position = path.get(i).mult(cs.getBLOCK_SIZE());
            var waypointNode = DebugUtils.createUnshadedBoxNode();
            waypointNode.setLocalScale(1f, 1f, 1f);
            debugNode.attachChild(waypointNode);
            waypointNode.setLocalTranslation(position);

            if (i + 1 < path.size()) {
                var nextPosition = path.get(i + 1).mult(cs.getBLOCK_SIZE());
                var arrow = DebugUtils.addArrow(debugNode, position, nextPosition, ColorRGBA.Blue);
                waypointArrows.add(arrow);
            }

            waypointNodes.add(waypointNode);
        }
        currentDrawnPath = path;
    }

    private void clearWaypointNodesAndArrows() {
        for (var waypointNode : waypointNodes) {
            waypointNode.removeFromParent();
        }
        waypointNodes.clear();

        for (var waypointArrow : waypointArrows) {
            waypointArrow.removeFromParent();
        }
        waypointArrows.clear();
    }
}
