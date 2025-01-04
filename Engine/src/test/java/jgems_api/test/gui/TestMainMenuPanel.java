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

package jgems_api.test.gui;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.rendering.JGemsSceneUtils;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.default_panels.DefaultGamePanel;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.default_panels.DefaultLeaveConfirmationPanel;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.default_panels.DefaultSettingsPanel;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.map.loaders.custom.DefaultMap;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import jgems_api.test.map.TestMap;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public class TestMainMenuPanel extends AbstractPanelUI {
    private final FBOTexture2DProgram postFbo;

    public TestMainMenuPanel(PanelUI panelUI) {
        super(panelUI);
        this.postFbo = new FBOTexture2DProgram(true);
    }

    public static void renderMenuBackGround(Vector3f color) {
        Window window = JGems3D.get().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowSize().x, window.getWindowSize().y);
        try (Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0)) {
            JGemsResourceManager.globalShaderAssets.menu.beginShading();
            JGemsResourceManager.globalShaderAssets.menu.performUniform(new UniformString("color"), UniformFunctions.VEC3F(color));
            JGemsResourceManager.globalShaderAssets.menu.performUniform(new UniformString("w_tick"), UniformFunctions.FLOAT(JGems3D.get().getScreen().getRenderTicks()));
            JGemsResourceManager.globalShaderAssets.menu.getUtils().performOrthographicMatrix(model);
            JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
            JGemsResourceManager.globalShaderAssets.menu.endShading();
        }
    }

    public void createFBOs(IWindow window) {
        this.postFbo.clearFBO();
        this.postFbo.createFrameBuffer2DTexture(window.getWindowSize(), new T2DAttachmentContainer(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB), false, GL46.GL_LINEAR, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_BORDER, null);
    }

    public void onWindowResize(IWindow window) {
        this.createFBOs(window);
    }

    @Override
    public void drawPanel(JGemsUI JGemsUI, float frameDeltaTicks) {
        Window window = JGemsUI.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        this.renderContent(JGemsUI, window, frameDeltaTicks);

        JGemsUI.buttonUI("SponzaMap", JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 120), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().loadMap(new TestMap());
                    JGemsHelper.UI.openUIPanel(new DefaultGamePanel(null));
                });

        JGemsUI.buttonUI("DefaultMap", JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().loadMap(new DefaultMap());
                    JGemsHelper.UI.openUIPanel(new DefaultGamePanel(null));
                });

        JGemsUI.buttonUI(JGems3D.get().I18n("menu.main.settings"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().openUIPanel(new DefaultSettingsPanel(this));
                });

        JGemsUI.buttonUI(JGems3D.get().I18n("menu.main.exit"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 140), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().openUIPanel(new DefaultLeaveConfirmationPanel(this));
                });
    }

    private void renderContent(JGemsUI JGemsUI, Window window, float frameDeltaTicks) {
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        TestMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f));
        JGemsUI.textUI(JGems3D.get().toString(), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(10, windowH - 35), 0x00ff00, 0.5f);
    }

    @Override
    public void onConstruct(JGemsUI JGemsUI) {
        this.createFBOs(JGems3D.get().getScreen().getWindow());
    }

    @Override
    public void onDestruct(JGemsUI JGemsUI) {
        this.postFbo.clearFBO();
    }
}
