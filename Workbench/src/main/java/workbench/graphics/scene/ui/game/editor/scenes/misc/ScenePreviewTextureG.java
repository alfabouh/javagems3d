package workbench.graphics.scene.ui.game.editor.scenes.misc;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.joml.Vector2i;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;

public class ScenePreviewTextureG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;

    public ScenePreviewTextureG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
    }

    public void render() {
        TextureAssetPreview textureAssetPreview = this.resourcesInterfaceComponentG.getTextureAssetsTreeDrawer().getPreviewWrapperObject();
        if (textureAssetPreview != null) {
            if (ImGui.collapsingHeader("Texture: " + textureAssetPreview.getAsset().getName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##texture_preview", ImGui.getColumnWidth(), 60, true);
                ImGui.indent();
                ImGui.bullet();
                ImGui.textWrapped(textureAssetPreview.getAsset().getName());
                Vector2i vector2i = textureAssetPreview.getAsset().getTexture2DProgram().getSize();
                ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
                ImGui.endChild();
            }
        }
    }
}
