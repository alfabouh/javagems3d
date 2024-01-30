package ru.BouH.engine.render.scene.scene_render.groups;

import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;

public class WorldRender extends SceneRenderBase {
    public WorldRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD", true));
    }

    public void onRender(double partialTicks) {
        for (PhysicsObject entityItem : this.getSceneWorld().getFilteredEntityList()) {
            if (entityItem.hasRender()) {
                entityItem.getShaderManager().bind();
                if (entityItem.isVisible()) {
                    entityItem.getShaderManager().getUtils().performProjectionMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                }
                entityItem.getShaderManager().unBind();
            }
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }
}