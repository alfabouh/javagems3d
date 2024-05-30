package ru.alfabouh.engine.system.resources.assets.materials.textures;

import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.alfabouh.engine.system.resources.cache.ICached;

public interface IImageSample extends ISample, ICached {
    void bindTexture();
    Vector2i size();
}
