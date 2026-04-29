package javagems3d.graphics.rendering.scene;

import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.physics.world.IWorld;

public interface IScene extends IWindow.ResizeEvent {
    IWindow getWindow();
    IWorld getWorld();
    OpenGLRenderer getSceneRenderer();
}