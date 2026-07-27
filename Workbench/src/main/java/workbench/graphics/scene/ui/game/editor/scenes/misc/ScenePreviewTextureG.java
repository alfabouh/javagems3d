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
            if (ImGui.collapsingHeader("Texture: " + textureAssetPreview.getAsset().name(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##texture_preview", ImGui.getColumnWidth(), 60, true);
                ImGui.indent();
                ImGui.bullet();
                ImGui.textWrapped(textureAssetPreview.getAsset().name());
                Vector2i vector2i = textureAssetPreview.getAsset().texture2DProgram().getSize();
                ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
                ImGui.endChild();
            }
        }
    }
}
