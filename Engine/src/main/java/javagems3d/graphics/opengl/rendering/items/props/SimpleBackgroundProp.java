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

package javagems3d.graphics.opengl.rendering.items.props;

import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.environment.light.Light;
import javagems3d.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import javagems3d.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.data.render.MeshRenderData;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleBackgroundProp implements IModeledSceneObject {
    private final IRenderObjectFabric renderFabric;
    private Model<Format3D> model;
    private MeshRenderData meshRenderData;

    public SimpleBackgroundProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull MeshRenderData meshRenderData) {
        this.renderFabric = renderFabric;
        this.model = model;
        this.meshRenderData = meshRenderData;
    }

    public SimpleBackgroundProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, model, MeshRenderData.defaultMeshRenderData(shaderManager));
    }

    public void clearLights() {
    }

    public void addLight(Light light) {
    }

    public void removeLight(Light light) {
    }

    @Override
    public RenderAABB getRenderAABB() {
        if (!this.getModel().isValid()) {
            return null;
        }
        return JGemsHelper.UTILS.calcRenderAABBWithTransforms(this.getModel());
    }

    public SimpleBackgroundProp setModelRenderConstraints(MeshRenderData meshRenderData) {
        this.meshRenderData = meshRenderData;
        return this;
    }

    public boolean canBeCulled() {
        return true;
    }

    @Override
    public List<Light> getLightsList() {
        return null;
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public SimpleBackgroundProp setModel(Model<Format3D> model) {
        this.model = model;
        return this;
    }

    public MeshRenderData getMeshRenderData() {
        return this.meshRenderData;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public IRenderObjectFabric renderFabric() {
        return this.renderFabric;
    }

    @Override
    public boolean hasRender() {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + this.getModel().getFormat().getPosition();
    }
}