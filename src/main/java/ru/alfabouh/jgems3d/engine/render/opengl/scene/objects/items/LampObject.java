package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items;

import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.physics.entities.prop.PhysLightCube;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.ColorSample;

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
