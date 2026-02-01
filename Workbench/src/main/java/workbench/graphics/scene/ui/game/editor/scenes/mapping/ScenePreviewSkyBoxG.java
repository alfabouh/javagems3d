package workbench.graphics.scene.ui.game.editor.scenes.mapping;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.scene.ui.game.editor.utils.AssetsChooseCombo;
import workbench.project.managing.WBenchGameResourcesManager;
import workbench.project.managing.instances.misc.GameResourceTextureAsset;

public class ScenePreviewSkyBoxG {
    private boolean showHint;
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final AssetsChooseCombo<GameResourceTextureAsset> textureUP;
    private final AssetsChooseCombo<GameResourceTextureAsset> textureBOTTOM;
    private final AssetsChooseCombo<GameResourceTextureAsset> textureFRONT;
    private final AssetsChooseCombo<GameResourceTextureAsset> textureBACK;
    private final AssetsChooseCombo<GameResourceTextureAsset> textureLEFT;
    private final AssetsChooseCombo<GameResourceTextureAsset> textureRIGHT;

    public ScenePreviewSkyBoxG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.textureUP = new AssetsChooseCombo<>("UP Texture", () -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder());
        this.textureBOTTOM = new AssetsChooseCombo<>("BOTTOM Texture",() -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder());
        this.textureFRONT = new AssetsChooseCombo<>("FRONT Texture",() -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder());
        this.textureBACK = new AssetsChooseCombo<>("BACK Texture",() -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder());
        this.textureLEFT = new AssetsChooseCombo<>("LEFT Texture",() -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder());
        this.textureRIGHT = new AssetsChooseCombo<>("RIGHT Texture",() -> WBench.get().getGameProjectManager().getGameResourcesManager().getTextureAssetsFolder());
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
    }

    public void render() {
        SkyBoxAssetPreview skyBoxAssetPreview = this.resourcesInterfaceComponentG.getSkyBoxResourceTreeDrawer().getPreviewWrapperObject();
        if (skyBoxAssetPreview != null) {
            if (ImGui.collapsingHeader("SkyBox: " + skyBoxAssetPreview.getAsset().getName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##skybox_preview", ImGui.getColumnWidth(), 280, true);
                ImGui.indent();
                ImGui.bullet();
                ImGui.text("Face Textures");
                {
                    final GameResourceTextureAsset extractFace = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(skyBoxAssetPreview.getAsset().getTextureFRONTRelativePath());
                    this.textureFRONT.render(
                            () -> extractFace,
                            (e) -> skyBoxAssetPreview.getAsset().setTextureFRONTRelativePath(e.getRelativePath()),
                            (e) -> skyBoxAssetPreview.getAsset().setTextureFRONTRelativePath(null));
                }
                {
                    final GameResourceTextureAsset extractFace = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(skyBoxAssetPreview.getAsset().getTextureBACKRelativePath());
                    this.textureBACK.render(
                            () -> extractFace,
                            (e) -> skyBoxAssetPreview.getAsset().setTextureBACKRelativePath(e.getRelativePath()),
                            (e) -> skyBoxAssetPreview.getAsset().setTextureBACKRelativePath(null));
                }
                {
                    final GameResourceTextureAsset extractFace = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(skyBoxAssetPreview.getAsset().getTextureUPRelativePath());
                    this.textureUP.render(
                            () -> extractFace,
                            (e) -> skyBoxAssetPreview.getAsset().setTextureUPRelativePath(e.getRelativePath()),
                            (e) -> skyBoxAssetPreview.getAsset().setTextureUPRelativePath(null));
                }
                {
                    final GameResourceTextureAsset extractFace = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(skyBoxAssetPreview.getAsset().getTextureBOTTOMRelativePath());
                    this.textureBOTTOM.render(
                            () -> extractFace,
                            (e) -> skyBoxAssetPreview.getAsset().setTextureBOTTOMRelativePath(e.getRelativePath()),
                            (e) -> skyBoxAssetPreview.getAsset().setTextureBOTTOMRelativePath(null));
                }
                {
                    final GameResourceTextureAsset extractFace = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(skyBoxAssetPreview.getAsset().getTextureLEFTRelativePath());
                    this.textureLEFT.render(
                            () -> extractFace,
                            (e) -> skyBoxAssetPreview.getAsset().setTextureLEFTRelativePath(e.getRelativePath()),
                            (e) -> skyBoxAssetPreview.getAsset().setTextureLEFTRelativePath(null));
                }
                {
                    final GameResourceTextureAsset extractFace = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(skyBoxAssetPreview.getAsset().getTextureRIGHTRelativePath());
                    this.textureRIGHT.render(
                            () -> extractFace,
                            (e) -> skyBoxAssetPreview.getAsset().setTextureRIGHTRelativePath(e.getRelativePath()),
                            (e) -> skyBoxAssetPreview.getAsset().setTextureRIGHTRelativePath(null));
                }
                if (ImGui.checkbox("Show Hints", this.showHint)) {
                    this.showHint = !this.showHint;
                }
                ImGui.spacing();
                if (ImGui.button("Save")) {
                    WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.SKYBOXES);
                }
                ImGui.unindent();
                ImGui.endChild();
            }
        }
    }

    public boolean isShowHint() {
        return this.showHint;
    }
}
