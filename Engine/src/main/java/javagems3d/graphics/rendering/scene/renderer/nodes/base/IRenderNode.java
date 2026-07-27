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

package javagems3d.graphics.rendering.scene.renderer.nodes.base;

import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public interface IRenderNode extends IWindow.ResizeEvent, IResourceInit {
    void onRender(FrameTicking frameTicking);
    @NotNull OpenGLRenderer getOpenGLRenderer();
    NodeID getNodeID();

    default Vector2i getRenderingResolution() {
        return this.getOpenGLRenderer().getRenderingResolution();
    }
    
    @Override
    default void onWindowResize(IWindow window) {
        this.recreateResources();
    }

    abstract class Template {
        private final OpenGLRenderer openGLRenderer;

        public Template(@NotNull OpenGLRenderer openGLRenderer) {
            this.openGLRenderer = openGLRenderer;
        }

        @NotNull
        public IRenderWorld getWorld() {
            return this.getOpenGLRenderer().getWorld();
        }

        @NotNull
        public OpenGLRenderer getOpenGLRenderer() {
            return this.openGLRenderer;
        }
    }
}
