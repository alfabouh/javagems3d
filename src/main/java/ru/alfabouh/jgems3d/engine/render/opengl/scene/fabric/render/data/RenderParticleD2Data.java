package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.ParticleTexturePack;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class RenderParticleD2Data extends RenderObjectData {
    public static final float RENDER_PARTICLE_DISTANCE = 256.0f;
    private ParticleTexturePack particleTexturePack;

    public RenderParticleD2Data(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull JGemsShaderManager shaderManager, ParticleTexturePack particleTexturePack, boolean isBright) {
        super(renderFabric, aClass, shaderManager);
        IEntityModelConstructor<WorldItem> particleModelConstructor = e -> new MeshDataGroup(MeshHelper.generatePlane3DMesh(new Vector3f(-0.05f, -0.05f, 0.0f), new Vector3f(-0.05f, 0.05f, 0.0f), new Vector3f(0.05f, -0.05f, 0.0f), new Vector3f(0.05f, 0.05f, 0.0f)));
        this.setEntityModelConstructor(particleModelConstructor);
        this.particleTexturePack = particleTexturePack;
        Material material = new Material();
        material.setDiffuse(particleTexturePack.getiImageSample()[0]);
        this.setOverObjectMaterial(material);
        this.getModelRenderParams().setShadowCaster(false);
        this.getModelRenderParams().setHasTransparency(true);
        this.getModelRenderParams().setRenderDistance(RenderParticleD2Data.RENDER_PARTICLE_DISTANCE);
        this.getModelRenderParams().invertTextureCoordinates();
        this.getModelRenderParams().setBright(isBright);
    }

    public ParticleTexturePack getParticleTexturePack() {
        return this.particleTexturePack;
    }

    public RenderParticleD2Data setParticleTexturePack(ParticleTexturePack particleTexturePack) {
        this.particleTexturePack = particleTexturePack;
        return this;
    }

    @Override
    protected RenderParticleD2Data copyObject() {
        RenderParticleD2Data renderObjectData = new RenderParticleD2Data(this.getRenderFabric(), this.getRenderClass(), this.getModelRenderParams().getShaderManager(), this.getParticleTexturePack(), this.getModelRenderParams().isBright());
        renderObjectData.setMeshDataGroup(this.getMeshDataGroup());
        renderObjectData.setModelTextureScaling(this.getModelTextureScaling());
        renderObjectData.setOverObjectMaterial(this.getOverObjectMaterial());
        renderObjectData.setEntityModelConstructor(this.getEntityModelConstructor());
        renderObjectData.setModelRenderParams(this.getModelRenderParams().copy());
        return renderObjectData;
    }
}
