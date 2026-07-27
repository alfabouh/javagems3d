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

package api.scripting.coding.env.internal.game.init.events.rendering.ogl;

import api.scripting.coding.env.def.*;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;

@JSCodingClass(binding = "JSRenderNode", description = "Wrapper for render nodes used by the OpenGL renderer.")
public class JSRenderNode {
    @JSHideFromDoc
    private final String nodeIdName;

    @JSHideFromDoc
    private final int nodeIdValue;

    @JSCodingConstructor(description = "Internal constructor for JSRenderNode", paramNames = {"name", "value"})
    public JSRenderNode(String name, int value) {
        this.nodeIdName = name;
        this.nodeIdValue = value;
    }

    @JSHideFromDoc
    public JSRenderNode(JSRenderNode node) {
        this(node.getName(), node.getValue());
    }

    @JSHideFromDoc
    public static JSRenderNode get(NodeID name) {
        if (name.equals(JGemsOpenGLRenderer.DEFERRED_RENDER_PASS)) {
            return JSRenderNode.DEFERRED_RENDER_PASS;
        }
        if (name.equals(JGemsOpenGLRenderer.FORWARD_RENDER_PASS)) {
            return JSRenderNode.FORWARD_RENDER_PASS;
        }
        if (name.equals(JGemsOpenGLRenderer.TRANSPARENCY_RENDER_PASS)) {
            return JSRenderNode.TRANSPARENCY_RENDER_PASS;
        }
        if (name.equals(JGemsOpenGLRenderer.UI_RENDER_PASS)) {
            return JSRenderNode.UI_RENDER_PASS;
        }
        if (name.equals(JGemsOpenGLRenderer.GLUING_RENDER_PASS)) {
            return JSRenderNode.GLUING_RENDER_PASS;
        }
        if (name.equals(JGemsOpenGLRenderer.POST_EFFECTS_RENDER_PASS)) {
            return JSRenderNode.POST_EFFECTS_RENDER_PASS;
        }
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Get node name")
    public String getName() {
        return this.nodeIdName;
    }

    @JSCodingFunctionOrMethod(description = "Get node numeric ID")
    public int getValue() {
        return this.nodeIdValue;
    }

    @JSCodingField(description = "Deferred render pass node")
    public static final JSRenderNode DEFERRED_RENDER_PASS = new JSRenderNode("d-pass", 0);

    @JSCodingField(description = "Forward render pass node")
    public static final JSRenderNode FORWARD_RENDER_PASS = new JSRenderNode("f-pass", 1);

    @JSCodingField(description = "Transparency render pass node")
    public static final JSRenderNode TRANSPARENCY_RENDER_PASS = new JSRenderNode("transparency-pass", 2);

    @JSCodingField(description = "UI render pass node")
    public static final JSRenderNode UI_RENDER_PASS = new JSRenderNode("ui-pass", 3);

    @JSCodingField(description = "Gluing render pass node")
    public static final JSRenderNode GLUING_RENDER_PASS = new JSRenderNode("gluing-pass", 4);

    @JSCodingField(description = "Post effects render pass node")
    public static final JSRenderNode POST_EFFECTS_RENDER_PASS = new JSRenderNode("post-fx-pass", 5);
}