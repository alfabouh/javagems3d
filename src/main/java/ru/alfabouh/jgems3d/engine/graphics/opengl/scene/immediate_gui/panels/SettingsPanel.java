package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels;

import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.AbstractPanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class SettingsPanel extends AbstractPanelUI {
    public SettingsPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float partialTicks) {
        MainMenuPanel.renderMenuBackGround(false, new Vector3f(1.0f, 0.2f, 1.0f));
        Window window = JGems.get().getScreen().getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        int x = (int) (windowW * 0.5f) - 300;
        int y = (int) (windowH * 0.5f) - 300;

        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.language"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().language, 0.5f);
        immediateUI.settingSliderUI(JGems.get().I18n("menu.settings.sound"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 90), JGems.get().getGameSettings().soundGain, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.vsync"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().vSync, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.window"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().windowMode, 0.5f);

        if (JGems.DEBUG_MODE) {
            immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.anisotropic"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().anisotropic, 0.5f);
            immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.fxaa"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().fxaa, 0.5f);
            immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.ssao"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().ssao, 0.5f);
            immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.bloom"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().bloom, 0.5f);
            immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.textureQ"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().texturesQuality, 0.5f);
            immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.shadowQ"), JGemsResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().shadowQuality, 0.5f);
        }

        immediateUI.buttonUI(JGems.get().I18n("menu.save"), JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - 150, y += 50), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().getGameSettings().saveOptions();
                    JGems.get().getScreen().showGameLoadingScreen();
                    JGems.get().getScreen().tryAddLineInLoadingScreen("Performing settings...");
                    JGems.get().getResourceManager().recreateTexturesInAllCaches();
                    JGems.get().getScreen().reloadSceneAndShadowsFrameBufferObjects();
                    JGems.get().getScreen().checkScreenMode();
                    JGems.get().getScreen().checkVSync();
                    JGems.get().getLocalisation().setCurrentLang(JGems.get().getGameSettings().language.getCurrentLanguage());
                    JGems.get().getScreen().removeLoadingScreen();
                    this.goBack(immediateUI);
                });

        immediateUI.buttonUI(JGems.get().I18n("menu.back"), JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - 150, y + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(immediateUI);
                });
    }
}
