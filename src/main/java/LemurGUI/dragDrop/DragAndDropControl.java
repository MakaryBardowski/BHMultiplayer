package LemurGUI.dragDrop;

import LemurGUI.LemurPlayerInventoryGui;
import LemurGUI.components.DraggableButton;
import java.util.*;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.SafeArrayList;

import com.simsilica.lemur.event.*;

/**
 *
 *
 * @author Paul Speed
 */
public class DragAndDropControl extends AbstractControl {

    private final DndControlListener listener = new DndControlListener();

    private final SafeArrayList<DragAndDropListener> listeners = new SafeArrayList<>(DragAndDropListener.class);

    private DefaultDragSession currentSession;

    private static DefaultDragSession globalSession;

    private static boolean isCurrentlyDragging;

    private final LemurPlayerInventoryGui lemurPlayerEquipment;

    public DragAndDropControl(LemurPlayerInventoryGui lemurPlayerEquipment, DragAndDropListener... initialListeners) {
        this.lemurPlayerEquipment = lemurPlayerEquipment;
        listeners.addAll(Arrays.asList(initialListeners));
    }

    public void addDragAndDropListener(DragAndDropListener l) {
        listeners.add(l);
    }

    public void removeDragAndDropListener(DragAndDropListener l) {
        listeners.remove(l);
    }

    @Override
    public void setSpatial(Spatial s) {

        if (getSpatial() != null) {
            detach(getSpatial());
        }

        super.setSpatial(s);

        if (s != null) {
            attach(s);
        }
    }

    protected void detach(Spatial s) {
        CursorEventControl.removeListenersFromSpatial(s, listener);
    }

    protected void attach(Spatial s) {
        CursorEventControl.addListenersToSpatial(s, listener);
    }

    protected DefaultDragSession getSession(AbstractCursorEvent event) {
        return globalSession;
    }

    protected DefaultDragSession clearSession(AbstractCursorEvent event) {
        DefaultDragSession result = globalSession;
        globalSession = null;
        return result;
    }

    protected boolean dragStarted(CursorButtonEvent event, CollisionResult collision, Spatial target, Spatial capture) {
        isCurrentlyDragging = true;

        if (capture instanceof LemurGUI.components.Draggable d) {
            d.onDragStart(event, collision, target);
        }

        this.currentSession = new DefaultDragSession(target, new Vector2f(event.getX(), event.getY()));
        DragEvent dragEvent = new DragEvent(currentSession, event, collision);
        Draggable draggable = null;

        // Deliver to all listeners until one creates a draggable
        for (DragAndDropListener l : listeners.getArray()) {
            draggable = l.onDragDetected(dragEvent);
            if (draggable != null) {
                break;
            }
        }

        if (draggable == null) {
            currentSession = null;
            return false;
        }

        currentSession.setDraggable(draggable);
        globalSession = currentSession;

        // Need to cache the event based on location but we can do that during the
        // first real drag event... which is coming right after this.
        return true;
    }

    protected void dragging(CursorMotionEvent event, Spatial target, Spatial capture) {
        DefaultDragSession session = getSession(event);

        if (target != getSpatial()) {
            // nothing to deliver as we are getting an event for a different 
            // container somehow that doesn't match the above
            return;
        }

        // If this is the event for the original drag source
        if (target == capture && target == getSpatial()) {
            // Update the draggable's location
            session.getDraggable().setLocation(event.getX(), event.getY());

            // If we aren't currently over anything           
            if (event.getCollision() == null) {
                // Nothing more to do 
                return;
            }
        }

        if (target.getControl(DragAndDropControl.class) == null) {
            return;
        }

        DragEvent dragEvent = new DragEvent(session, event);
        if (event.getCollision() == null) {
            session.setDropTarget(null, dragEvent);
            session.setDropCollision(null);
        } else {
            session.setDropTarget(target, dragEvent);
            session.setDropCollision(dragEvent.getCollision());
            fireDragOver(dragEvent);
        }
    }

