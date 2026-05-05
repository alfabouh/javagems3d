package javagems3d.graphics.environment.particles.fx;

import javagems3d.graphics.environment.particles.data.ParticleFXRenderData;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.managing.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class ParticleFX implements ICulled {
    private final ParticleFXRenderData particleFXRenderData;
    private Vector3f position;
    private Vector3f scaling;
    private final int maxID;
    private int currentTextureID;

    public ParticleFX(@NotNull ParticleFXRenderData particleFXRenderData) {
        this.particleFXRenderData = particleFXRenderData;
        this.currentTextureID = 0;
        this.maxID = particleFXRenderData.particleFXMaterial().getCellsXY().x * particleFXRenderData.particleFXMaterial().getCellsXY().y;
        this.position = new Vector3f();
        this.scaling = new Vector3f(1.0f);
    }

    public abstract void update(IRenderWorld renderWorld);

    public static MeshBuffer getParticlesMeshBuffer() {
        return ResourceManager.GLOBAL_PARTICLE_MESHBUFFER();
    }

    @Override
    public @NotNull CullingRules getCullingRules() {
        return new CullingRules(false, false);
    }

    @Override
    public CullingAABB getCullingData() {
        return ParticleFX.getParticlesMeshBuffer().getMeshAABBData().getNormalizedAABB(new Pose3D(this.position, new Vector3f(), this.scaling));
    }

    public Matrix4f getMatrix() {
        return TransformUtils.getOrientedToViewModelMatrix(new Pose3D().setPosition(this.getPosition()).setScaling(this.getScaling()), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    public ParticleFXRenderData getParticleFXRenderData() {
        return this.particleFXRenderData;
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

    public int getMaxID() {
        return this.maxID;
    }

    public int getCurrentTextureID() {
        return this.currentTextureID;
    }
}
