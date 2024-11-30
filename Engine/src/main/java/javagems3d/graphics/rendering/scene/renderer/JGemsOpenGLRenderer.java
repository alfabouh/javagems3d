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

package javagems3d.graphics.rendering.scene.renderer;

import javagems3d.graphics.rendering.scene.JGemsSceneData;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.nodes.groups.NodesGroup;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JGemsOpenGLRenderer extends OpenGLRenderer {
    private final Map<NodesGroup, @NotNull IRenderProcessor> conveyorNodes;

    public JGemsOpenGLRenderer(IWindow window, @NotNull JGemsSceneData sceneData) {
        super(window, sceneData);
        this.conveyorNodes = new HashMap<>();
    }

    @Override
    public void onStartRender() {
        this.createResources();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {

    }

    @Override
    public void onStopRender() {
        this.getConveyorNodes().clear();
        this.destroyResources();
    }

    protected void createResources() {
        this.getConveyorNodes().values().forEach(IRenderProcessor::createResources);
    }

    protected void destroyResources() {
        this.getConveyorNodes().values().forEach(IRenderProcessor::destroyResources);
    }

    @Override
    public void onWindowResize(IWindow window) {
        this.getConveyorNodes().values().forEach(e -> e.onWindowResize(window));
    }

    protected Map<NodesGroup, @NotNull IRenderProcessor> getConveyorNodes() {
        return this.conveyorNodes;
    }
}