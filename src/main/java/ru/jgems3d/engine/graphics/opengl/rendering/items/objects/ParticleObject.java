package ru.jgems3d.engine.graphics.opengl.rendering.items.objects;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderParticleD2Data;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ParticleTexturePack;

public class ParticleObject extends EntityObject {
    private int currentTexture;
    private double lastUpdate = JGems.glfwTime();

    public ParticleObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
        this.currentTexture = 0;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        RenderParticleD2Data renderParticleD2Data = (RenderParticleD2Data) this.getRenderData();
        ParticleTexturePack particleTexturePack = renderParticleD2Data.getParticleTexturePack();
        if (particleTexturePack.getAnimationRate() <= 0.0f) {
            this.currentTexture = JGems.random.nextInt(particleTexturePack.getTexturesNum());
            Material material = new Material();
            material.setDiffuse(particleTexturePack.getiImageSample()[this.currentTexture]);
            renderParticleD2Data.setOverObjectMaterial(material);
        }
        this.getModel().getFormat().setOrientedToView(true);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        RenderParticleD2Data renderParticleD2Data = (RenderParticleD2Data) this.getRenderData();
        ParticleTexturePack particleTexturePack = renderParticleD2Data.getParticleTexturePack();
        double curr = JGems.glfwTime();
        if (particleTexturePack.getAnimationRate() > 0.0f) {
            if (curr - this.lastUpdate > 1.0d * particleTexturePack.getAnimationRate()) {
                this.currentTexture = (this.currentTexture + 1) % particleTexturePack.getTexturesNum();
                Material material = new Material();
                material.setDiffuse(particleTexturePack.getiImageSample()[this.currentTexture]);
                renderParticleD2Data.setOverObjectMaterial(material);
                this.lastUpdate = curr;
            }
        }
    }
}
