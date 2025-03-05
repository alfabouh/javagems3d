package javagems3d.graphics.rendering.programs.textures.base;

import org.lwjgl.opengl.ARBBindlessTexture;

public interface ITextureBindless {
    long getBindingHandler();

    default boolean isHandlerExists() {
        return this.getBindingHandler() != 0;
    }

    default long createBindlessHandler(int textureId) {
        return ARBBindlessTexture.glGetTextureHandleARB(textureId);
    }

    default long createBindlessHandler(int textureId, int samplerId) {
        return ARBBindlessTexture.glGetTextureSamplerHandleARB(textureId, samplerId);
    }

    default void createARB64Handling() {
        ARBBindlessTexture.glMakeTextureHandleResidentARB(this.getBindingHandler());
    }

    default void removeARB64Handling() {
        ARBBindlessTexture.glMakeTextureHandleNonResidentARB(this.getBindingHandler());
    }
}
