package ru.BouH.engine.render.scene.fabric.render;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.BouH.engine.render.scene.objects.IRenderObject;

public class RenderPlayerSP implements IRenderFabric {
    public static float stepBobbing = 0.0f;
    private double lastGlfwTime;

    public RenderPlayerSP() {
        this.lastGlfwTime = Game.glfwTime();
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        double currTime = Game.glfwTime();
        double delta = currTime - this.lastGlfwTime;
        this.lastGlfwTime = currTime;
        Vector3d vec3 = Game.getGame().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput();
        if (Math.abs(vec3.y) <= 0.1f && vec3.length() > 0.5f) {
            RenderPlayerSP.stepBobbing += (float) delta * 60.0f;
        }
    }

    @Override
    public void onStartRender(IRenderObject renderItem) {

    }

    @Override
    public void onStopRender(IRenderObject renderItem) {

    }
}
