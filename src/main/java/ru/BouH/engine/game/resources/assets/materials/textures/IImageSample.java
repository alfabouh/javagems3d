package ru.BouH.engine.game.resources.assets.materials.textures;

import org.joml.Vector2d;
import ru.BouH.engine.game.resources.cache.ICached;

public interface IImageSample extends ISample, ICached {
    Vector2d scaling();

    void bindTexture();
}
