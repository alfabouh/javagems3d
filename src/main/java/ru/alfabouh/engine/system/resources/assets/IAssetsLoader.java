package ru.alfabouh.engine.system.resources.assets;

import ru.alfabouh.engine.system.resources.cache.GameCache;

public interface IAssetsLoader {
    void load(GameCache gameCache);

    LoadMode loadMode();

    int loadOrder();

    enum LoadMode {
        PARALLEL,
        PRE,
        POST
    }
}