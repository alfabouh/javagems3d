package workbench.graphics.scene.ui.game.editor.scenes.scripting;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.system.external.gaming.JGemsGameInstance;
import javagems3d.system.service.files.JGemsPath;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ScenePreviewScriptG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;

    public ScenePreviewScriptG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
    }

    public void render() {
        final ScriptAssetPreview previewScriptG = this.resourcesInterfaceComponentG.getScriptResourceTreeDrawer().getPreviewWrapperObject();
        if (previewScriptG != null && previewScriptG.getAsset() != null) {
            if (ImGui.collapsingHeader("Script: " + previewScriptG.getAsset().name(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##script_preview", ImGui.getColumnWidth(), 60, true);
                final File fileG = new JGemsPath(JGemsGameInstance.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), previewScriptG.getAsset().relativePath()).toFile();
                final String modifiedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(Instant.ofEpochMilli(fileG.lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                ImGui.indent();
                ImGui.bulletText(previewScriptG.getAsset().name());
                ImGui.textWrapped("Modified: " + modifiedDate);
                ImGui.unindent();
                ImGui.endChild();
            }
        }
    }
}
