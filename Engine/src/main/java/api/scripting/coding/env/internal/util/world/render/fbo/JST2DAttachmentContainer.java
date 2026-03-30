package api.scripting.coding.env.internal.util.world.render.fbo;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachment;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;

import java.util.List;

@JSCodingClass(binding = "JST2DAttachmentContainer", description = "Wrapper for T2DAttachmentContainer, holds multiple T2DAttachment objects.")
public class JST2DAttachmentContainer {
    @JSHideFromDoc
    private final T2DAttachmentContainer container;

    @JSCodingConstructor(description = "Create empty T2DAttachmentContainer")
    public JST2DAttachmentContainer() {
        this.container = new T2DAttachmentContainer();
    }

    @JSCodingConstructor(description = "Create T2DAttachmentContainer with a single attachment", paramNames = {"attachment"})
    public JST2DAttachmentContainer(JST2DAttachment attachment) {
        this.container = new T2DAttachmentContainer();
        this.container.add(attachment.getJavaAttachment());
    }

    @JSCodingConstructor(description = "Create T2DAttachmentContainer from an array of attachments", paramNames = {"attachments"})
    public JST2DAttachmentContainer(JST2DAttachment[] attachments) {
        this.container = new T2DAttachmentContainer();
        if (attachments != null) {
            for (JST2DAttachment att : attachments) {
                this.container.add(att.getJavaAttachment());
            }
        }
    }

    @JSCodingFunctionOrMethod(description = "Add a single attachment", paramNames = {"attachment"})
    public void add(JST2DAttachment attachment) {
        container.add(attachment.getJavaAttachment());
    }

    @JSCodingFunctionOrMethod(description = "Add multiple attachments", paramNames = {"attachments"})
    public void add(JST2DAttachment[] attachments) {
        if (attachments != null) {
            for (JST2DAttachment att : attachments) {
                container.add(att.getJavaAttachment());
            }
        }
    }

    @JSCodingFunctionOrMethod(description = "Get all attachments as array")
    public JST2DAttachment[] getAttachments() {
        List<T2DAttachment> list = container.getT2DAttachmentSet();
        JST2DAttachment[] result = new JST2DAttachment[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = new JST2DAttachment(list.get(i).getAttachment(), list.get(i).getTextureFormat(), list.get(i).getInternalFormat());
        }
        return result;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java T2DAttachmentContainer")
    public T2DAttachmentContainer getJavaContainer() {
        return container;
    }
}