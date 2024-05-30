package ru.alfabouh.engine.render.scene.gui.panels;

import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.gui.ImmediateUI;
import ru.alfabouh.engine.render.scene.gui.panels.base.AbstractPanelUI;
import ru.alfabouh.engine.render.scene.gui.panels.base.PanelUI;
import ru.alfabouh.engine.render.screen.window.Window;
import ru.alfabouh.engine.system.resources.ResourceManager;

public class LeaveConfirmationPanel extends AbstractPanelUI {
    public LeaveConfirmationPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, double partialTicks) {
        MainMenuPanel.renderMenuBackGround(false, new Vector3f(1.0f, 0.2f, 1.0f));
        Window window = JGems.get().getScreen().getWindow();

        String text = JGems.get().I18n("menu.confirm.text");
        int textSize = ImmediateUI.getTextWidth(ResourceManager.renderAssets.standardFont, text);
        immediateUI.textUI(text, ResourceManager.renderAssets.standardFont, new Vector2i(window.getWidth() / 2 - textSize / 2, window.getHeight() / 2 - 60), 0xffffff, 0.5f);

        immediateUI.buttonUI(JGems.get().I18n("menu.confirm.yes"), ResourceManager.renderAssets.standardFont, new Vector2i(window.getWidth() / 2 + 5, window.getHeight() / 2 ), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().destroyGame();
                });
        immediateUI.buttonUI(JGems.get().I18n("menu.confirm.no"), ResourceManager.renderAssets.standardFont, new Vector2i(window.getWidth() / 2 - 205, window.getHeight() / 2 ), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(immediateUI);
                });
    }
}
