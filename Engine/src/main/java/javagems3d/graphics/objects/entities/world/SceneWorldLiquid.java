package javagems3d.graphics.objects.entities.world;

import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.physics.world.triggers.liquids.base.Liquid;

import javagems3d.system.resources.assets.models.helper.MeshHelper;

public final class SceneWorldLiquid implements IWorldObject, ICulled {
    private final LiquidRenderData liquidRenderData;
    private final Liquid liquid;
    private Model3D model;
    private final Vector2f textureScaling;

    public SceneWorldLiquid(Liquid iLiquid, LiquidRenderData liquidRenderData) {
        this.liquidRenderData = liquidRenderData;
        this.liquid = iLiquid;
        this.textureScaling = new Vector2f(1.0f);
    }

    private Model3D constructModel(Liquid liquid) {
        Vector3f location = liquid.getSimpleTriggerZone().getZone().getLocation();
        Vector3f size = new Vector3f(liquid.getSimpleTriggerZone().getZone().getSize()).mul(0.5f);
        double y = location.y + size.y - 0.1f;
        Vector3f v1 = new Vector3f(location.x - size.x, (float) y, location.z - size.z);
        Vector3f v2 = new Vector3f(location.x - size.x, (float) y, location.z + size.z);
        Vector3f v3 = new Vector3f(location.x + size.x, (float) y, location.z - size.z);
        Vector3f v4 = new Vector3f(location.x + size.x, (float) y, location.z + size.z);
        float sizeBound = 3.0f;
        if (size.x > sizeBound) {
            this.textureScaling.mul(size.x / sizeBound, 1.0f);
        }
        if (size.z > sizeBound) {
            this.textureScaling.mul(1.0f, size.z / sizeBound);
        }
        return MeshHelper.generatePlane3DModel(ArbitraryArguments.pass(this.getRenderLiquidData().getLiquidMaterial()), v1, v2, v3, v4);
    }

    public Vector2f getTextureScaling() {
        return new Vector2f(this.textureScaling);
    }

    public Model3D getModel() {
        return this.model;
    }

    public LiquidRenderData getRenderLiquidData() {
        return this.liquidRenderData;
    }

    public Liquid getLiquid() {
        return this.liquid;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        this.model = this.constructModel(this.getLiquid());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        if (this.getModel() != null) {
            this.getModel().clear();
        }
    }

    @Override
    public void setDead() {
    }

    @Override
    public boolean isDead() {
        return this.getLiquid().isDead();
    }

    @Override
    public @NotNull CullingRules getCullingRules() {
        return CullingRules.get();
    }

    @Override
    public CullingAABB getCullingData() {
        Vector3f min = this.getLiquid().getZone().getLocation().sub(this.getLiquid().getZone().getSize().mul(0.5f));
        Vector3f max = this.getLiquid().getZone().getLocation().add(this.getLiquid().getZone().getSize().mul(0.5f));
        return new CullingAABB(min, max);
    }
}
