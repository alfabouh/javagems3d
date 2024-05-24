package ru.alfabouh.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.ui.ButtonUI;
import ru.alfabouh.engine.render.scene.gui.ui.OptionArrowsUI;
import ru.alfabouh.engine.render.scene.gui.ui.OptionSliderUI;
import ru.alfabouh.engine.render.screen.window.Window;

public class SettingsMenuGUI extends AbstractGUI {
    private OptionSliderUI soundSliderUI;
    private OptionArrowsUI shadowQuality;
    private OptionArrowsUI windowMode;
    private OptionArrowsUI vSync;
    private OptionArrowsUI anisotropic;
    private OptionArrowsUI fxaa;
    private OptionArrowsUI texturesQuality;
    private OptionArrowsUI bloom;
    private OptionArrowsUI texturesFiltering;
    private ButtonUI saveSettings;

    public SettingsMenuGUI(GUI gui) {
        super(gui);
    }

    @Override
    public void onRender(double partialTicks) {
        Window window = Game.getGame().getScreen().getWindow();
        float x = window.getWidth() * 0.3f;
        float y = window.getHeight() * 0.2f;

        if (Game.DEBUG_MODE) {
            this.shadowQuality.setPosition(new Vector3f(x, y + 90.0f, 0.5f));
            this.anisotropic.setPosition(new Vector3f(x, y + 120.0f, 0.5f));
            this.fxaa.setPosition(new Vector3f(x, y + 150.0f, 0.5f));
            this.texturesQuality.setPosition(new Vector3f(x, y + 180.0f, 0.5f));
            this.bloom.setPosition(new Vector3f(x, y + 210.0f, 0.5f));
            this.texturesFiltering.setPosition(new Vector3f(x, y + 240.0f, 0.5f));

            this.shadowQuality.render(partialTicks);
            this.anisotropic.render(partialTicks);
            this.fxaa.render(partialTicks);
            this.texturesQuality.render(partialTicks);
            this.bloom.render(partialTicks);
            this.texturesFiltering.render(partialTicks);
        } else {
            y += 100;
        }

        this.soundSliderUI.setPosition(new Vector3f(x, y, 0.5f));
        this.soundSliderUI.render(partialTicks);

        this.windowMode.setPosition(new Vector3f(x, y + 30.0f, 0.5f));
        this.windowMode.render(partialTicks);

        this.vSync.setPosition(new Vector3f(x, y + 60.0f, 0.5f));
        this.vSync.render(partialTicks);

        this.saveSettings.setPosition(new Vector3f(window.getWidth() / 2.0f - this.saveSettings.getSize().x / 2.0f, (Game.DEBUG_MODE ? this.texturesFiltering.getPosition().y : this.vSync.getPosition().y) + 100.0f, 0.5f));
        this.saveSettings.render(partialTicks);
    }

    @Override
    public void onStartRender() {
        this.saveSettings = new ButtonUI("Save", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.saveSettings.setOnClick(() -> {
            Game.getGame().getGameSettings().saveOptions();
            Game.getGame().getScreen().showGameLoadingScreen();
            Game.getGame().getScreen().addLineInLoadingScreen("Performing settings...");
            Game.getGame().getResourceManager().recreateTexturesInCache();
            Game.getGame().getScreen().reloadSceneAndShadowsFrameBufferObjects();
            Game.getGame().getScreen().checkScreenMode();
            Game.getGame().getScreen().checkVSync();
            Game.getGame().getScreen().removeLoadingScreen();
            this.goBack();
        });

        this.soundSliderUI = new OptionSliderUI("Sound", new Vector3f(0.0f), Game.getGame().getGameSettings().soundGain);
        this.shadowQuality = new OptionArrowsUI("Shadow Quality", new Vector3f(0.0f), Game.getGame().getGameSettings().shadowQuality);
        this.windowMode = new OptionArrowsUI("Window Mode", new Vector3f(0.0f), Game.getGame().getGameSettings().windowMode);
        this.vSync = new OptionArrowsUI("V-Sync", new Vector3f(0.0f), Game.getGame().getGameSettings().vSync);
        this.anisotropic = new OptionArrowsUI("Anisotropic Filtering", new Vector3f(0.0f), Game.getGame().getGameSettings().anisotropic);
        this.fxaa = new OptionArrowsUI("FXAA", new Vector3f(0.0f), Game.getGame().getGameSettings().fxaa);
        this.texturesQuality = new OptionArrowsUI("Textures Quality", new Vector3f(0.0f), Game.getGame().getGameSettings().texturesQuality);
        this.bloom = new OptionArrowsUI("Bloom Effect", new Vector3f(0.0f), Game.getGame().getGameSettings().bloom);
        this.texturesFiltering = new OptionArrowsUI("Textures Filtering", new Vector3f(0.0f), Game.getGame().getGameSettings().texturesFiltering);
    }

    @Override
    public void onStopRender() {
        this.saveSettings.clear();
        this.soundSliderUI.clear();
        this.windowMode.clear();
        this.vSync.clear();
        this.anisotropic.clear();
        this.fxaa.clear();
        this.texturesQuality.clear();
        this.bloom.clear();
        this.texturesFiltering.clear();
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
