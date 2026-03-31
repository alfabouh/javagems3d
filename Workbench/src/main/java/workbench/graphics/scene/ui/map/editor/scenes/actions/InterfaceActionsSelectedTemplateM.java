package workbench.graphics.scene.ui.map.editor.scenes.actions;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.screen.WBenchScreen;
import workbench.resources.WBenchResourceManager;
import workbench.resources.shaders.WBenchShaderManager;

import java.util.Set;

public class InterfaceActionsSelectedTemplateM {
    private float previewDistance;
    private final MapEditorInterface mapEditorInterface;
    private final FBOTexture2DProgram scenePreview;

    public InterfaceActionsSelectedTemplateM(FBOTexture2DProgram scenePreview, MapEditorInterface mapEditorInterface) {
        this.scenePreview = scenePreview;
        this.mapEditorInterface = mapEditorInterface;
        this.reset();
    }

    public void reset() {
        this.setPreviewDistance(5.0f);
    }

    public void spawnInWorld(WBenchObject<?> wBenchObject, @Nullable Vector3f pos) {
        CullingAABB cullingAABB = wBenchObject.getCullingData();
        if (cullingAABB != null) {
            if (pos != null) {
                wBenchObject.setPosition(pos);
            } else {
                ICamera camera = this.mapEditorInterface.getOpenGLRenderer().getCamera();
                float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
                Vector3f posToSpawn = camera.getCamPosition();
                posToSpawn.add(JGemsHelper.math().calcLookVector(camera.getCamRotation()).mul((diagonal / 2.0f) + 1.0f));
                wBenchObject.setPosition(posToSpawn);
            }
            this.mapEditorInterface.addObjectInWorld(wBenchObject);
        }
    }

