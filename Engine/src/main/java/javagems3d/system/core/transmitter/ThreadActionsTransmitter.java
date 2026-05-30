package javagems3d.system.core.transmitter;

import javagems3d.system.core.transmitter.actions.Physics_Render__Action;
import javagems3d.system.core.transmitter.actions.Render_Physics__Action;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ThreadActionsTransmitter {
    public static final ThreadActionsTransmitter INSTANCE = new ThreadActionsTransmitter();
   // public static final Object monitor = new Object();

    private final Queue<Physics_Render__Action> actions__PHYSICS_TO_RENDER;
    private final Queue<Render_Physics__Action> actions__RENDER_TO_PHYSICS;

    private ThreadActionsTransmitter() {
        this.actions__PHYSICS_TO_RENDER = new ConcurrentLinkedQueue<>();
        this.actions__RENDER_TO_PHYSICS = new ConcurrentLinkedQueue<>();
    }

    public void clear() {
        this.actions__PHYSICS_TO_RENDER.clear();
        this.actions__RENDER_TO_PHYSICS.clear();
    }

    public void TRANSMIT_ACTION__RENDER_PHYS(@NotNull Render_Physics__Action action) {
        //synchronized (ThreadActionsTransmitter.monitor) {
            this.actions__RENDER_TO_PHYSICS.add(action);
        //}
    }

    public void TRANSMIT_ACTION__PHYS_RENDER(@NotNull Physics_Render__Action action) {
        //synchronized (ThreadActionsTransmitter.monitor) {
            this.actions__PHYSICS_TO_RENDER.add(action);
        //}
    }

    public Queue<Physics_Render__Action> getActions__PHYSICS_TO_RENDER() {
        //synchronized (ThreadActionsTransmitter.monitor) {
            return this.actions__PHYSICS_TO_RENDER;
        //}
    }

    public Queue<Render_Physics__Action> getActions__RENDER_TO_PHYSICS() {
        //synchronized (ThreadActionsTransmitter.monitor) {
            return this.actions__RENDER_TO_PHYSICS;
        //}
    }
}
