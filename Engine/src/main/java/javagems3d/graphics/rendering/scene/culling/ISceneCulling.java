package javagems3d.graphics.rendering.scene.culling;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.Set;

public interface ISceneCulling <T extends SceneObject> extends IResourceInit, IWindow.ResizeEvent {
    void cull(@NotNull Collection<T> sceneObjects, @NotNull Matrix4f projectionMatrix, @NotNull ICamera camera);
}