    public void renderPreviewItem(@NotNull WBenchObjectTemplate currentSelectedTemplate) {
        OpenGLRenderer.setViewPort(new Vector2i(256, 256));
        this.scenePreview.bindFBO();
        GL46.glClearColor(0.0f, 0.0f, 0.75f, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.renderPreviewItem(this.getPreviewDistance(), WBenchResourceManager.localShaderAssets.preview, currentSelectedTemplate.getMeshGroup());
        this.scenePreview.unBindFBO();
        WBenchScreen.clearColor();
        OpenGLRenderer.setViewPort(this.mapEditorInterface.getOpenGLRenderer().getRenderingResolution());
    }

    private void renderPreviewItem(float distance, WBenchShaderManager shaderManager, MeshGroup meshGroup) {
        float dist = (distance - 5.0f);
        CullingAABB cullingAABB = meshGroup.getMeshAABBData().getNormalizedAABB(new Pose3D());
        float diagonal = (float) Math.sqrt(cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin()));
        dist = Math.min(dist, diagonal);
        diagonal -= dist;
        final Pose3D pose3D = new Pose3D(new Vector3f(0.0f, -diagonal * 0.25f, 0.0f));

        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(TransformUtils.getPerspectiveMatrix(1.0f, (float) (Math.PI / 2.0f), 0.01f, 100.0f)));
        shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), TransformUtils.getModelMatrix(pose3D));
        shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), new Matrix4f().identity().lookAt(new Vector3f(diagonal), new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f)));
        JGemsHelper.render().performEmptyAnimationsInfo(shaderManager);
        for (MeshNode3D<RenderMesh> meshNode3D : meshGroup.getAllNodes()) {
            ITexture2DProgram diffuseMap = meshNode3D.getMaterial().getDiffuseMap();
            ISampleColor4 diffuseColor = meshNode3D.getMaterial().getDiffuseColor();
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.DIFFUSE_COLOR), UniformFunctions.VEC4F(diffuseColor.color()));
            if (diffuseMap != null) {
                shaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.DIFFUSE_MAP), diffuseMap);
                shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.USE_TEXTURE), UniformFunctions.BOOLEAN(true));
            } else {
                shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.USE_TEXTURE), UniformFunctions.BOOLEAN(false));
            }
            GL46.glBindVertexArray(meshNode3D.getMeshData().getVao());
            meshNode3D.getMeshData().enableAllMeshAttributes();
            GL46.glDrawElements(GL46.GL_TRIANGLES, meshNode3D.getMeshData().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            meshNode3D.getMeshData().disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }
        shaderManager.endShading();
    }

    public void render() {
        WBenchObjectTemplate selected = this.mapEditorInterface.getCurrentSelectedTemplate();
        if (selected != null && ImGui.collapsingHeader("Resource: " + selected.name(), ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##insideResourceTmpPreview", ImGui.getColumnWidth(), 480, true, ImGuiWindowFlags.HorizontalScrollbar);
            {
                if (ImGui.beginPopup("GenAtPosition")) {
                    float[] coordinates = new float[]{0.0f, 0.0f, 0.0f};
                    ImGui.inputFloat3("##coords", coordinates);
                    if (ImGui.button("Confirm")) {
                        WBenchObject<?> wBenchObject = selected.createObject(this.mapEditorInterface.getWorld(), null);
                        this.spawnInWorld(wBenchObject, new Vector3f(coordinates));
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Cancel")) {
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }
            }

            if (selected.getMeshGroup() != null) {
                float availableForImage = Math.min(ImGui.getContentRegionAvailX(), 256);
                this.renderPreviewItem(selected);
                ImGui.image(this.mapEditorInterface.getScenePreview().getTextureIDByIndex(0), availableForImage, availableForImage, 0.0f, 1.0f, 1.0f, 0.0f);
                float[] distance = new float[]{this.getPreviewDistance()};
                if (ImGui.sliderFloat("Zoom", distance, 0.0f, 10.0f)) {
                    this.setPreviewDistance(distance[0]);
                }
            } else {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                ImGui.bulletText("Invalid Model!");
                ImGui.popStyleColor();
            }
            ImGui.spacing();
            {
                int i = 0;
                final StringBuilder tagsStringBuilder = new StringBuilder();
                Set<TagID> tagIDSet = selected.getTagsContainer().tags().keySet();
                for (TagID tag : tagIDSet) {
                    tagsStringBuilder.append(tag.getId());
                    if (i++ != tagIDSet.size() - 1) {
                        tagsStringBuilder.append(", ");
                    }
                }
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
                ImGui.bulletText("Data");
                ImGui.popStyleColor();
                ImGui.indent();
                if (selected.getModelDef() == null || selected.getModelDef().equals("null")) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff4444ff);
                    ImGui.textWrapped("Model: NULL");
                    ImGui.popStyleColor();
                } else {
                    ImGui.textWrapped("Model: " + selected.getModelDef());
                }
                ImGui.textWrapped("Tags: (" + tagsStringBuilder + ")");
                ImGui.textWrapped("Translation-Position: " + selected.getTranslationConstraints().positionConstraints());
                ImGui.textWrapped("Translation-Rotation: " + selected.getTranslationConstraints().rotationConstraints());
                ImGui.textWrapped("Translation-Scaling: " + selected.getTranslationConstraints().scalingConstraints());
                ImGui.unindent();
                ImGui.spacing();
            }
            {
                ImGui.bullet();
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
                ImGui.text("Actions");
                ImGui.popStyleColor();
                ImGui.indent();
                if (ImGui.button("Place")) {
                    WBenchObject wBenchObject = selected.createObject(this.mapEditorInterface.getWorld(), null);
                    this.spawnInWorld(wBenchObject, null);
                }
                ImGui.sameLine();
                if (ImGui.button("Place At...")) {
                    ImGui.openPopup("GenAtPosition");
                }
                ImGui.unindent();
            }
            ImGui.endChild();
        }
    }

    public float getPreviewDistance() {
        return this.previewDistance;
    }

    public InterfaceActionsSelectedTemplateM setPreviewDistance(float previewDistance) {
        this.previewDistance = previewDistance;
        return this;
    }
}
