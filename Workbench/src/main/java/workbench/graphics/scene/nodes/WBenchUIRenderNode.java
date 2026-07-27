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

package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.scene.nodes.templates.IUIRenderNode;

public final class WBenchUIRenderNode implements IUIRenderNode {
    private final OpenGLRenderer openGLRenderer;
    private final DearUIRenderer dearUIRenderer;
    private DearUIInterface anInterface;

    public WBenchUIRenderNode(DearUIRenderer dearUIRenderer, OpenGLRenderer openGLRenderer) {
        this.openGLRenderer = openGLRenderer;
        this.dearUIRenderer = dearUIRenderer;
        this.anInterface = null;
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        MouseKeyboardController mouseKeyboardController = WBench.get().getControllerDispatcher().getCurrentController();
        this.dearUIRenderer.onRender(mouseKeyboardController, this.getAnInterface(), frameTicking);
    }

    public void setAnInterface(DearUIInterface anInterface) {
        this.anInterface = anInterface;
    }

    public DearUIInterface getAnInterface() {
        return this.anInterface;
    }

    @Override
    public @NotNull OpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    @Override
    public NodeID getNodeID() {
        return JGemsOpenGLRenderer.UI_RENDER_PASS;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }
}
