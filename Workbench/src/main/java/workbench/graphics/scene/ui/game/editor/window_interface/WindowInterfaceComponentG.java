package workbench.graphics.scene.ui.game.editor.window_interface;

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
import workbench.graphics.scene.ui.game.editor.resources_interface.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.actions_interface.ActionsInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;
import workbench.graphics.scene.ui.game.editor.window_interface.scenes.ModelPreviewEditorWindow;
import workbench.graphics.scene.ui.game.editor.window_interface.scenes.SkyBoxPreviewEditorWindow;
import workbench.graphics.scene.ui.game.editor.window_interface.scenes.TexturePreviewEditorWindow;

import java.lang.Math;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WindowInterfaceComponentG {
    private final ModelPreviewEditorWindow modelPreviewRenderFunctions;
    private final SkyBoxPreviewEditorWindow skyBoxPreviewRenderFunctions;
    private final TexturePreviewEditorWindow texturePreviewRenderFunctions;

    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final ActionsInterfaceComponentG actionsInterfaceComponentG;
    private final FBOTexture2DProgram modelScenePreview;
    private Vector2f previewRotation;
    private WindowSection<?> currentSection = null;

    public WindowInterfaceComponentG(OpenGLRenderer openGLRenderer, ActionsInterfaceComponentG actionsInterfaceComponentG, ResourcesInterfaceComponentG resourcesInterfaceComponentG, FBOTexture2DProgram modelScenePreview) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.actionsInterfaceComponentG = actionsInterfaceComponentG;
        this.modelScenePreview = modelScenePreview;
        this.texturePreviewRenderFunctions = new TexturePreviewEditorWindow();
        this.modelPreviewRenderFunctions = new ModelPreviewEditorWindow(actionsInterfaceComponentG, openGLRenderer);
        this.skyBoxPreviewRenderFunctions = new SkyBoxPreviewEditorWindow(actionsInterfaceComponentG, openGLRenderer);

        this.reset();
    }

    public void windowContent() {
        WindowSection<?>[] sections = new WindowSection[] {
                new WindowSection<>(WindowSection.SectionType.MODEL, () -> this.resourcesInterfaceComponentG.getModelAssetsTreeDrawer().getPreviewWrapperObject(), this::renderModelPreview, "Model"),
                new WindowSection<>(WindowSection.SectionType.TEXTURE, () -> this.resourcesInterfaceComponentG.getTextureAssetsTreeDrawer().getPreviewWrapperObject(), this::renderTexturePreview, "Texture"),
                new WindowSection<>(WindowSection.SectionType.SKYBOX, () -> this.resourcesInterfaceComponentG.getSkyBoxResourceTreeDrawer().getPreviewWrapperObject(), this::renderSkyBoxPreview, "Skybox")
        };
        List<WindowSection<?>> actualSections = Arrays.stream(sections).filter(WindowSection::check).collect(Collectors.toList());
        if (this.currentSection == null) {
            if (!actualSections.isEmpty()) {
                this.currentSection = actualSections.get(0);
            }
        } else {
            if (!this.currentSection.check()) {
                this.currentSection = null;
            } else {
                this.currentSection.render();
            }
        }

        if (!actualSections.isEmpty()) {
            if (ImGui.beginMenuBar()) {
                for (WindowSection<?> windowSection : actualSections) {
                    if (ImGui.menuItem(windowSection.id, "##" + windowSection.id, this.currentSection != null && this.currentSection.id.equals(windowSection.id))) {
                        this.currentSection = windowSection;
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

    private Triple<Matrix4f, Matrix4f, Matrix4f> prepareMatricesForModelPreview(float inDistScaling) {
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

    private void renderModelPreview(ModelAssetPreview modelAssetPreview) {
        if (ImGui.isWindowHovered() && ImGui.isWindowFocused()) {
            this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling += WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().getScrollVector() * -0.25f;
        }
        final CullingAABB cullingAABB = modelAssetPreview.getAsset().getMeshGroup().getMeshAABBData().getNormalizedAABB(new Pose3D());
        final float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
        final float diagonalOffset = (float) Math.sqrt(diagonal * 8.0f);
        if (this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling < -((diagonalOffset) - 0.1f)) {
            this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling = -((diagonalOffset) - 0.1f);
        }
        final float outScaling = this.actionsInterfaceComponentG.getScenePreviewModelG().getModelPreviewScaling() + diagonalOffset;
        final Triple<Matrix4f, Matrix4f, Matrix4f> preparedMatrices = this.prepareMatricesForModelPreview(outScaling);
        this.modelPreviewRenderFunctions.render(preparedMatrices.getFirst(), preparedMatrices.getSecond(), preparedMatrices.getThird(), cullingAABB, modelAssetPreview, this.modelScenePreview);
        this.renderFBO(this.modelScenePreview);
    }

    private void renderTexturePreview(TextureAssetPreview textureAssetPreview) {
        this.texturePreviewRenderFunctions.render(textureAssetPreview);
    }

    private void renderSkyBoxPreview(SkyBoxAssetPreview skyBoxAssetPreview) {
        final Triple<Matrix4f, Matrix4f, Matrix4f> preparedMatrices = this.prepareMatricesForSkyBoxPreview();
        this.skyBoxPreviewRenderFunctions.render(preparedMatrices.getFirst(), preparedMatrices.getThird(), skyBoxAssetPreview, this.modelScenePreview);
        this.renderFBO(this.modelScenePreview);
    }

    private void renderFBO(FBOTexture2DProgram modelScenePreview) {
        final int sizeX = (int) ImGui.getWindowSizeX();
        final int sizeY = (int) ImGui.getWindowSizeY();
        final int quadSize = Math.min(sizeX, sizeY);
        final float dX = 52.0f;
        final float cursorX = ImGui.getCursorPosX() + (ImGui.getWindowSizeX() / 2.0f - (quadSize - dX) / 2.0f);
        ImGui.setCursorPos(cursorX, ImGui.getCursorPosY());
        ImGui.image(modelScenePreview.getTextureIDByIndex(0), quadSize - dX, quadSize - dX, 0.0f, 1.0f, 1.0f, 0.0f);
    }

    public static class WindowSection<T> {
        private final Supplier<T> object;
        private final Consumer<T> render;
        private final String id;
        private final SectionType sectionType;

        public enum SectionType {
            TEXTURE,
            MODEL,
            SKYBOX
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
