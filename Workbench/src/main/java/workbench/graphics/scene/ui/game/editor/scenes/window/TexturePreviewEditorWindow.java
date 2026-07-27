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

package workbench.graphics.scene.ui.game.editor.scenes.window;

import imgui.ImGui;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;

public class TexturePreviewEditorWindow {
    public void render(TextureAssetPreview textureAssetPreview) {
        final float sx = ImGui.getWindowSizeX();
        final float sy = ImGui.getWindowSizeY();
        final float square = Math.min(sx - 32, sy - 64);
        ImGui.setCursorPos(sx / 2 - square / 2, sy / 2 - square / 2);
        ImGui.image(textureAssetPreview.getAsset().texture2DProgram().getTextureId(), square, square, 0.0f, 0.0f, 1.0f, 1.0f);
    }
}