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

package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base;

import ru.jgems3d.engine.api_bridge.events.APIEventsLauncher;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine_api.events.bus.Events;

public abstract class SceneRenderBase {
    private final int renderOrder;
    private final RenderGroup renderGroup;
    private final JGemsOpenGLRenderer sceneRender;

    protected SceneRenderBase(int renderOrder, JGemsOpenGLRenderer sceneRender, RenderGroup renderGroup) {
        this.renderOrder = renderOrder;
        this.renderGroup = renderGroup;
        this.sceneRender = sceneRender;
    }

    public SceneWorld getSceneWorld() {
        return this.getSceneRenderer().getSceneData().getSceneWorld();
    }

    public ICamera getCamera() {
        return this.getSceneRenderer().getSceneData().getCamera();
    }

    protected abstract void onRender(FrameTicking frameTicking);

    public void onBaseRender(FrameTicking frameTicking) {
        if (!APIEventsLauncher.pushEvent(new Events.RenderBaseRender(frameTicking, this)).isCancelled()) {
            this.onRender(frameTicking);
        }
    }

    public void onStartRender() {
        APIEventsLauncher.pushEvent(new Events.RenderBaseStartRender(this));
        JGemsHelper.getLogger().log("Scene " + this.getRenderGroup().getId() + ": render start!");
    }

    public void onStopRender() {
        APIEventsLauncher.pushEvent(new Events.RenderBaseEndRender(this));
        JGemsHelper.getLogger().log("Scene " + this.getRenderGroup().getId() + ": render stop!");
    }

    public int getRenderOrder() {
        return this.renderOrder;
    }

    public RenderGroup getRenderGroup() {
        return this.renderGroup;
    }

    public JGemsOpenGLRenderer getSceneRenderer() {
        return this.sceneRender;
    }
}
