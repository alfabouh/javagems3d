package javagems3d.graphics.rendering.scene.culling;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.scene.culling.stages.CPUDistanceCulling;
import javagems3d.graphics.rendering.scene.culling.stages.CPUFrustumCulling;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SceneCulling implements ISceneCulling {
    private Set<SceneObject> snapshot;

    private final OpenGLRenderer openGLRenderer;
    private final CPUDistanceCulling cpuDistanceCulling;
    private final CPUFrustumCulling cpuFrustumCulling;
    private final Pipeline pipeline;

    private boolean freeze;

    public SceneCulling(OpenGLRenderer openGLRenderer, @Nullable Pipeline pipeline) {
        this.openGLRenderer = openGLRenderer;
        this.pipeline = pipeline;
        this.snapshot = null;

        this.cpuDistanceCulling = new CPUDistanceCulling(256.0f);
        this.cpuFrustumCulling = new CPUFrustumCulling();
    }

    @Override
    public @NotNull OpenGLRenderer getOpenGLRender() {
        return this.openGLRenderer;
    }

    @Override
    public void cull(@NotNull Collection<SceneObject> sceneObjects) {
        if (sceneObjects.isEmpty()) {
            return;
        }
        this.setFreeze(false);
        if (this.snapshot != null) {
            if (!this.isFrozen()) {
                this.snapshot = null;
            } else {
                sceneObjects.clear();
                sceneObjects.addAll(this.snapshot);
                return;
            }
        }
        this.preFilter(sceneObjects);
        if (!this.disableDistanceCulling()) {
            this.getCpuDistanceCulling().setCamera(this.getOpenGLRender().getCamera());
            this.getCpuDistanceCulling().filter(sceneObjects);
        }
        if (!this.disableFrustumCulling()) {
            this.getCpuFrustumCulling().rebuildFrustum(JGemsTransformManager.INSTANCE.getPerspectiveMatrix(), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
            this.getCpuFrustumCulling().filter(sceneObjects);
        }
        if (this.isFrozen() && this.snapshot == null) {
            this.snapshot = new HashSet<>(sceneObjects);
        }
    }

    @Override
    public void createResources() {
        this.getCpuDistanceCulling().createResources(this.getOpenGLRender());
        this.getCpuFrustumCulling().createResources(this.getOpenGLRender());
    }

    @Override
    public void destroyResources() {
        this.getCpuDistanceCulling().destroyResources(this.getOpenGLRender());
        this.getCpuFrustumCulling().destroyResources(this.getOpenGLRender());
        this.snapshot = null;
    }

    protected void preFilter(@NotNull Collection<SceneObject> sceneObjects) {
        sceneObjects.removeIf((e) -> {
            if (!e.canBeRendered() || !e.hasModel()) {
                return true;
            }
            return this.getPipeline() != null && !e.canBeRendered(this.getPipeline());
        });
    }

    public boolean isFrozen() {
        return this.freeze;
    }

    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public CPUDistanceCulling getCpuDistanceCulling() {
        return this.cpuDistanceCulling;
    }

    public CPUFrustumCulling getCpuFrustumCulling() {
        return this.cpuFrustumCulling;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    @Override
    public boolean disableDistanceCulling() {
        return false;
    }

    @Override
    public boolean disableFrustumCulling() {
        return false;
    }

    @Override
    public void onWindowResize(IWindow window) {
        this.destroyResources();
        this.createResources();
    }
}
