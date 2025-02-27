package javagems3d.system.resources.assets.texturing.base;

import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.system.resources.cache.ICached;
import org.lwjgl.opengl.GL46;

public interface ImageBasedTexture extends ITextureProgram, ISample, ICached {
    void init(@Nullable IProperties properties, @NotNull IData data);
    void reload(@Nullable IProperties properties);
}
