package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels;

import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.AbstractPanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class LeaveConfirmationPanel extends AbstractPanelUI {
    public LeaveConfirmationPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float partialTicks) {
        MainMenuPanel.renderMenuBackGround(false, new Vector3f(1.0f, 0.2f, 1.0f));
        Window window = JGems.get().getScreen().getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        String text = JGems.get().I18n("menu.confirm.text");
        int textSize = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, text);
        immediateUI.textUI(text, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - textSize / 2, windowH / 2 - 60), 0xffffff, 0.5f);

        immediateUI.buttonUI(JGems.get().I18n("menu.confirm.yes"), JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 + 5, windowH / 2), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().destroyGame();
                });
        immediateUI.buttonUI(JGems.get().I18n("menu.confirm.no"), JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - 205, windowH / 2), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(immediateUI);
                });
    }
}
