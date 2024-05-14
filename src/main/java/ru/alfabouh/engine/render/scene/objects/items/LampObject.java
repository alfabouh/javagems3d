package ru.alfabouh.engine.render.scene.objects.items;

import org.joml.Vector3d;
import ru.alfabouh.engine.game.resources.assets.materials.Material;
import ru.alfabouh.engine.game.resources.assets.materials.textures.ColorSample;
import ru.alfabouh.engine.physics.entities.prop.PhysLightCube;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.environment.light.Light;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.scene.world.SceneWorld;

public class LampObject extends EntityObject {
    public LampObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    @Override
    public void onAddLight(Light light) {
        super.onAddLight(light);
        if (this.getWorldItem() instanceof PhysLightCube) {
            if (this.getWorldItem().hasLight()) {
                Vector3d color = light.getLightColor();
                Material material = new Material();
                material.setDiffuse(ColorSample.createColor(color));
                this.getRenderData().setOverObjectMaterial(material);
                this.getRenderData().getModelRenderParams().setBright(true);
            }
        }
    }
}
