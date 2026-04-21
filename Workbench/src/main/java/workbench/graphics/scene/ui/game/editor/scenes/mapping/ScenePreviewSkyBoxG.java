package workbench.graphics.scene.ui.game.editor.scenes.mapping;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.scene.ui.game.editor.utils.AssetsChooseCombo;
import workbench.project.managing.WBenchProjectResourcesManager;
import javagems3d.system.external.gaming.def.misc.GameResourceTextureAsset;

import java.util.concurrent.atomic.AtomicBoolean;

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
            if (ImGui.collapsingHeader("SkyBox: " + skyBoxAssetPreview.getAsset().name(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##skybox_preview", ImGui.getColumnWidth(), 280, true);
                ImGui.indent();
                ImGui.bullet();
                ImGui.text("Face Textures");
                AtomicBoolean save = new AtomicBoolean(false);
                final ICubeMapProgram.CMTextures cmTextures = skyBoxAssetPreview.getAsset().getCmTextures();
                {
                    final GameResourceTextureAsset extractFace = cmTextures.getTextureFRONTPath() == null ? null : WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(cmTextures.getTextureFRONTPath().toString());
                    this.textureFRONT.render(
                            () -> extractFace,
                            (e) -> {
                                cmTextures.setTextureFRONTPath(new JGemsPathSource(e.relativePath(), ISource.Source.OUTSIDE_JAR));
                                save.set(true);
                            },
                            (e) -> {
                                cmTextures.setTextureFRONTPath(null);
                                save.set(true);
                            });
                }
                {
                    final GameResourceTextureAsset extractFace = cmTextures.getTextureBACKPath() == null ? null : WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(cmTextures.getTextureBACKPath().toString());
                    this.textureBACK.render(
                            () -> extractFace,
                            (e) -> {
                                cmTextures.setTextureBACKPath(new JGemsPathSource(e.relativePath(), ISource.Source.OUTSIDE_JAR));
                                save.set(true);
                            },
                            (e) -> {
                                cmTextures.setTextureBACKPath(null);
                                save.set(true);
                            });
                }
                {
                    final GameResourceTextureAsset extractFace = cmTextures.getTextureUPPath() == null ? null : WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(cmTextures.getTextureUPPath().toString());
                    this.textureUP.render(
                            () -> extractFace,
                            (e) -> {
                                cmTextures.setTextureUPPath(new JGemsPathSource(e.relativePath(), ISource.Source.OUTSIDE_JAR));
                                save.set(true);
                            },
                            (e) -> {
                                cmTextures.setTextureUPPath(null);
                                save.set(true);
                            });
                }
                {
                    final GameResourceTextureAsset extractFace = cmTextures.getTextureBOTTOMPath() == null ? null : WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(cmTextures.getTextureBOTTOMPath().toString());
                    this.textureBOTTOM.render(
                            () -> extractFace,
                            (e) -> {
                                cmTextures.setTextureBOTTOMPath(new JGemsPathSource(e.relativePath(), ISource.Source.OUTSIDE_JAR));
                                save.set(true);
                            },
                            (e) -> {
                                cmTextures.setTextureBOTTOMPath(null);
                                save.set(true);
                            });
                }
                {
                    final GameResourceTextureAsset extractFace = cmTextures.getTextureLEFTPath() == null ? null : WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(cmTextures.getTextureLEFTPath().toString());
                    this.textureLEFT.render(
                            () -> extractFace,
                            (e) -> {
                                cmTextures.setTextureLEFTPath(new JGemsPathSource(e.relativePath(), ISource.Source.OUTSIDE_JAR));
                                save.set(true);
                            },
                            (e) -> {
                                cmTextures.setTextureLEFTPath(null);
                                save.set(true);
                            });
                }
                {
                    final GameResourceTextureAsset extractFace = cmTextures.getTextureRIGHTPath() == null ? null : WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(cmTextures.getTextureRIGHTPath().toString());
                    this.textureRIGHT.render(
                            () -> extractFace,
                            (e) -> {
                                cmTextures.setTextureRIGHTPath(new JGemsPathSource(e.relativePath(), ISource.Source.OUTSIDE_JAR));
                                save.set(true);
                            },
                            (e) -> {
                                cmTextures.setTextureRIGHTPath(null);
                                save.set(true);
                            });
                }
                if (ImGui.checkbox("Show Hints", this.showHint)) {
                    this.showHint = !this.showHint;
                }
                ImGui.spacing();
                if (save.get()) {
                    WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.SKYBOXES);
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
