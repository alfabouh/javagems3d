package ru.BouH.engine.render.scene.gui.base;

import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.render.scene.gui.MainMenuGUI;

public class GameGUI {
    private GUI currentGui;
    private MainMenuGUI mainMenuGUI;
    private GUI oldGui;

    public GameGUI() {
        this.oldGui = null;
        this.currentGui = null;
    }

    public void initMainMenu() {
        this.mainMenuGUI = new MainMenuGUI(false);
    }

    public MainMenuGUI getMainMenuGUI() {
        return this.mainMenuGUI;
    }

    public void showMainMenu() {
        Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.menu, SoundType.SYSTEM, 2.0f, 1.0f);
        Game.getGame().getScreen().zeroRenderTick();
        this.setCurrentGui(this.getMainMenuGUI());
    }

    public void onRender(double partialTicks) {
        if (this.oldGui != null) {
            this.oldGui.onStopRender();
            this.oldGui = null;
        }
        if (this.getCurrentGui() != null) {
            this.getCurrentGui().onRender(partialTicks);
        }
    }

    public GUI getCurrentGui() {
        return this.currentGui;
    }

    public void setCurrentGui(GUI currentGui) {
        if (this.getCurrentGui() != null) {
            if (this.oldGui != null) {
                this.oldGui.onStopRender();
            }
            this.oldGui = this.getCurrentGui();
        }
        if (currentGui != null) {
            currentGui.onStartRender();
        }
        this.currentGui = currentGui;
    }
}
