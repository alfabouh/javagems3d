/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package jgems_api.horror.gui;

import jgems_api.horror.HorrorGame;
import jgems_api.horror.HorrorGamePlayerState;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultGamePanel;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class HorrorGamePanel extends DefaultGamePanel {
    public HorrorGamePanel(PanelUI prevPanel) {
        super(prevPanel);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float frameDeltaTicks) {
        super.drawPanel(immediateUI, frameDeltaTicks);
        this.renderBrains(immediateUI);
        this.renderGas(immediateUI);
    }

    private void renderBrains(ImmediateUI immediateUI) {
        Window window = immediateUI.getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        TextureSample sample = HorrorGame.get().horrorTexturesLoader.brains;
        if (sample == null) {
            sample = TextureAssetsLoader.DEFAULT;
        }
        immediateUI.imageUI(sample, new Vector2i(windowW - 104, 120), new Vector2i(96), 0.5f);

        String text = HorrorGamePlayerState.brainsCollected + " / " + HorrorGamePlayerState.MAX_BRAINS + " Brains";
        immediateUI.textUI(text, JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW - 120 - ImmediateUI.getTextWidth(JGemsResourceManager.globalTextureAssets.standardFont, text), 150), 0xffff00, 0.51f);
    }

    private void renderGas(ImmediateUI immediateUI) {
        Window window = immediateUI.getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        TextureSample sample = HorrorGame.get().horrorTexturesLoader.gas;
        if (sample == null) {
            sample = TextureAssetsLoader.DEFAULT;
        }
        immediateUI.imageUI(sample, new Vector2i(windowW - 104, 200), new Vector2i(96), 0.5f);

        String text = ((int) (HorrorGamePlayerState.zippoFluid * 100)) + "% Gas Left";
        immediateUI.textUI(text, JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(windowW - 120 - ImmediateUI.getTextWidth(JGemsResourceManager.globalTextureAssets.standardFont, text), 230), 0xffff00, 0.51f);
    }
}
