package ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.attachments;

import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;

import java.util.HashSet;
import java.util.Set;

public class T2DAttachmentContainer {
    private final Set<T2DAttachment> t2DAttachmentSet;

    public T2DAttachmentContainer(T2DAttachment attachment) {
        this();
        this.add(attachment);
    }

    public T2DAttachmentContainer(int attachment, int textureFormat, int internalFormat) {
        this();
        this.add(attachment, textureFormat, internalFormat);
    }

    public T2DAttachmentContainer() {
        this.t2DAttachmentSet = new HashSet<>();
    }

    public void add(int attachment, int textureFormat, int internalFormat) {
        this.getT2DAttachmentSet().add(T2DAttachment.create(attachment, textureFormat, internalFormat));
    }

    public void add(T2DAttachment attachment) {
        this.getT2DAttachmentSet().add(attachment);
    }

    public Set<T2DAttachment> getT2DAttachmentSet() {
        return this.t2DAttachmentSet;
    }
}
