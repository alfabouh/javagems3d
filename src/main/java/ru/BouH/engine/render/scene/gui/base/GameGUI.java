package ru.BouH.engine.render.scene.gui.base;

public class GameGUI {
    private GUI currentGui;

    public GameGUI() {
        this.currentGui = null;
    }

    public void onRender(double partialTicks) {
        if (this.getCurrentGui() != null) {
            this.getCurrentGui().onRender(partialTicks);
        }
    }

    public GUI getCurrentGui() {
        return this.currentGui;
    }

    public void setCurrentGui(GUI currentGui) {
        if (this.getCurrentGui() != null) {
            this.getCurrentGui().onStopRender();
        }
        if (currentGui != null) {
            currentGui.onStartRender();
        }
        this.currentGui = currentGui;
    }
}
