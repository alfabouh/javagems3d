package ru.jgems3d.engine.system.resources.assets.material.samples.base;

import org.joml.Vector2i;
import ru.jgems3d.engine.system.resources.cache.ICached;

public interface IImageSample extends ISample, ICached {
    int getTextureId();
    int getTextureAttachment();
    void bindTexture();
    Vector2i size();
}
