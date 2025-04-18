package javagems3d.graphics.rendering.ui.jgems_imgui.panels;

import javagems3d.graphics.screen.window.IWindow;
import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class DefaultLeaveConfirmationPanel extends AbstractPanelUI {
    public DefaultLeaveConfirmationPanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        DefaultMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f, 0.2f, 1.0f));
        IWindow window = ui.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        String text = JGems3D.get().I18n("menu.confirm.text");
        int textSize = JGemsUI.getTextWidth(JGemsResourceManager.globalTextureAssets.standardFont, text);
        ui.textUI(text, JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 - textSize / 2, windowH / 2 - 60), 0xffffff, 0.5f);

        ui.buttonUI(JGems3D.get().I18n("menu.confirm.yes"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 + 5, windowH / 2), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.close(null);
                });
        ui.buttonUI(JGems3D.get().I18n("menu.confirm.no"), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW / 2 - 205, windowH / 2), new Vector2i(200, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    this.goBack(ui);
                });
    }
}
