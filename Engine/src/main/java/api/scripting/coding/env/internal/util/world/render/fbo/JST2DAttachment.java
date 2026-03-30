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