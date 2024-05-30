package ru.alfabouh.engine.render.scene.fabric.render;

import org.joml.Vector3d;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.engine.render.scene.objects.IRenderObject;
import ru.alfabouh.engine.render.scene.objects.items.PhysicsObject;

public class RenderPlayerSP implements IRenderFabric {
    public static float stepBobbing = 0.0f;
    private double lastGlfwTime;

    public RenderPlayerSP() {
        this.lastGlfwTime = JGems.glfwTime();
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        PhysicsObject entityObject = (PhysicsObject) renderItem;
        WorldItem worldItem = entityObject.getWorldItem();
        if (worldItem instanceof KinematicPlayerSP) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) worldItem;
            double currTime = JGems.glfwTime();
            double delta = currTime - this.lastGlfwTime;
            this.lastGlfwTime = currTime;
            Vector3d vec3 = JGems.get().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput();
            if (!JGems.get().isPaused() && kinematicPlayerSP.getScalarSpeed() > 0.001f && ((vec3.y < 0 || !kinematicPlayerSP.isCanPlayerJump()) || Math.abs(vec3.y) <= 0.1f) && vec3.length() > 0.5f) {
                RenderPlayerSP.stepBobbing += (float) delta * 60.0f * (kinematicPlayerSP.isRunning() ? 1.25f : 1.0f);
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
