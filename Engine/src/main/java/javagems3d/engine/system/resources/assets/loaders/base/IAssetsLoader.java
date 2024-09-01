/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.resources.assets.loaders.base;

import javagems3d.engine.system.resources.manager.GameResources;

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