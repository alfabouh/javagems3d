package ru.BouH.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.controller.input.MouseKeyboardController;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.render.scene.gui.base.GUI;
import ru.BouH.engine.render.scene.gui.ui.ButtonUI;
import ru.BouH.engine.render.screen.window.Window;

public class PauseMenuGUI implements GUI {
    private final boolean isVisible;
    private ButtonUI playButton;
    private ButtonUI exitButton;

    public PauseMenuGUI() {
        this.isVisible = true;
    }

    @Override
    public void onRender(double partialTicks) {
        Window window = Game.getGame().getScreen().getWindow();
        this.playButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f, 0.5f));
        this.playButton.render(partialTicks);
        this.exitButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f + 70.0f, 0.5f));
        this.exitButton.render(partialTicks);
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
            Game.getGame().destroyMap();
            Game.getGame().showGui(new MainMenuGUI());
        });
    }

    @Override
    public void onStopRender() {
        this.playButton.clear();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
}
