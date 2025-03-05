package javagems3d.graphics.rendering.programs.textures.base;

import org.joml.Vector2i;

public interface ICubeMapProgram extends ITextureProgram {
    Vector2i[] getSize();
}