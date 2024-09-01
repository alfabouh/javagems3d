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

package toolbox.render.scene.dear_imgui.content;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;
import toolbox.ToolBox;

public class LoadingContent implements ImGuiContent {
    public LoadingContent() {
    }

    public void drawContent(Vector2i dim, float partialTicks) {
        ImGui.begin(ToolBox.get().toString(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(400, 200);
        ImGui.setWindowPos(dim.x * 0.5f - 200, dim.y * 0.5f - 100);
        ImGui.text("Loading...");
        ImGui.end();
    }
}
