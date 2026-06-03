package javagems3d.graphics.environment.particles.fx;

import javagems3d.graphics.environment.particles.data.ParticleFXRenderConfig;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.managing.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class ParticleFX implements ICulled, IWorldObject, IWorldTicked {
    private final ParticleFXRenderConfig particleFXRenderConfig;
    private Vector3f position;
    private Vector3f scaling;
    private int currentTextureID;
    private int interpolateWithTextureID;
    private boolean isDead;

    public ParticleFX(@NotNull ParticleFXRenderConfig particleFXRenderConfig) {
        this.particleFXRenderConfig = particleFXRenderConfig;
        this.currentTextureID = 0;
        this.interpolateWithTextureID = 0;
        this.position = new Vector3f();
        this.scaling = new Vector3f(1.0f);
        this.isDead = false;
    }

    public static MeshBuffer getParticlesMeshBuffer() {
        return ResourceManager.GLOBAL_PARTICLE_MESHBUFFER();
    }

    @Override
    public @NotNull CullingRules getCullingRules() {
        return new CullingRules(false, false);
    }

    @Override
    public CullingAABB getCullingData() {
        return ParticleFX.getParticlesMeshBuffer().getMeshAABBData().getNormalizedAABB(this.getMatrix());
    }

    public Matrix4f getMatrix() {
        return TransformUtils.getOrientedToViewModelMatrix(new Pose3D().setPosition(this.getPosition()).setScaling(this.getScaling()), JGemsTransformManager.INSTANCE.getCameraViewMatrix(), this.getParticleFXRenderConfig().isNormalizeY());
    }

    public abstract float interpolationPoint();

    public boolean snapToEmitterPos() {
        return false;
    }

    public ParticleFXRenderConfig getParticleFXRenderConfig() {
        return this.particleFXRenderConfig;
    }

    public Vector3f getScaling() {
        return new Vector3f(this.scaling);
    }

    public ParticleFX setScaling(Vector3f scaling) {
        this.scaling = scaling;
        return this;
    }

    public Vector3f getPosition() {
        return new Vector3f(this.position);
    }

    public ParticleFX setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public ParticleFX setCurrentTextureID(int currentTextureID) {
        this.currentTextureID = currentTextureID;
        return this;
    }

    public int getInterpolateWithTextureID() {
        return this.interpolateWithTextureID;
    }

    public ParticleFX setInterpolateWithTextureID(int interpolateWithTextureID) {
        this.interpolateWithTextureID = interpolateWithTextureID;
        return this;
    }

    @Override
    public void setDead() {
        this.isDead = true;
    }

    @Override
    public boolean isDead() {
        return this.isDead;
    }

    public int getCurrentTextureID() {
        return this.currentTextureID;
    }
}
