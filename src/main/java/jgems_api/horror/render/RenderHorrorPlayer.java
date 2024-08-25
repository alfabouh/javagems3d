/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package jgems_api.horror.render;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.physics.entities.player.SimpleKinematicPlayer;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public class RenderHorrorPlayer implements IRenderObjectFabric {
    public static float stepBobbing = 0.0f;

    public RenderHorrorPlayer() {
    }

    @Override
    public void onRender(FrameTicking frameTicking, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneEntity entityObject = (AbstractSceneEntity) renderItem;
        WorldItem worldItem = entityObject.getWorldItem();
        if (worldItem instanceof SimpleKinematicPlayer) {
            SimpleKinematicPlayer dynamicPlayer = (SimpleKinematicPlayer) worldItem;
            Vector3f vec3 = JGems3D.get().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput();
             if (!JGems3D.get().isPaused() && dynamicPlayer.getScalarSpeed() > 0.001f && ((vec3.y < 0 || !dynamicPlayer.canJump()) || Math.abs(vec3.y) <= 0.1f) && vec3.length() > 0.5f) {
                 RenderHorrorPlayer.stepBobbing += frameTicking.getFrameDeltaTime() * 60.0f;
             }
        }
    }

    @Override
    public void onPreRender(IRenderObject renderItem) {

    }

    @Override
    public void onPostRender(IRenderObject renderItem) {

    }
}
