package ru.alfabouh.engine.game.resources.assets.materials.textures;

import org.joml.Vector2d;
import ru.alfabouh.engine.game.resources.cache.ICached;

public interface IImageSample extends ISample, ICached {
    void bindTexture();
}
