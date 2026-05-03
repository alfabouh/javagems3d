package javagems3d.graphics.rendering.scene.culling;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ISceneCulling extends IResourceInit, IWindow.ResizeEvent {
    void updateFrustum(@NotNull Matrix4f projectionMatrix, @NotNull ICamera camera);
    void cull(@NotNull Matrix4f projectionMatrix, @NotNull ICamera camera, @NotNull Collection<? extends ICulled>... objects);
    List<MeshNode3D<RenderMesh>> cullSubMeshes(Pose3D pose3D, List<MeshNode3D<RenderMesh>> meshNode);

    boolean isFrozen();
    void setFreeze(boolean freeze);
}
