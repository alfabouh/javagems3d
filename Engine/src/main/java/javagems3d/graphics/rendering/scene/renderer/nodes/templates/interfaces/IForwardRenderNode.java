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

package javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces;

import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IForwardRenderNode extends IRenderNode {
    FBOTexture2DProgram getInColorBuffer();
    FBOTexture2DProgram getOutColorBuffer();

    void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects);
    Collection<SceneObject> getForwardRenderingObjects();

    IForwardRenderNode setFilteredParticlesToRender(Collection<ParticleFX> filteredParticlesToRender);
    Collection<SceneObject> getRejectedDirectForwardRenderingObjects();
}
