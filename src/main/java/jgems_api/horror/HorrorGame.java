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

package jgems_api.horror;

import jgems_api.horror.events.HorrorEvents;
import jgems_api.horror.items.ItemZippoModded;
import jgems_api.horror.resources.HorrorModelLoader;
import jgems_api.horror.resources.HorrorRenderDataLoader;
import jgems_api.horror.resources.HorrorShaderLoader;
import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render.AbstractInventoryZippo;
import ru.jgems3d.engine.system.core.EngineSystem;
import ru.jgems3d.engine.system.inventory.items.ItemZippo;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine_api.app.JGemsGameApplication;
import ru.jgems3d.engine_api.app.JGemsGameEntry;
import ru.jgems3d.engine_api.app.JGemsGameInstance;
import ru.jgems3d.engine_api.events.IAppEventSubscriber;
import ru.jgems3d.engine_api.manager.AppManager;
import ru.jgems3d.engine_api.resources.IAppResourceLoader;
import jgems_api.horror.manager.HorrorAppManager;

@JGemsGameEntry(gameTitle = "An Episode Of One Scary Horror", gameVersion = "0.1a", devStage = JGemsGameEntry.DevStage.PRE_ALPHA)
public class HorrorGame implements JGemsGameApplication {
    @JGemsGameInstance
    private static HorrorGame horrorGame;

    public HorrorModelLoader horrorModelLoader;
    public HorrorRenderDataLoader horrorRenderDataLoader;
    public HorrorShaderLoader horrorShaderLoader;

    public HorrorGame() {
        this.horrorModelLoader = new HorrorModelLoader();
        this.horrorShaderLoader = new HorrorShaderLoader();
        this.horrorRenderDataLoader = new HorrorRenderDataLoader();
    }

    @Override
    public void loadResources(IAppResourceLoader appResourceLoader) {
        appResourceLoader.addAssetsLoader(this.horrorModelLoader);
        appResourceLoader.addAssetsLoader(this.horrorRenderDataLoader);
        appResourceLoader.addShadersLoader(this.horrorShaderLoader);
    }

    @Override
    public void subscribeEvents(IAppEventSubscriber appEventSubscriber) {
        appEventSubscriber.addClassWithEvents(HorrorEvents.class);
    }

    @Override
    public void preInitEvent(EngineSystem engineSystem) {
        JGemsDebugGlobalConstants.PATH_GEN_GRAPH_GAP = 0.6f;
    }

    @Override
    public void postInitEvent(EngineSystem engineSystem) {
        JGemsHelper.LOCALISATION.createLocalisation("English", new JGemsPath("/assets/testgame/lang/"));
    }

    @Override
    public @NotNull AppManager createAppManager() {
        return new HorrorAppManager(null);
    }

    public static HorrorGame get() {
        return HorrorGame.horrorGame;
    }
}
