package javagems3d.graphics.rendering.scene;

import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.graphics.rendering.ui.jgems_imgui.ImmediateUI;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.graphics.world.SceneWorld;

public class JGemsSceneData {
    private TransformationUtils transformationUtils;
    private SceneWorld sceneWorld;
    private ImmediateUI immediateUI;
    private IndirectRenderBuffer sceneIndirectRenderBuffer;

    public JGemsSceneData(TransformationUtils transformationUtils, SceneWorld sceneWorld, ImmediateUI immediateUI, IndirectRenderBuffer sceneIndirectRenderBuffer) {
        this.transformationUtils = transformationUtils;
        this.sceneWorld = sceneWorld;
        this.immediateUI = immediateUI;
        this.sceneIndirectRenderBuffer = sceneIndirectRenderBuffer;
    }

    public TransformationUtils getTransformationUtils() {
        return this.transformationUtils;
    }

    public JGemsSceneData setTransformationUtils(TransformationUtils transformationUtils) {
        this.transformationUtils = transformationUtils;
        return this;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public JGemsSceneData setSceneWorld(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        return this;
    }

    public ImmediateUI getImmediateUI() {
        return this.immediateUI;
    }

    public JGemsSceneData setImmediateUI(ImmediateUI immediateUI) {
        this.immediateUI = immediateUI;
        return this;
    }

    public IndirectRenderBuffer getSceneIndirectRenderBuffer() {
        return this.sceneIndirectRenderBuffer;
    }

    public JGemsSceneData setSceneIndirectRenderBuffer(IndirectRenderBuffer sceneIndirectRenderBuffer) {
        this.sceneIndirectRenderBuffer = sceneIndirectRenderBuffer;
        return this;
    }
}
