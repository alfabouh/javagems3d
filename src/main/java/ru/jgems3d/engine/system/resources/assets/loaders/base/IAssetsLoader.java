package ru.jgems3d.engine.system.resources.assets.loaders.base;

import ru.jgems3d.engine.system.resources.manager.GameResources;

public interface IAssetsLoader {
    void load(GameResources gameResources);

    LoadMode loadMode();

    LoadPriority loadPriority();

    enum LoadMode {
        PARALLEL,
        NORMAL
    }

    enum LoadPriority {
        LOW(2),
        NORMAL(1),
        HIGH(0);

        public final int priority;

        LoadPriority(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}