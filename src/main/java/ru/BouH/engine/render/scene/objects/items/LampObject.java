package ru.BouH.engine.render.scene.objects.items;

import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.materials.textures.ColorSample;
import ru.BouH.engine.physics.entities.prop.PhysLightCube;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class LampObject extends EntityObject {
    public LampObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    @Override
    public void onAddLight(Light light) {
        super.onAddLight(light);
        if (this.getWorldItem() instanceof PhysLightCube) {
            if (this.getWorldItem().hasLight()) {
                Vector3d color = light.getLightColor().mul(8.0d);
                Material material = new Material();
                material.setDiffuse(ColorSample.createColor(color));
                this.getRenderData().setOverObjectMaterial(material);
                this.getRenderData().getModelRenderParams().setLightOpaque(false);
            }
        }
    }
}
