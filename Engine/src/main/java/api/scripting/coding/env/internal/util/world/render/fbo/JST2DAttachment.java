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
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachment;

@JSCodingClass(binding = "JST2DAttachment", description = "Wrapper for T2DAttachment, represents a single framebuffer attachment with format information.")
public class JST2DAttachment {
    @JSHideFromDoc
    private final T2DAttachment attachment;

    @JSCodingConstructor(description = "Create T2DAttachment", paramNames = {"attachment", "textureFormat", "internalFormat"})
    public JST2DAttachment(int attachment, int textureFormat, int internalFormat) {
        this.attachment = T2DAttachment.create(attachment, textureFormat, internalFormat);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java T2DAttachment")
    public T2DAttachment getJavaAttachment() {
        return this.attachment;
    }

    @JSCodingFunctionOrMethod(description = "Get attachment slot")
    public int getAttachment() {
        return attachment.getAttachment();
    }

    @JSCodingFunctionOrMethod(description = "Get texture format")
    public int getTextureFormat() {
        return attachment.getTextureFormat();
    }

    @JSCodingFunctionOrMethod(description = "Get internal format")
    public int getInternalFormat() {
        return attachment.getInternalFormat();
    }
}