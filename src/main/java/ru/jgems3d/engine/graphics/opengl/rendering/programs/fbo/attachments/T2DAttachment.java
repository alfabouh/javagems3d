/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.attachments;

public final class T2DAttachment {
    private final int attachment;
    private final int textureFormat;
    private final int internalFormat;

    private T2DAttachment(int attachment, int textureFormat, int internalFormat) {
        this.attachment = attachment;
        this.textureFormat = textureFormat;
        this.internalFormat = internalFormat;
    }

    public static T2DAttachment create(int attachment, int textureFormat, int internalFormat) {
        return new T2DAttachment(attachment, textureFormat, internalFormat);
    }

    public int getAttachment() {
        return this.attachment;
    }

    public int getTextureFormat() {
        return this.textureFormat;
    }

    public int getInternalFormat() {
        return this.internalFormat;
    }
}
