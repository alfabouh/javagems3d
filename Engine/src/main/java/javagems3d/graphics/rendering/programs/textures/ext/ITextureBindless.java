package javagems3d.graphics.rendering.programs.textures.ext;

import org.lwjgl.opengl.ARBBindlessTexture;

public interface ITextureBindless {
    long getBindingHandler();

    default long createBindingHandler(int textureId) {
        return ARBBindlessTexture.glGetTextureHandleARB(textureId);
    }

    default long createBindingHandler(int textureId, int samplerId) {
        return ARBBindlessTexture.glGetTextureSamplerHandleARB(textureId, samplerId);
    }

    default void createARB64Handling() {
        ARBBindlessTexture.glMakeTextureHandleResidentARB(this.getBindingHandler());
    }

    default void removeARB64Handling() {
        ARBBindlessTexture.glMakeTextureHandleNonResidentARB(this.getBindingHandler());
    }
}
