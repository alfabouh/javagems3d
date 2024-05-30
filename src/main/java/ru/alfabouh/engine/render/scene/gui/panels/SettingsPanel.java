package ru.alfabouh.engine.render.scene.gui.panels;

import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.gui.ImmediateUI;
import ru.alfabouh.engine.render.scene.gui.panels.base.AbstractPanelUI;
import ru.alfabouh.engine.render.scene.gui.panels.base.PanelUI;
import ru.alfabouh.engine.render.screen.window.Window;
import ru.alfabouh.engine.system.resources.ResourceManager;

public class SettingsPanel extends AbstractPanelUI {
    public SettingsPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, double partialTicks) {
        MainMenuPanel.renderMenuBackGround(false, new Vector3f(1.0f, 0.2f, 1.0f));

        Window window = JGems.get().getScreen().getWindow();
        int x = (int) (window.getWidth() * 0.3f);
        int y = (int) (window.getHeight() * 0.5f) - 300;

        immediateUI.settingSliderUI(JGems.get().I18n("menu.settings.sound"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 90), JGems.get().getGameSettings().soundGain, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.anisotropic"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().anisotropic, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.fxaa"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().fxaa, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.bloom"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().bloom, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.window"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().windowMode, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.textureQ"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().texturesQuality, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.shadowQ"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().shadowQuality, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.vsync"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().vSync, 0.5f);
        immediateUI.settingCarouselUI(JGems.get().I18n("menu.settings.language"), ResourceManager.renderAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems.get().getGameSettings().language, 0.5f);

        immediateUI.buttonUI(JGems.get().I18n("menu.save"), ResourceManager.renderAssets.standardFont, new Vector2i(window.getWidth() / 2 - 150, y += 50), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().getGameSettings().saveOptions();
                    JGems.get().getScreen().showGameLoadingScreen();
                    JGems.get().getScreen().addLineInLoadingScreen("Performing settings...");
                    JGems.get().getResourceManager().recreateTexturesInCache();
                    JGems.get().getScreen().reloadSceneAndShadowsFrameBufferObjects();
                    JGems.get().getScreen().checkScreenMode();
                    JGems.get().getScreen().checkVSync();
                    JGems.get().getScreen().removeLoadingScreen();
                    JGems.get().getLocalisation().setCurrentLang(JGems.get().getGameSettings().language.getCurrentLanguage());
                    this.goBack(immediateUI);
                });

        immediateUI.buttonUI(JGems.get().I18n("menu.back"), ResourceManager.renderAssets.standardFont, new Vector2i(window.getWidth() / 2 - 150, y + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(immediateUI);
                });
    }
}
