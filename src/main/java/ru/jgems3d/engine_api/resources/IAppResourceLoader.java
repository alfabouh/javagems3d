package ru.jgems3d.engine_api.resources;

import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;

public interface IAppResourceLoader {
    void addAssetsLoader(IAssetsLoader assetsLoader);
    void addShadersLoader(ShadersLoader shadersLoader);
}
