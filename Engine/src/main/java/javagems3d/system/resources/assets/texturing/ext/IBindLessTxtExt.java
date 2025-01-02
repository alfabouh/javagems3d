package javagems3d.system.resources.assets.texturing.ext;

import org.lwjgl.opengl.ARBBindlessTexture;

public interface IBindLessTxtExt {
    long getBindingHandler();
    default long createBindingHandler(int textureId) {
        return ARBBindlessTexture.glGetTextureHandleARB(textureId);
    }

    default void createARB64Handling() {
        ARBBindlessTexture.glMakeTextureHandleResidentARB(this.getBindingHandler());
    }
    default void removeARB64Handling() {
        ARBBindlessTexture.glMakeTextureHandleNonResidentARB(this.getBindingHandler());
    }
}
