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

package javagems3d.graphics.rendering.scene.renderer.processors;

import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.world.IRenderWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public interface IRenderProcessor extends IResourceInit {
    void runProcessorRendering(FrameTicking frameTicking);

    default Vector2i getRenderingResolution() {
        return this.getOpenGLRenderer().getRenderingResolution();
    }

    @NotNull OpenGLRenderer getOpenGLRenderer();

    abstract class Template implements IRenderProcessor {
        private final OpenGLRenderer openGLRenderer;

        public Template(OpenGLRenderer openGLRenderer) {
            this.openGLRenderer = openGLRenderer;
        }

        @NotNull
        public IRenderWorld getWorld() {
            return this.getOpenGLRenderer().getWorld();
        }

        @Override
        @NotNull
        public OpenGLRenderer getOpenGLRenderer() {
            return this.openGLRenderer;
        }
    }
}