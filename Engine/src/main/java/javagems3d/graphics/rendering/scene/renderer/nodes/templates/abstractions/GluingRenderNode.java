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

package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IGluingRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.GluingRenderProcessor;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIDefaultButton;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;

public abstract class GluingRenderNode extends IRenderNode.Template implements IGluingRenderNode {
    private GluingRenderProcessor gluingRenderProcessor;
    private final FBOTexture2DProgram inColorScene;
    private final FBOTexture2DProgram inColorTransparency;

    public GluingRenderNode(@NotNull FBOTexture2DProgram inColorTransparency, @NotNull FBOTexture2DProgram inColorScene, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.inColorScene = inColorScene;
        this.inColorTransparency = inColorTransparency;
    }

    @Override
    public FBOTexture2DProgram getInColorTransparencyBuffer() {
        return this.inColorTransparency;
    }

    @Override
    public FBOTexture2DProgram getInColorSceneBuffer() {
        return this.inColorScene;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return this.getInColorSceneBuffer();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        this.getOutColorBuffer().bindFBO();
        if (!EventLauncher.pushEvent(new EventBus.GlueRenderFBOsOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.PRE), null).isCancelled()) {
            this.getSceneGluingRenderProcessor().runProcessorRendering(frameTicking);
            EventLauncher.pushEvent(new EventBus.GlueRenderFBOsOGLRenderInMainFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.POST), null);
        }
        this.getOutColorBuffer().unBindFBO();
    }

    public abstract @NotNull JGemsShaderManager getGluingShader();

    @Override
    public void createResources() {
        this.gluingRenderProcessor = new GluingRenderProcessor(this.getInColorTransparencyBuffer(), this.getOutColorBuffer(), this.getOpenGLRenderer(), this.getGluingShader());
        this.getSceneGluingRenderProcessor().createResources();
    }

    @Override
    public void destroyResources() {
        this.getSceneGluingRenderProcessor().destroyResources();
    }

    public GluingRenderProcessor getSceneGluingRenderProcessor() {
        return this.gluingRenderProcessor;
    }

    @Override
    public NodeID getNodeID() {
        return JGemsOpenGLRenderer.GLUING_RENDER_PASS;
    }
}
