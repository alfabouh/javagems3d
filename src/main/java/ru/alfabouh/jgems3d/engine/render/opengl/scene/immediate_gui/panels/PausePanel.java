package ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.panels;

import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.panels.base.AbstractPanelUI;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;

public class PausePanel extends AbstractPanelUI {
    public PausePanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, double partialTicks) {
        MainMenuPanel.renderMenuBackGround(false, new Vector3f(1.0f, 0.2f, 1.0f));
        Window window = JGems.get().getScreen().getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;
        
        immediateUI.buttonUI(JGems.get().I18n("menu.pause.continue"), ResourceManager.renderAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().unPauseGame();
                    JGems.get().getScreen().getWindow().setInFocus(true);
                    immediateUI.setPanel(new GamePlayPanel(null));
                });

        immediateUI.buttonUI(JGems.get().I18n("menu.main.settings"), ResourceManager.renderAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().setUIPanel(new SettingsPanel(this));
                });

        immediateUI.buttonUI(JGems.get().I18n("menu.main.exit"), ResourceManager.renderAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 140), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    MainMenuPanel.showBlood = false;
                    MainMenuPanel.victory = false;
                    JGems.get().destroyMap();
                });
    }
}