/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.scene.culling;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.culling.stages.CPUDistanceCulling;
import javagems3d.graphics.rendering.scene.culling.stages.CPUFrustumCulling;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.*;

public class SceneCulling implements ISceneCulling {
    private final CPUDistanceCulling cpuDistanceCulling;
    private final CPUFrustumCulling cpuFrustumCulling;
    private final Pipeline pipeline;
    private final int modes;
    private boolean freeze;

    public SceneCulling(int modes, @Nullable Pipeline pipeline) {
        this.modes = modes;
        this.pipeline = pipeline;

        this.cpuDistanceCulling = new CPUDistanceCulling(256.0f);
        this.cpuFrustumCulling = new CPUFrustumCulling();
    }

    @Override
    public void updateFrustum(@NotNull Matrix4f projectionMatrix, @NotNull ICamera camera) {
        if (!this.isFrozen()) {
            if ((this.getModes() & SceneCulling.FRUSTUM_CPU) != 0) {
                this.getCpuFrustumCulling().rebuildFrustum(projectionMatrix, TransformUtils.getViewMatrix(camera));
            }
        }
    }

    @Override
    public final void cull(@NotNull Matrix4f projectionMatrix, @NotNull ICamera camera, @NotNull Collection<? extends ICulled>[] objects) {
        for (Collection<? extends ICulled> collection : objects) {
            if (collection.isEmpty()) {
                continue;
            }
            if ((this.getModes() & SceneCulling.DISTANCE) != 0) {
                this.getCpuDistanceCulling().setCamera(camera);
                this.getCpuDistanceCulling().filter(collection);
            }
            if ((this.getModes() & SceneCulling.FRUSTUM_CPU) != 0) {
                this.getCpuFrustumCulling().filter(collection);
            }
        }
    }

    @Override
    public List<MeshNode3D<RenderMesh>> cullSubMeshes(Pose3D pose3D, List<MeshNode3D<RenderMesh>> meshNodes) {
        if ((this.getModes() & SceneCulling.FRUSTUM_CPU) != 0) {
            return meshNodes.stream().filter(e -> {
                boolean flag = e.getMeshData().getLocalAABB() == null || this.getCpuFrustumCulling().isInFrustum(MeshBoundingBoxData.transformAABB(e.getMeshData().getLocalAABB(), pose3D));
                if (!flag) {
                    JGemsOpenGLRenderer.DEBUG_CULLED_SUBMESHES++;
                }
                return flag;
            }).toList();
        }
        return meshNodes;
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
