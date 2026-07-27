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

package api.scripting.coding.env.internal.util.world.render.fbo;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.misc.JSPair;
import api.scripting.coding.env.internal.util.misc.JSRequiresClearResources;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.List;

@JSCodingClass(binding = "JSFBOTexture2D", description = "Wrapper for FBOTexture2DProgram, handling framebuffer, textures, and render attachments.")
public class JSFrameBuffer2D implements JSRequiresClearResources {
    @JSHideFromDoc
    private final FBOTexture2DProgram fbo;

    @JSCodingConstructor(description = "Wrap existing FBOTexture2DProgram", paramNames = {"fbo"})
    public JSFrameBuffer2D(FBOTexture2DProgram fbo) {
        this.fbo = fbo;
    }

    @JSCodingConstructor(description = "Create new FBOTexture2DProgram", paramNames = {"drawColor", "bindlessTextureHandler"})
    public JSFrameBuffer2D(boolean drawColor, boolean bindlessTextureHandler) {
        this.fbo = new FBOTexture2DProgram(drawColor, bindlessTextureHandler);
    }

    @JSCodingFunctionOrMethod(description = "Create a multisampled FBO texture", paramNames = {"size", "attachments", "internalFormat", "msaa"})
    public void createFrameBuffer2DTextureMSAA(JSVector2f size, int[] attachments, int internalFormat, int msaa) {
        fbo.createFrameBuffer2DTextureMSAA(new Vector2i((int) size.x(), (int) size.y()), attachments, internalFormat, msaa);
    }

    @JSCodingFunctionOrMethod(description = "Create standard FBO texture", paramNames = {"size", "attachmentContainer", "depthBuffer", "filtering", "compareMode", "compareFunc", "clamp", "borderColor"})
    public void createFrameBuffer2DTexture(JSVector2f size, JST2DAttachmentContainer attachmentContainer, boolean depthBuffer, int filtering, int compareMode, int compareFunc, int clamp, float[] borderColor) {
        fbo.createFrameBuffer2DTexture(new Vector2i((int) size.x(), (int) size.y()), attachmentContainer.getJavaContainer(), depthBuffer, filtering, compareMode, compareFunc, clamp, borderColor);
    }

    @JSCodingFunctionOrMethod(description = "Bind the framebuffer for rendering")
    public void bindFBO() {
        fbo.bindFBO();
    }

    @JSCodingFunctionOrMethod(description = "Unbind the framebuffer")
    public void unBindFBO() {
        fbo.unBindFBO();
    }

    @JSCodingFunctionOrMethod(description = "Get framebuffer ID")
    public int getFrameBufferId() {
        return fbo.getFrameBufferId();
    }

    @JSCodingFunctionOrMethod(description = "Get renderbuffer ID")
    public int getRenderBufferId() {
        return fbo.getRenderBufferId();
    }

    @JSCodingFunctionOrMethod(description = "Check if FBO is valid")
    public boolean isValid() {
        return fbo.isValid();
    }

    @JSCodingFunctionOrMethod(description = "Clear and delete the FBO")
    public void clearFBO() {
        fbo.clearFBO();
    }

    @JSCodingFunctionOrMethod(description = "Get texture program by index", paramNames = {"index"})
    public JSTexture2D getTextureByIndex(int index) {
        return new JSTexture2D(fbo.getTextureByIndex(index));
    }

    @JSCodingFunctionOrMethod(description = "Bind texture by index", paramNames = {"index"})
    public void bindTexture(int index) {
        fbo.bindTexture(index);
    }

    @JSCodingFunctionOrMethod(description = "Unbind currently bound texture")
    public void unBindTexture() {
        fbo.unBindTexture();
    }

    //TODO
   //@SuppressWarnings("all")
   //@JSCodingFunctionOrMethod(description = "Copy color buffers from this FBO to another FBO", paramNames = {"fboTo", "colorAttachments", "dimension"})
   //public void copyFBOtoFBOColor(int fboTo, JSPair<Integer, Integer>[] colorAttachments, JSVector2f dimension) {
   //    fbo.copyFBOtoFBOColor(fboTo, (Pair<Integer, Integer>[]) Arrays.stream(colorAttachments).map(JSPair::getJavaPair).toArray(), new Vector2i((int) dimension.x(), (int) dimension.y()));
   //}

    @JSCodingFunctionOrMethod(description = "Copy depth buffer from this FBO to another FBO", paramNames = {"fboTo", "dimension"})
    public void copyFBOtoFBODepth(int fboTo, JSVector2f dimension) {
        fbo.copyFBOtoFBODepth(fboTo, new Vector2i((int) dimension.x(), (int) dimension.y()));
    }

    @JSCodingFunctionOrMethod(description = "Connect a texture to framebuffer attachment", paramNames = {"attachment", "textureIndex"})
    public void connectTextureToBuffer(int attachment, int textureIndex) {
        fbo.connectTextureToBuffer(attachment, textureIndex);
    }

    @JSCodingFunctionOrMethod(description = "Get all texture programs attached to this FBO")
    public List<JSTexture2D> getTexturePrograms() {
        return fbo.getTexturePrograms().stream().map(JSTexture2D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java FBOTexture2DProgram")
    public FBOTexture2DProgram getJavaFBO() {
        return this.fbo;
    }

    @Override
    public void clear() {
        this.clearFBO();
    }
}