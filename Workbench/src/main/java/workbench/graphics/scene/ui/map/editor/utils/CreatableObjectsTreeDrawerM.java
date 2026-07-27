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

package workbench.graphics.scene.ui.map.editor.utils;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.help.JGemsHelper;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.project.map.MapObjectTemplatesFolder;

import java.util.function.Predicate;
import java.util.function.Supplier;

public record CreatableObjectsTreeDrawerM<T extends WBenchObjectTemplate>(MapEditorInterface mapEditorInterface, Supplier<MapObjectTemplatesFolder<T>> folderSupplier, String tab) {
    private void tree(VirtualObjectsFolder<T> folder, boolean root, @Nullable Predicate<T> filter) {
        ImGui.pushID(this.tab + "_" + folder.getName());
        String folderName = root ? "View" : folder.getName();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffffb0b0);
        if (ImGui.treeNodeEx(folderName, ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.popStyleColor();
            for (T t : folder.getObjectsThere()) {
                if (filter != null) {
                    if (!filter.test(t)) {
                        continue;
                    }
                }
                ImGui.pushID(t.name());
                final boolean selected = this.mapEditorInterface().getCurrentSelectedTemplate() != null && this.mapEditorInterface().getCurrentSelectedTemplate().equals(t);
                if (ImGui.selectable("./" + t.name(), selected)) {
                    if (selected) {
                        this.mapEditorInterface().setCurrentSelectedTemplate(null);
                    } else {
                        this.mapEditorInterface().getActionsContent().resetTemplatePreview();
                        this.mapEditorInterface().setCurrentSelectedTemplate(t);
                    }
                }
                ImGui.popID();
            }
            for (VirtualObjectsFolder<T> child : folder.getFoldersThere()) {
                this.tree(child, false, filter);
            }
            ImGui.treePop();
        } else {
            ImGui.popStyleColor();
        }

        ImGui.popID();
    }

    public void render(@Nullable Predicate<T> filter) {
        if (ImGui.collapsingHeader(this.tab(), ImGuiTreeNodeFlags.DefaultOpen)) {
            final float dynHeight = JGemsHelper.math().clamp(this.folderSupplier.get().totalObjectsAndFoldersThere((int) (ImGui.getWindowHeight() / 10.0f)) * 30.0f, 160.0f, ImGui.getWindowHeight() * 0.5f) + 10.0f;
            ImGui.beginChild("##MResChild_" + this.tab, ImGui.getColumnWidth(), dynHeight, true, ImGuiWindowFlags.HorizontalScrollbar);
            this.tree(this.folderSupplier.get(), true, filter);
            ImGui.endChild();
        }
    }
}
