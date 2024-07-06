package ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base;

import ru.alfabouh.jgems3d.engine.system.resources.manager.objects.GameResources;

public interface IAssetsLoader {
    void load(GameResources gameResources);

    LoadMode loadMode();

    int loadOrder();

    enum LoadMode {
        PARALLEL,
        PRE,
        POST
    }
}