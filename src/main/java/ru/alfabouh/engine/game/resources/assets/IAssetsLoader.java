package ru.alfabouh.engine.game.resources.assets;

import ru.alfabouh.engine.game.resources.cache.GameCache;

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