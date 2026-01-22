package workbench.graphics.scene.ui.game.editor.scenes;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.joml.Vector2i;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.TextureAssetPreview;

public class ScenePreviewTextureG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;

    public ScenePreviewTextureG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
    }

    public void render() {
        TextureAssetPreview textureAssetPreview = this.resourcesInterfaceComponentG.getTextureAssetsTreeDrawer().getPreviewWrapperObject();
        if (textureAssetPreview != null) {
            if (ImGui.collapsingHeader(textureAssetPreview.getAsset().getName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.indent();
                ImGui.bullet();
                ImGui.textWrapped(textureAssetPreview.getAsset().getName());
                Vector2i vector2i = textureAssetPreview.getAsset().getTexture2DProgram().getSize();
                ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
            }
        }
    }
}
