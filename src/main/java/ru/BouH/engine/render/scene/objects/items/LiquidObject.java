package ru.BouH.engine.render.scene.objects.items;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.physics.liquids.ILiquid;
import ru.BouH.engine.render.frustum.ICullable;
import ru.BouH.engine.render.frustum.RenderABB;
import ru.BouH.engine.render.scene.fabric.render.data.RenderLiquidData;

public final class LiquidObject implements ICullable {
    private final RenderLiquidData renderLiquidData;
    private final ILiquid liquid;
    private final Model<Format3D> model;
    private final Vector2d textureScaling;

    public LiquidObject(ILiquid iLiquid, RenderLiquidData renderLiquidData) {
        this.renderLiquidData = renderLiquidData;
        this.liquid = iLiquid;
        this.textureScaling = new Vector2d(1.0d);
        this.model = this.constructModel(iLiquid);
    }

    private Model<Format3D> constructModel(ILiquid liquid) {
        Vector3d location = liquid.getZone().getLocation();
        Vector3d size = new Vector3d(liquid.getZone().getSize()).mul(0.5d);
        double y = location.y + size.y;
        Vector3d v1 = new Vector3d(location.x - size.x, y, location.z - size.z);
        Vector3d v2 = new Vector3d(location.x - size.x, y, location.z + size.z);
        Vector3d v3 = new Vector3d(location.x + size.x, y, location.z - size.z);
        Vector3d v4 = new Vector3d(location.x + size.x, y, location.z + size.z);
        if (size.x > size.z) {
            this.textureScaling.set(new Vector2d(size.x / size.z, 1.0d));
        } else if (size.x < size.z) {
            this.textureScaling.set(new Vector2d(1.0d, size.z / size.x));
        }
        float sizeBound = 10.0f;
        if (size.x > sizeBound) {
            this.textureScaling.mul(size.x / sizeBound, 1.0f);
        }
        if (size.z > sizeBound) {
            this.textureScaling.mul(1.0f, size.z / sizeBound);
        }
        return MeshHelper.generatePlane3DModel(v1, v2, v3, v4);
    }

    public Vector2d getTextureScaling() {
        return new Vector2d(this.textureScaling);
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public RenderLiquidData getRenderLiquidData() {
        return this.renderLiquidData;
    }

    public ILiquid getLiquid() {
        return this.liquid;
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }

    @Override
    public RenderABB getRenderABB() {
        return new RenderABB(this.getLiquid().getZone().getLocation(), this.getLiquid().getZone().getSize());
    }
}
