/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.objects.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;

public final class LiquidObject implements ICulled {
    private final LiquidRenderData liquidRenderData;
    private final Liquid liquid;
    private final Model<Format3D> model;
    private final Vector2f textureScaling;

    public LiquidObject(Liquid iLiquid, LiquidRenderData liquidRenderData) {
        this.liquidRenderData = liquidRenderData;
        this.liquid = iLiquid;
        this.textureScaling = new Vector2f(1.0f);
        this.model = this.constructModel(iLiquid);
    }

    private Model<Format3D> constructModel(Liquid liquid) {
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
        return MeshHelper.generatePlane3DModel(v1, v2, v3, v4);
    }

    public Vector2f getTextureScaling() {
        return new Vector2f(this.textureScaling);
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public LiquidRenderData getRenderLiquidData() {
        return this.liquidRenderData;
    }

    public Liquid getLiquid() {
        return this.liquid;
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }
}
