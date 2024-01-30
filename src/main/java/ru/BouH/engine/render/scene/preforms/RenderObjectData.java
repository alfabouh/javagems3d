package ru.BouH.engine.render.scene.preforms;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.constraints.ModelRenderConstraints;
import ru.BouH.engine.render.scene.fabric.physics.base.IRenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.lang.reflect.InvocationTargetException;

public class RenderObjectData {
    private final IRenderFabric renderFabric;
    private final Class<? extends PhysicsObject> aClass;
    private final Vector2d modelTextureScaling;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private ShaderManager shaderManager;
    private Material overObjectMaterial;
    private ModelRenderConstraints modelRenderConstraints;

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull ShaderManager shaderManager) {
        this.aClass = aClass;
        this.shaderManager = shaderManager;
        this.renderFabric = renderFabric;
        this.modelTextureScaling = new Vector2d(1.0d);
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.modelRenderConstraints = ModelRenderConstraints.defaultModelRenderConstraints();
    }

    public PhysicsObject constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderObjectData renderObjectData = this.copyObject();
        try {
            return this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderObjectData.class).newInstance(sceneWorld, worldItem, renderObjectData);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector2d getModelTextureScaling() {
        return new Vector2d(this.modelTextureScaling);
    }

    public RenderObjectData setModelTextureScaling(Vector2d scale) {
        this.modelTextureScaling.set(scale);
        return this;
    }

    public IRenderFabric getRenderFabric() {
        return this.renderFabric;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public RenderObjectData setShaderManager(@NotNull ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        return this;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public RenderObjectData setMeshDataGroup(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
        this.entityModelConstructor = null;
        return this;
    }

    public Material getOverObjectMaterial() {
        return this.overObjectMaterial;
    }

    public RenderObjectData setOverObjectMaterial(Material overObjectMaterial) {
        this.overObjectMaterial = overObjectMaterial;
        return this;
    }

    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderObjectData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public ModelRenderConstraints getModelRenderConstraints() {
        return this.modelRenderConstraints;
    }

    public RenderObjectData setModelRenderConstraints(ModelRenderConstraints modelRenderConstraints) {
        this.modelRenderConstraints = modelRenderConstraints;
        return this;
    }

    public Class<? extends PhysicsObject> getRenderClass() {
        return this.aClass;
    }

    protected RenderObjectData copyObject() {
        RenderObjectData renderObjectData = new RenderObjectData(this.getRenderFabric(), this.getRenderClass(), this.getShaderManager());
        renderObjectData.setMeshDataGroup(this.getMeshDataGroup());
        renderObjectData.setModelTextureScaling(this.getModelTextureScaling());
        renderObjectData.setOverObjectMaterial(this.getOverObjectMaterial());
        renderObjectData.setEntityModelConstructor(this.getEntityModelConstructor());
        renderObjectData.setModelRenderConstraints(this.getModelRenderConstraints());
        return renderObjectData;
    }
}
