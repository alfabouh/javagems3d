package ru.alfabouh.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.controller.ControllerDispatcher;
import ru.alfabouh.engine.game.controller.input.MouseKeyboardController;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.ui.ButtonUI;
import ru.alfabouh.engine.render.scene.gui.ui.OptionSliderUI;
import ru.alfabouh.engine.render.scene.gui.ui.TextUI;
import ru.alfabouh.engine.render.screen.window.Window;

public class PauseMenuGUI implements GUI {
    private final boolean isVisible;
    private ButtonUI playButton;
    private ButtonUI exitButton;
    private OptionSliderUI soundSliderUI;
    private TextUI soundSliderText;

    public PauseMenuGUI() {
        this.isVisible = true;
    }

    @Override
    public void onRender(double partialTicks) {
        Window window = Game.getGame().getScreen().getWindow();
        this.playButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f - 40.0f, 0.5f));
        this.playButton.render(partialTicks);
        this.exitButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f + 40.0f, 0.5f));
        this.exitButton.render(partialTicks);

        this.soundSliderUI.render(partialTicks);
        this.soundSliderText.setText("Sound " + ((int) (this.soundSliderUI.getValue() * 100.0f)) + "%");
        this.soundSliderText.render(partialTicks);
    }

    @Override
    public void onStartRender() {
        this.playButton = new ButtonUI("Continue", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.playButton.setOnClick(() -> {
            if (Game.getGame().getScreen().getControllerDispatcher().getCurrentController() instanceof MouseKeyboardController) {
                ControllerDispatcher.mouseKeyboardController.setCursorInCenter();
            }
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

        this.soundSliderUI = new OptionSliderUI(new Vector3f(20.0f, 20.0f, 0.5f), Game.getGame().getGameSettings().soundGain);
        this.soundSliderText = new TextUI("", ResourceManager.renderAssets.standardFont, new Vector3f(330.0f, 12.0f, 0.5f));
    }

    @Override
    public void onStopRender() {
        this.playButton.clear();
        this.exitButton.clear();
        this.soundSliderText.clear();
        this.soundSliderUI.clear();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
}
