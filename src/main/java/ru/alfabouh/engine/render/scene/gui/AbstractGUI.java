package ru.alfabouh.engine.render.scene.gui;

import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.render.scene.gui.base.GUI;

public abstract class AbstractGUI implements GUI {
    private final GUI oldGui;

    public AbstractGUI(GUI oldGui) {
        this.oldGui = oldGui;
    }

    protected void goBack() {
        Game.getGame().showGui(this.oldGui);
    }
}
