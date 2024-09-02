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

package javagems3d.graphics.opengl.rendering.imgui.panels.default_panels;

import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.imgui.ImmediateUI;
import javagems3d.graphics.opengl.rendering.imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import javagems3d.graphics.opengl.screen.window.Window;
import javagems3d.system.resources.manager.JGemsResourceManager;

public class DefaultSettingsPanel extends AbstractPanelUI {
    public DefaultSettingsPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float frameDeltaTicks) {
        DefaultMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f, 0.2f, 1.0f));
        Window window = immediateUI.getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        int x = (int) (windowW * 0.5f) - 300;
        int y = (int) (windowH * 0.5f) - 270;

        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.language"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().language, 0.5f);
        immediateUI.settingSliderUI(JGems3D.get().I18n("menu.settings.sound"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 90), JGems3D.get().getGameSettings().soundGain, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.vsync"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().vSync, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.window"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().windowMode, 0.5f);

        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.anisotropic"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().anisotropic, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.fxaa"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().fxaa, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.ssao"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().ssao, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.bloom"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().bloom, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.textureQ"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().texturesQuality, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.shadowQ"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().shadowQuality, 0.5f);
        immediateUI.settingCarouselUI(JGems3D.get().I18n("menu.settings.filtering"), JGemsResourceManager.globalTextureAssets.standardFont, 0xffffff, new Vector2i(x, y += 30), JGems3D.get().getGameSettings().texturesFiltering, 0.5f);

        immediateUI.buttonUI(JGems3D.get().I18n("menu.save"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 - 150, y += 50), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().getGameSettings().saveOptions();
                    JGemsHelper.RESOURCES.reloadResources();
                    this.goBack(immediateUI);
                });

        immediateUI.buttonUI(JGems3D.get().I18n("menu.back"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 - 150, y + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(immediateUI);
                });
    }
}
