package javagems3d.graphics.rendering.scene.culling;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.scene.culling.stages.CPUDistanceCulling;
import javagems3d.graphics.rendering.scene.culling.stages.CPUFrustumCulling;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SceneCulling <T extends SceneObject> implements ISceneCulling <T> {
    private Set<T> snapshot;

    private final CPUDistanceCulling cpuDistanceCulling;
    private final CPUFrustumCulling cpuFrustumCulling;
    private final Pipeline pipeline;
    private final int modes;
    private boolean freeze;

    public SceneCulling(int modes, @Nullable Pipeline pipeline) {
        this.modes = modes;
        this.pipeline = pipeline;
        this.snapshot = null;

        this.cpuDistanceCulling = new CPUDistanceCulling(256.0f);
        this.cpuFrustumCulling = new CPUFrustumCulling();
    }

    @Override
    public void cull(@NotNull Collection<T> sceneObjects, @NotNull Matrix4f projectionMatrix, @NotNull ICamera camera) {
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
        if ((this.getModes() & SceneCulling.DISTANCE) != 0) {
            this.getCpuDistanceCulling().setCamera(camera);
            this.getCpuDistanceCulling().filter(sceneObjects);
        }
        if ((this.getModes() & SceneCulling.FRUSTUM_CPU) != 0) {
            this.getCpuFrustumCulling().rebuildFrustum(projectionMatrix, TransformUtils.getViewMatrix(camera));
            this.getCpuFrustumCulling().filter(sceneObjects);
        }
        if (this.isFrozen() && this.snapshot == null) {
            this.snapshot = new HashSet<>(sceneObjects);
        }
    }

    @Override
    public void createResources() {
        this.getCpuDistanceCulling().createResources();
        this.getCpuFrustumCulling().createResources();
    }

    @Override
    public void destroyResources() {
        this.getCpuDistanceCulling().destroyResources();
        this.getCpuFrustumCulling().destroyResources();
        this.snapshot = null;
    }

    protected void preFilter(@NotNull Collection<T> sceneObjects) {
        sceneObjects.removeIf((e) -> {
            if (!e.canBeRendered() || !e.hasModel()) {
                return true;
            }
            return this.getPipeline() != null && !e.canBeRendered(this.getPipeline());
        });
    }

    public int getModes() {
        return this.modes;
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
    public void onWindowResize(IWindow window) {
        this.destroyResources();
        this.createResources();
    }

    public static int FRUSTUM_CPU = 1 << 2;
    public static int DISTANCE = 1 << 3;
}