    protected void dragStopped(CursorButtonEvent event, CursorMotionEvent lastMotion, Spatial target, Spatial source) {
        isCurrentlyDragging = false;

        var dragIcon = lemurPlayerEquipment.getDragIcon();
        lemurPlayerEquipment.getDragItemIconContainer().removeChild(dragIcon);
        if (source instanceof DraggableButton dragged) {
            dragged.onDragStop(event, lastMotion, target, dragged);
        }

        DefaultDragSession session = clearSession(event);
        if (session == null) {
            // There was no active session... but then why did we get a stopped?
            return;
        }

        session.close(new DragEvent(session, lastMotion, session.getDropCollision()));
    }

    protected void dragExit(CursorMotionEvent event, Spatial target, Spatial capture) {
        DefaultDragSession session = getSession(event);
        if (session == null) {
            // There was no active session
            return;
        }

        // Double check that we should be clearing the drop target
        if (session.getDropTarget() == getSpatial()) {
            DragEvent dragEvent = new DragEvent(session, event);
            session.setDropTarget(null, dragEvent);
        }
    }

    protected void fireEnter(DragEvent event) {
        for (DragAndDropListener l : listeners.getArray()) {
            l.onDragEnter(event);
        }
    }

    protected void fireExit(DragEvent event) {
        for (DragAndDropListener l : listeners.getArray()) {
            l.onDragExit(event);
        }
    }

    protected void fireDragOver(DragEvent event) {
        for (DragAndDropListener l : listeners.getArray()) {
            l.onDragOver(event);
        }
    }

    protected void fireDrop(DragEvent event) {
        for (DragAndDropListener l : listeners.getArray()) {
            l.onDrop(event);
        }
    }

    protected void fireDone(DragEvent event) {
        for (DragAndDropListener l : listeners.getArray()) {
            l.onDragDone(event);
        }
    }

    public void enableDrag() {
        listener.enableDrag();
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private class DndControlListener implements CursorListener {

        private CursorMotionEvent lastEvent;
        private CursorButtonEvent downEvent = null;
        private Spatial downTarget;
        private Spatial downCapture;
        private boolean dragDisabled;

        protected boolean isDownEvent(CursorButtonEvent event, Spatial target, Spatial capture) {
            return event.isPressed();
        }

        protected boolean isDragging(CursorMotionEvent event, Spatial target, Spatial capture) {
            if (downEvent == null) {
                return false;
            }
            return !(event.getX() == downEvent.getX() && event.getY() == downEvent.getY());
        }

        @Override
        public void cursorButtonEvent(CursorButtonEvent event, Spatial target, Spatial capture) {

            if (isDownEvent(event, target, capture)) {
                downEvent = event;
                downTarget = target;
                downCapture = capture;
            } else if( downEvent != null){
                dragStopped(event, lastEvent, target, capture);
                downEvent = null;
                dragDisabled = false;
            }
        }

        @Override
        public void cursorEntered(CursorMotionEvent event, Spatial target, Spatial capture) {

        }

        @Override
        public void cursorExited(CursorMotionEvent event, Spatial target, Spatial capture) {
            dragExit(event, target, capture);
        }

        private void enableDrag() {
            dragDisabled = false;
            downEvent = null;
        }

        @Override
        public void cursorMoved(CursorMotionEvent event, Spatial target, Spatial capture) {
            if (!dragDisabled && isDragging(event, target, capture)) {
                if (dragStarted(downEvent, lastEvent != null ? lastEvent.getCollision() : null,
                        downTarget, downCapture)) {
                    dragging(event, target, capture);
                } else {
                    dragDisabled = true;
                }
            }

            lastEvent = event;
        }
    }

    public static boolean isDragging() {
        return isCurrentlyDragging;
    }

    public static void setDragging(boolean v) {
        isCurrentlyDragging = v;
    }

}
