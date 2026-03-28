package api.scripting.coding.env.internal.util.world.render.world.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.bound.JSBoxAABB;
import api.scripting.coding.env.internal.util.world.physical.zones.instances.JSLiquid;
import api.scripting.coding.env.internal.util.world.render.data.JSLiquidRenderData;
import api.scripting.coding.env.internal.util.world.render.processing.JSCullingRules;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

@JSCodingClass(binding = "JSSceneWorldLiquid", description = "Wrapper for SceneWorldLiquid, representing liquid volumes in the scene.")
public class JSSceneWorldLiquid {
    @JSHideFromDoc
    private final SceneWorldLiquid liquidObj;

    @JSCodingConstructor(description = "Create a JSSceneWorldLiquid instance", paramNames = {"liquid", "renderData"})
    public JSSceneWorldLiquid(@NotNull JSLiquid liquid, @NotNull JSLiquidRenderData renderData) {
        this.liquidObj = new SceneWorldLiquid(liquid.getJavaLiquid(), renderData.getJavaLiquidRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java SceneWorldLiquid object (unsafe, internal use)")
    public SceneWorldLiquid getJavaSceneWorldLiquid() {
        return this.liquidObj;
    }

    @JSCodingFunctionOrMethod(description = "Returns the texture scaling applied to the liquid plane")
    public JSVector2f getTextureScaling() {
        Vector2f scale = this.liquidObj.getTextureScaling();
        return new JSVector2f(scale.x, scale.y);
    }

    @JSCodingFunctionOrMethod(description = "Returns the 3D model representing the liquid")
    public JSModel3D getModel() {
        return new JSModel3D(this.liquidObj.getModel());
    }

    @JSCodingFunctionOrMethod(description = "Returns liquid render data")
    public JSLiquidRenderData getRenderLiquidData() {
        return new JSLiquidRenderData(this.liquidObj.getRenderLiquidData());
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying liquid object")
    public JSLiquid getLiquid() {
        return new JSLiquid(this.liquidObj.getLiquid());
    }

    @JSCodingFunctionOrMethod(description = "Spawns the liquid in the world, constructing its model", paramNames = {"world"})
    public void onSpawn(JSSceneWorld world) {
        this.liquidObj.onSpawn(world.getJavaSceneWorld());
    }

    @JSCodingFunctionOrMethod(description = "Destroys the liquid and clears its model", paramNames = {"world"})
    public void onDestroy(JSSceneWorld world) {
        this.liquidObj.onDestroy(world.getJavaSceneWorld());
    }

    @JSCodingFunctionOrMethod(description = "Marks the liquid as dead (no-op for liquids)")
    public void setDead() {
        this.liquidObj.setDead();
    }

    @JSCodingFunctionOrMethod(description = "Checks if the liquid is dead")
    public boolean isDead() {
        return this.liquidObj.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Returns the culling rules for this liquid")
    public JSCullingRules getCullingRules() {
        return new JSCullingRules(this.liquidObj.getCullingRules());
    }

    @JSCodingFunctionOrMethod(description = "Returns the culling AABB for this liquid")
    public JSBoxAABB getCullingData() {
        return new JSBoxAABB(this.liquidObj.getCullingData());
    }
}