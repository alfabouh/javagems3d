package ru.BouH.engine.render.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.materials.textures.ParticleTexturePack;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;

public class RenderParticleD2Data extends RenderObjectData {
    private ParticleTexturePack particleTexturePack;
    public static final float RENDER_PARTICLE_DISTANCE = 256.0f;

    public RenderParticleD2Data(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull ShaderManager shaderManager, ParticleTexturePack particleTexturePack, boolean isBright) {
        super(renderFabric, aClass, shaderManager);
        IEntityModelConstructor<WorldItem> particleModelConstructor = e -> new MeshDataGroup(MeshHelper.generatePlane3DMesh(new Vector3d(-0.05d, -0.05d, 0.0d), new Vector3d(-0.05d, 0.05d, 0.0d), new Vector3d(0.05d, -0.05d, 0.0d), new Vector3d(0.05d, 0.05d, 0.0d)));
        this.setEntityModelConstructor(particleModelConstructor);
        this.particleTexturePack = particleTexturePack;
        Material material = new Material();
        material.setDiffuse(particleTexturePack.getiImageSample()[0]);
        this.setOverObjectMaterial(material);
        this.getModelRenderParams().setShadowCaster(false);
        this.getModelRenderParams().setShadowReceiver(false);
        this.getModelRenderParams().setHasTransparency(true);
        this.getModelRenderParams().setRenderDistance(RenderParticleD2Data.RENDER_PARTICLE_DISTANCE);
        this.getModelRenderParams().invertTextureCoordinates();
        this.getModelRenderParams().setBright(isBright);
    }

    public RenderParticleD2Data setParticleTexturePack(ParticleTexturePack particleTexturePack) {
        this.particleTexturePack = particleTexturePack;
        return this;
    }

    public ParticleTexturePack getParticleTexturePack() {
        return this.particleTexturePack;
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
