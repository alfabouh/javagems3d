package ru.alfabouh.engine.render.scene.immediate_gui.panels.base;

import org.joml.Vector2i;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.immediate_gui.ImmediateUI;

public abstract class AbstractPanelUI implements PanelUI {
    private final PanelUI prevPanel;

    public AbstractPanelUI(PanelUI prevPanel) {
        this.prevPanel = prevPanel;
    }

    @Override
    public void onConstruct(ImmediateUI immediateUI) {

    }

    @Override
    public void onDestruct(ImmediateUI immediateUI) {

    }

    @Override
    public void onWindowResize(Vector2i dim) {

    }

    public void closePanel(ImmediateUI immediateUI) {
        immediateUI.removePanel();
    }

    public void goBack(ImmediateUI immediateUI) {
        if (immediateUI != null) {
            immediateUI.setPanel(this.prevPanel);
        } else {
            JGems.get().getLogManager().warn("Couldn't go back to NULL UI panel!");
        }
    }
}
