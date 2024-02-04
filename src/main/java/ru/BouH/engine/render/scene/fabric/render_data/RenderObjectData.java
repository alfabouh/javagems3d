package ru.BouH.engine.render.scene.fabric.render_data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObjectModeled;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.lang.reflect.InvocationTargetException;

public class RenderObjectData {
    private final IRenderFabric renderFabric;
    private final Class<? extends PhysicsObjectModeled> aClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private Material overObjectMaterial;
    private ModelRenderParams modelRenderParams;

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObjectModeled> aClass, @NotNull ShaderManager shaderManager) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.modelRenderParams = ModelRenderParams.defaultModelRenderConstraints(shaderManager);
    }

    public PhysicsObjectModeled constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderObjectData renderObjectData = this.copyObject();
        try {
            return this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderObjectData.class).newInstance(sceneWorld, worldItem, renderObjectData);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector2d getModelTextureScaling() {
        return this.getModelRenderParams().getTextureScaling();
    }

    public RenderObjectData setModelTextureScaling(Vector2d scale) {
        this.getModelRenderParams().setTextureScaling(scale);
        return this;
    }

    public IRenderFabric getRenderFabric() {
        return this.renderFabric;
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

    public ModelRenderParams getModelRenderParams() {
        return this.modelRenderParams;
    }

    public RenderObjectData setModelRenderParams(ModelRenderParams modelRenderParams) {
        this.modelRenderParams = modelRenderParams;
        return this;
    }

    public Class<? extends PhysicsObjectModeled> getRenderClass() {
        return this.aClass;
    }

    protected RenderObjectData copyObject() {
        RenderObjectData renderObjectData = new RenderObjectData(this.getRenderFabric(), this.getRenderClass(), this.getModelRenderParams().getShaderManager());
        renderObjectData.setMeshDataGroup(this.getMeshDataGroup());
        renderObjectData.setModelTextureScaling(this.getModelTextureScaling());
        renderObjectData.setOverObjectMaterial(this.getOverObjectMaterial());
        renderObjectData.setEntityModelConstructor(this.getEntityModelConstructor());
        renderObjectData.setModelRenderParams(this.getModelRenderParams().copy());
        return renderObjectData;
    }
}
