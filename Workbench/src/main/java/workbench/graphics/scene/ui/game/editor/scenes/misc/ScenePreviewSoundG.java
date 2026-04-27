package workbench.graphics.scene.ui.game.editor.scenes.misc;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.audio.sound.data.SoundType;
import org.joml.Vector2i;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.SoundAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.misc.TextureAssetPreview;

public class ScenePreviewSoundG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private float localPitch;
    private float localVolume;

    public ScenePreviewSoundG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.reset();
    }

    public void reset() {
        this.localPitch = 1.0f;
        this.localVolume = 0.5f;
    }

    public void render() {
        SoundAssetPreview soundAssetPreview = this.resourcesInterfaceComponentG.getSoundAssetsTreeDrawer().getPreviewWrapperObject();
        if (soundAssetPreview != null) {
            if (ImGui.collapsingHeader("Sound: " + soundAssetPreview.getAsset().name(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##sound_preview", ImGui.getColumnWidth(), 100, true, ImGuiWindowFlags.HorizontalScrollbar);
                ImGui.indent();
                ImGui.bullet();
                ImGui.textWrapped(soundAssetPreview.getAsset().name());
                {
                    ImGui.setNextItemWidth(200);
                    float[] f1 = new float[] {this.localPitch};
                    if (ImGui.sliderFloat("Pitch", f1, 0.0f, 3.0f)) {
                        this.localPitch = f1[0];
                    }
                }
                {
                    ImGui.setNextItemWidth(200);
                    float[] f1 = new float[] {this.localVolume};
                    if (ImGui.sliderFloat("Volume", f1, 0.0f, 1.0f)) {
                        this.localVolume = f1[0];
                    }
                }
                if (ImGui.button("Play")) {
                    WBench.get().getSoundManager().playLocalSound(soundAssetPreview.getAsset().soundBuffer(), SoundType.SYSTEM, this.localPitch, this.localVolume);
                }
                ImGui.unindent();
                ImGui.endChild();
            }
        }
    }
}
