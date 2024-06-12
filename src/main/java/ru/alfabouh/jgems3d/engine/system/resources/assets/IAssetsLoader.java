package ru.alfabouh.jgems3d.engine.system.resources.assets;

import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;

public interface IAssetsLoader {
    void load(ResourceCache ResourceCache);

    LoadMode loadMode();

    int loadOrder();

    enum LoadMode {
        PARALLEL,
        PRE,
        POST
    }
}