package javagems3d.graphics.rendering.ui.jgems_imgui.panels;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.help.JGemsHelper;
import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class DefaultPausePanel extends AbstractPanelUI {
    public DefaultPausePanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        DefaultMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f, 0.2f, 1.0f));
        IWindow window = ui.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        ui.buttonUI(JGems3D.get().I18n("menu.pause.continue"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.state().resumeGame();
                    JGems3D.get().getScreen().getWindow().setFocus(true);
                    this.openGamePanel(ui);
                });

        ui.buttonUI(JGems3D.get().I18n("menu.main.settings"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.ui().openPanel(new DefaultSettingsPanel(this));
                });

        ui.buttonUI(JGems3D.get().I18n("menu.main.exit"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 140), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.map().exitMap();
                });
    }

    protected void openGamePanel(JGemsUI ui) {
        ui.setPanel(new DefaultGamePanel(null));
    }
}
