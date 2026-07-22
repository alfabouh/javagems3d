package javagems3d.graphics.rendering.ui.jgems_imgui.panels;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.processing.ExternalMapProcessor;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class DefaultSettingsPanel extends AbstractPanelUI {
    public DefaultSettingsPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        DefaultMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f, 0.2f, 1.0f));
        IWindow window = ui.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        int x = (int) (windowW * 0.5f) - 300;
        int y = (int) (windowH * 0.5f) - 270;

        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.language"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().language, 0.5f);
        ui.settingSliderUI(JGems3D.get().I18n("menu.settings.sound"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 90), JGems3D.get().getGameSettings().soundGain, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.vsync"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().vSync, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.window"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().windowMode, 0.5f);

        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.anisotropic"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().anisotropic, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.fxaa"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().fxaa, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.ssao"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().ssao, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.bloom"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().bloom, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.textureQ"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().texturesQuality, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.shadowQ"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().shadowQuality, 0.5f);
        ui.settingCarouselUI(JGems3D.get().I18n("menu.settings.filtering"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2f(x, y += 30), JGems3D.get().getGameSettings().texturesFiltering, 0.5f);

        ui.buttonUI(JGems3D.get().I18n("menu.save"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2f(windowW / 2f - 150, y += 50), new Vector2f(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().getGameSettings().saveOptions();
                    JGemsHelper.resources().reloadResources();
                    this.goBack(ui);
                });

        ui.buttonUI(JGems3D.get().I18n("menu.back"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2f(windowW / 2f - 150, y + 70), new Vector2f(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(ui);
                });
    }

    @Override
    public String getPanelID() {
        return "default_settings_panel";
    }
}
