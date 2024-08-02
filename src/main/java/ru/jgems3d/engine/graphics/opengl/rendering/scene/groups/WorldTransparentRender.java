package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.system.misc.Pair;
import ru.jgems3d.engine.system.misc.Triple;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.*;

public class WorldTransparentRender extends SceneRenderBase {
    private final Set<Triple<Format3D, ModelNode, JGemsShaderManager>> transparentModelModes;
    private final Set<IModeledSceneObject> transparentModelObjects;

    public WorldTransparentRender(JGemsOpenGLRenderer sceneRender) {
        super(99, sceneRender, new RenderGroup("WORLD_TRANSPARENT"));

        this.transparentModelModes = new HashSet<>();
        this.transparentModelObjects = new HashSet<>();
    }

    public void onRender(float partialTicks) {
        Set<IModeledSceneObject> modeledSceneObjects = new HashSet<>(this.transparentModelObjects);
        modeledSceneObjects.addAll(this.getSceneWorld().getFilteredEntityList(RenderPass.TRANSPARENCY));
        for (IModeledSceneObject modeledSceneObject : modeledSceneObjects) {
            this.renderIModeledSceneObject(modeledSceneObject);
        }
        this.transparentModelObjects.clear();
        this.transparentModelModes.clear();
    }

    private void renderIModeledSceneObject(IModeledSceneObject object) {
        Material overMaterial = object.getMeshRenderData().getOverlappingMaterial();
        JGemsShaderManager gemsShaderManager = object.getMeshRenderData().getOverridenTransparencyShader();
        if (gemsShaderManager == null) {
            gemsShaderManager = this.getSceneRenderer().getOITShader();
        }
        gemsShaderManager.bind();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(object.getModel());
        for (ModelNode modelNode : object.getModel().getMeshDataGroup().getModelNodeList()) {
            gemsShaderManager.getUtils().performShadowsInfo();
            gemsShaderManager.getUtils().performModelMaterialOnShader(overMaterial != null ? overMaterial : modelNode.getMaterial());
            gemsShaderManager.performUniform("alpha_factor", modelNode.getMaterial().getFullOpacity() * object.getMeshRenderData().getRenderAttributes().getObjectOpacity());

            boolean f = GL30.glIsEnabled(GL11.GL_CULL_FACE);
            if (object.getMeshRenderData().getRenderAttributes().isDisabledFaceCulling()) {
                GL30.glDisable(GL11.GL_CULL_FACE);
            }
            JGemsSceneUtils.renderModelNode(modelNode);
            if (f) {
                GL30.glEnable(GL11.GL_CULL_FACE);
            }
        }
        gemsShaderManager.unBind();
    }

    private void render(float partialTicks, List<IModeledSceneObject> renderObjects) {
        for (IModeledSceneObject entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    entityItem.getMeshRenderData().getShaderManager().bind();
                    entityItem.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getMeshRenderData().getShaderManager().unBind();
                }
            }
        }
    }

    public void addModelNodeInTransparencyPass(Triple<Format3D, ModelNode, JGemsShaderManager> node) {
        this.transparentModelModes.add(node);
    }

    public void addSceneModelObjectInTransparencyPass(IModeledSceneObject sceneObject) {
        this.transparentModelObjects.add(sceneObject);
    }

    private void clearSets() {
        this.transparentModelModes.clear();
        this.transparentModelObjects.clear();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
        this.clearSets();
    }
}