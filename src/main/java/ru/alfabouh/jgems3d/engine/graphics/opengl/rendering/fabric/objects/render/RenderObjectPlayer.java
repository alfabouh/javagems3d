package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.alfabouh.jgems3d.engine.physics.entities.player.KinematicPlayer;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;

public class RenderObjectPlayer implements IRenderObjectFabric {
    public static float stepBobbing = 0.0f;
    private double lastGlfwTime;

    public RenderObjectPlayer() {
        this.lastGlfwTime = JGems.glfwTime();
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        WorldItem worldItem = entityObject.getWorldItem();
        if (worldItem instanceof KinematicPlayer) {
            KinematicPlayer dynamicPlayer = (KinematicPlayer) worldItem;
            double currTime = JGems.glfwTime();
            float delta = (float) (currTime - this.lastGlfwTime);
            this.lastGlfwTime = currTime;
            Vector3f vec3 = JGems.get().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput();
          // if (!JGems.get().isPaused() && dynamicPlayer.getScalarSpeed() > 0.001f && ((vec3.y < 0 || !dynamicPlayer.isCanPlayerJump()) || Math.abs(vec3.y) <= 0.1f) && vec3.length() > 0.5f) {
          //     RenderObjectPlayer.stepBobbing += delta * 60.0f * (dynamicPlayer.isRunning() ? 1.25f : 1.0f);
          // }
        }
    }

    @Override
    public void onStartRender(IRenderObject renderItem) {

    }

    @Override
    public void onStopRender(IRenderObject renderItem) {

    }
}
