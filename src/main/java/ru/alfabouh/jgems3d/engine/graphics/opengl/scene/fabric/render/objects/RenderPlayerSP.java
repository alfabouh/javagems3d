package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.KinematicPlayerSP;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;

public class RenderPlayerSP implements IRenderFabric {
    public static float stepBobbing = 0.0f;
    private double lastGlfwTime;

    public RenderPlayerSP() {
        this.lastGlfwTime = JGems.glfwTime();
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneItemObject entityObject = (AbstractSceneItemObject) renderItem;
        WorldItem worldItem = entityObject.getWorldItem();
        if (worldItem instanceof KinematicPlayerSP) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) worldItem;
            double currTime = JGems.glfwTime();
            float delta = (float) (currTime - this.lastGlfwTime);
            this.lastGlfwTime = currTime;
            Vector3f vec3 = JGems.get().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput();
            if (!JGems.get().isPaused() && kinematicPlayerSP.getScalarSpeed() > 0.001f && ((vec3.y < 0 || !kinematicPlayerSP.isCanPlayerJump()) || Math.abs(vec3.y) <= 0.1f) && vec3.length() > 0.5f) {
                RenderPlayerSP.stepBobbing += delta * 60.0f * (kinematicPlayerSP.isRunning() ? 1.25f : 1.0f);
            }
        }
    }

    @Override
    public void onStartRender(IRenderObject renderItem) {

    }

    @Override
    public void onStopRender(IRenderObject renderItem) {

    }
}
