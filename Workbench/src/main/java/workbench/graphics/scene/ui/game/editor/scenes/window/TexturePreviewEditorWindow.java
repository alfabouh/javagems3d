package workbench.graphics.scene.ui.game.editor.scenes.window;

import imgui.ImGui;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;

public class TexturePreviewEditorWindow {
    public void render(TextureAssetPreview textureAssetPreview) {
        final float sx = ImGui.getWindowSizeX();
        final float sy = ImGui.getWindowSizeY();
        final float square = Math.min(sx - 32, sy - 64);
        ImGui.setCursorPos(sx / 2 - square / 2, sy / 2 - square / 2);
        ImGui.image(textureAssetPreview.getAsset().texture2DProgram().getTextureId(), square, square, 1.0f, 0.0f, 0.0f, 1.0f);
    }
}
