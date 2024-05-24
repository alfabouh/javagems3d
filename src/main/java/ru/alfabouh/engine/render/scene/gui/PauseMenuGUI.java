package ru.alfabouh.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.controller.ControllerDispatcher;
import ru.alfabouh.engine.game.controller.input.MouseKeyboardController;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.ui.ButtonUI;
import ru.alfabouh.engine.render.scene.gui.ui.OptionSliderUI;
import ru.alfabouh.engine.render.scene.gui.ui.TextUI;
import ru.alfabouh.engine.render.screen.window.Window;

public class PauseMenuGUI extends AbstractGUI {
    private final boolean isVisible;
    private ButtonUI playButton;
    private ButtonUI exitButton;
    private ButtonUI settingsButton;

    public PauseMenuGUI() {
        super(null);
        this.isVisible = true;
    }

    @Override
    public void onRender(double partialTicks) {
        Window window = Game.getGame().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowDimensions().x, window.getWindowDimensions().y);
        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0);
        this.renderContent(window, partialTicks, model);
    }

    private void renderContent(Window window, double partialTicks, Model<Format2D> model) {
        ResourceManager.shaderAssets.menu.bind();
        ResourceManager.shaderAssets.menu.performUniform("show_blood", false);
        ResourceManager.shaderAssets.menu.performUniform("texture_blood", 0);
        ResourceManager.shaderAssets.menu.performUniform("w_tick", Game.getGame().getScreen().getRenderTicks());
        ResourceManager.shaderAssets.menu.getUtils().performProjectionMatrix2d(model);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        ResourceManager.renderAssets.blood.bindTexture();
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        ResourceManager.shaderAssets.menu.unBind();

        this.playButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f - 70.0f, 0.5f));
        this.playButton.render(partialTicks);

        this.settingsButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.settingsButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.settingsButton.getSize().y / 2.0f, 0.5f));
        this.settingsButton.render(partialTicks);

        this.exitButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f + 70.0f, 0.5f));
        this.exitButton.render(partialTicks);
    }

    @Override
    public void onStartRender() {
        this.settingsButton = new ButtonUI("Options", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.settingsButton.setOnClick(() -> {
            Game.getGame().showGui(new SettingsMenuGUI(this));
        });

        this.playButton = new ButtonUI("Continue", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.playButton.setOnClick(() -> {
            Game.getGame().unPauseGame();
            Game.getGame().getScreen().getWindow().setInFocus(true);
            Game.getGame().showGui(new InGameGUI());
        });

        this.exitButton = new ButtonUI("Menu", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.exitButton.setOnClick(() -> {
            Game.getGame().getScreen().getScene().getGui().getMainMenuGUI().showBlood = false;
            Game.getGame().getScreen().getScene().getGui().getMainMenuGUI().victory = false;
            Game.getGame().destroyMap();
        });
    }

    @Override
    public void onStopRender() {
        this.playButton.clear();
        this.exitButton.clear();
        this.settingsButton.clear();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
}
