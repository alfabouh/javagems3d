/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
