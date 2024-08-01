package ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels;

import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.AbstractPanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class DefaultLeaveConfirmationPanel extends AbstractPanelUI {
    public DefaultLeaveConfirmationPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float partialTicks) {
        DefaultMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f, 0.2f, 1.0f));
        Window window = immediateUI.getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        String text = JGems3D.get().I18n("menu.confirm.text");
        int textSize = ImmediateUI.getTextWidth(JGemsResourceManager.globalTextureAssets.standardFont, text);
        immediateUI.textUI(text, JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 - textSize / 2, windowH / 2 - 60), 0xffffff, 0.5f);

        immediateUI.buttonUI(JGems3D.get().I18n("menu.confirm.yes"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 + 5, windowH / 2), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().destroyGame();
                });
        immediateUI.buttonUI(JGems3D.get().I18n("menu.confirm.no"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 - 205, windowH / 2), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(immediateUI);
                });
    }
}