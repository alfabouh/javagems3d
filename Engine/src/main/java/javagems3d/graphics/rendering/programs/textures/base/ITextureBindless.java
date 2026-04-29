package javagems3d.graphics.rendering.programs.textures.base;

import org.lwjgl.opengl.ARBBindlessTexture;

public interface ITextureBindless {
    long getBindingHandler();

    default boolean isHandlerExists() {
        return this.canBeBindless() && this.getBindingHandler() != 0;
    }

    boolean canBeBindless();

    default long createBindlessHandler(int textureId) {
        if (!this.canBeBindless()) {
            return 0L;
        }
        return ARBBindlessTexture.glGetTextureHandleARB(textureId);
    }

    default long createBindlessHandler(int textureId, int samplerId) {
        if (!this.canBeBindless()) {
            return 0L;
        }
        return ARBBindlessTexture.glGetTextureSamplerHandleARB(textureId, samplerId);
    }

    default void createARB64Handling() {
        if (!this.isHandlerExists()) {
            return;
        }
        ARBBindlessTexture.glMakeTextureHandleResidentARB(this.getBindingHandler());
    }

    default void removeARB64Handling() {
        if (!this.isHandlerExists()) {
            return;
        }
        ARBBindlessTexture.glMakeTextureHandleNonResidentARB(this.getBindingHandler());
    }
}
