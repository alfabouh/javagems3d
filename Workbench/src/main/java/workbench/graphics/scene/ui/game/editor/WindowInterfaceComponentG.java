/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package workbench.graphics.scene.ui.game.editor;

import api.system.JGemsAPI;
import imgui.ImGui;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.collections.Triple;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;
import workbench.graphics.scene.ui.game.editor.scenes.window.ModelPreviewEditorWindow;
import workbench.graphics.scene.ui.game.editor.scenes.window.SkyBoxPreviewEditorWindow;
import workbench.graphics.scene.ui.game.editor.scenes.window.TexturePreviewEditorWindow;
import workbench.graphics.scene.ui.game.editor.utils.ScriptEditorDrawerG;

import java.lang.Math;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WindowInterfaceComponentG {
    private final ModelPreviewEditorWindow modelPreviewRenderFunctions;
    private final SkyBoxPreviewEditorWindow skyBoxPreviewRenderFunctions;
    private final TexturePreviewEditorWindow texturePreviewRenderFunctions;

    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final ActionsInterfaceComponentG actionsInterfaceComponentG;
    private final FBOTexture2DProgram modelScenePreview;
    private Vector2f previewRotation;
    private static WindowSection<?> currentSection = null;

    private final ScriptEditorDrawerG scenePreviewScriptG;

    public WindowInterfaceComponentG(OpenGLRenderer openGLRenderer, ActionsInterfaceComponentG actionsInterfaceComponentG, ResourcesInterfaceComponentG resourcesInterfaceComponentG, FBOTexture2DProgram modelScenePreview) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.actionsInterfaceComponentG = actionsInterfaceComponentG;
        this.modelScenePreview = modelScenePreview;
        this.texturePreviewRenderFunctions = new TexturePreviewEditorWindow();
        this.modelPreviewRenderFunctions = new ModelPreviewEditorWindow(actionsInterfaceComponentG, openGLRenderer);
        this.skyBoxPreviewRenderFunctions = new SkyBoxPreviewEditorWindow(actionsInterfaceComponentG, openGLRenderer);
        this.scenePreviewScriptG = new ScriptEditorDrawerG(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController(), true);

        this.reset();
    }

    public static void clearSection() {
        WindowInterfaceComponentG.currentSection = null;
    }

    public void windowContent() {
        WindowSection<?>[] sections = new WindowSection[] {
                new WindowSection<>(WindowSection.SectionType.MODEL, () -> this.resourcesInterfaceComponentG.getModelAssetsTreeDrawer().getPreviewWrapperObject(), this::renderModelPreview, "Model"),
                new WindowSection<>(WindowSection.SectionType.TEXTURE, () -> this.resourcesInterfaceComponentG.getTextureAssetsTreeDrawer().getPreviewWrapperObject(), this::renderTexturePreview, "Texture"),
                new WindowSection<>(WindowSection.SectionType.SKYBOX, () -> this.resourcesInterfaceComponentG.getSkyBoxResourceTreeDrawer().getPreviewWrapperObject(), this::renderSkyBoxPreview, "Skybox"),
                new WindowSection<>(WindowSection.SectionType.SCRIPT, () -> this.resourcesInterfaceComponentG.getScriptResourceTreeDrawer().getPreviewWrapperObject(), this::renderScriptCode, "Script")
        };
        List<WindowSection<?>> actualSections = Arrays.stream(sections).filter(WindowSection::check).toList();
        if (WindowInterfaceComponentG.currentSection == null) {
            if (!actualSections.isEmpty()) {
                WindowInterfaceComponentG.currentSection = actualSections.getFirst();
            }
        } else {
            if (!WindowInterfaceComponentG.currentSection.check()) {
                WindowInterfaceComponentG.currentSection = null;
            } else {
                WindowInterfaceComponentG.currentSection.render();
            }
        }

        if (!actualSections.isEmpty()) {
            if (ImGui.beginMenuBar()) {
                for (WindowSection<?> windowSection : actualSections) {
                    if (ImGui.menuItem(windowSection.id, "##" + windowSection.id, WindowInterfaceComponentG.currentSection != null && WindowInterfaceComponentG.currentSection.id.equals(windowSection.id))) {
                        WindowInterfaceComponentG.currentSection = windowSection;
                        this.reset();
                    }
                }
                ImGui.endMenuBar();
            }
        }
    }

    private void reset() {
        this.previewRotation = new Vector2f((float) (Math.PI / 4.0f), (float) (Math.PI / 4.0f));
        this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling = 0.0f;
    }

    private Triple<Matrix4f, Matrix4f, Matrix4f> prepareMatricesForModelPreview(CullingAABB cullingAABB, float inDistScaling) {
        final Pose3D pose = new Pose3D(new Vector3f(0.0f, 0.0f, 0.0f));
        final Vector2f inputRot = WBench.get().getControllerDispatcher().getCurrentController().getNormalizedRotationInput();
        if (this.actionsInterfaceComponentG.getScenePreviewModelG().isFlipModel()) {
            pose.setRotation(new Vector3f(0.0f, 0.0f, (float) Math.PI));
            inputRot.mul(-1, 1);
        } else {
            inputRot.mul(1, -1);
        }

        this.previewRotation.add(inputRot);
        this.previewRotation.x = (float) Math.max(-Math.PI / 2.0f + 0.01f, Math.min(Math.PI / 2.0f - 0.01f, this.previewRotation.x));
        float previewRotationC = this.previewRotation.x;

        float x = (float) (inDistScaling * Math.cos(previewRotationC) * Math.sin(previewRotation.y));
        float y = (float) (inDistScaling * Math.sin(previewRotationC));
        float z = (float) (inDistScaling * Math.cos(previewRotationC) * Math.cos(previewRotation.y));

        final Matrix4f view = new Matrix4f().lookAt(new Vector3f(x, y, z), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        final Matrix4f model = TransformUtils.getModelMatrix(pose);
        final Matrix4f projection = TransformUtils.getPerspectiveMatrix(1.0f, (float) (Math.PI / 2.0f), 0.01f, 1024.0f);

        return new Triple<>(projection, model, view);
    }

    private Triple<Matrix4f, Matrix4f, Matrix4f> prepareMatricesForSkyBoxPreview() {
        final Pose3D pose = new Pose3D(new Vector3f(0.0f, 0.0f, 0.0f));
        final Vector2f inputRot = WBench.get().getControllerDispatcher().getCurrentController().getNormalizedRotationInput();
        inputRot.mul(-1, -1);

        this.previewRotation.add(inputRot);
        this.previewRotation.x = (float) Math.max(-Math.PI / 2.0f + 0.01f, Math.min(Math.PI / 2.0f - 0.01f, this.previewRotation.x));

        final Matrix4f view = new Matrix4f().rotateX(-this.previewRotation.x).rotateY(-this.previewRotation.y);
        final Matrix4f model = TransformUtils.getModelMatrix(pose);
        final Matrix4f projection = TransformUtils.getPerspectiveMatrix(1.0f, (float) (Math.PI / 2.0f), 0.01f, 1024.0f);

        return new Triple<>(projection, model, view);
    }

    private void renderScriptCode(ScriptAssetPreview scriptAssetPreview) {
        this.scenePreviewScriptG.render(scriptAssetPreview);
    }

    private void renderModelPreview(ModelAssetPreview modelAssetPreview) {
        if (ImGui.isWindowHovered() && ImGui.isWindowFocused()) {
            this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling += WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().getScrollVector() * -0.25f;
        }
        CullingAABB cullingAABB = modelAssetPreview.getAsset().meshGroup().getMeshAABBData().getNormalizedAABB(new Pose3D());
        if (modelAssetPreview.isAnimated() && modelAssetPreview.getAnimationData().getCurrentAnimation() != null) {
            cullingAABB = modelAssetPreview.getAsset().meshGroup().getMeshAABBDataForAnimation(modelAssetPreview.getAnimationData().getCurrentAnimation()).getNormalizedAABB(new Pose3D());
        }
        final float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
        final float diagonalOffset = (float) Math.sqrt(diagonal * 8.0f);
        if (this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling < -((diagonalOffset) - 0.1f)) {
            this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling = -((diagonalOffset) - 0.1f);
        }
        final float outScaling = this.actionsInterfaceComponentG.getScenePreviewModelG().getModelPreviewScaling() + diagonalOffset;
        final Triple<Matrix4f, Matrix4f, Matrix4f> preparedMatrices = this.prepareMatricesForModelPreview(cullingAABB, outScaling);
        this.modelPreviewRenderFunctions.render(preparedMatrices.first(), preparedMatrices.second(), preparedMatrices.third(), cullingAABB, modelAssetPreview, this.modelScenePreview);
        this.renderFBO(this.modelScenePreview);
    }

    private void renderTexturePreview(TextureAssetPreview textureAssetPreview) {
        this.texturePreviewRenderFunctions.render(textureAssetPreview);
    }

    private void renderSkyBoxPreview(SkyBoxAssetPreview skyBoxAssetPreview) {
        final Triple<Matrix4f, Matrix4f, Matrix4f> preparedMatrices = this.prepareMatricesForSkyBoxPreview();
        this.skyBoxPreviewRenderFunctions.render(preparedMatrices.first(), preparedMatrices.third(), skyBoxAssetPreview, this.modelScenePreview);
        this.renderFBO(this.modelScenePreview);
    }

    private void renderFBO(FBOTexture2DProgram modelScenePreview) {
        final float sx = ImGui.getWindowSizeX();
        final float sy = ImGui.getWindowSizeY();
        final float square = Math.min(sx - 32, sy - 64);
        ImGui.setCursorPos(sx / 2 - square / 2, sy / 2 - square / 2);
        ImGui.image(modelScenePreview.getTextureIDByIndex(0), square, square, 0.0f, 1.0f, 1.0f, 0.0f);
    }

    public ScriptEditorDrawerG getScenePreviewScriptG() {
        return this.scenePreviewScriptG;
    }

    public static class WindowSection<T> {
        private final Supplier<T> object;
        private final Consumer<T> render;
        private final String id;
        private final SectionType sectionType;

        public enum SectionType {
            TEXTURE,
            MODEL,
            SKYBOX,
            SCRIPT
        }

        public WindowSection(@NotNull SectionType sectionType, Supplier<T> object, Consumer<T> render, String id) {
            this.object = object;
            this.sectionType = sectionType;
            this.render = render;
            this.id = id;
        }

        public void render() {
            if (this.object.get() != null) {
                this.render.accept(this.object.get());
            }
        }

        public SectionType getSectionType() {
            return this.sectionType;
        }

        public boolean check() {
            return this.object.get() != null;
        }

        public Supplier<T> getObject() {
            return this.object;
        }

        public Consumer<T> getRender() {
            return this.render;
        }

        public String getId() {
            return this.id;
        }
    }
}
