package ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples;

import org.joml.Vector2i;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ICached;

public interface IImageSample extends ISample, ICached {
    void bindTexture();

    Vector2i size();
}
