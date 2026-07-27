/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.ui.jgems_imgui.panels;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.processing.ExternalMapProcessor;
import javagems3d.system.external.mapping.processing.ManualMapProcessor;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import org.joml.Vector2f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.AbstractPanelUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.screen.window.Window;

import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class DefaultMainMenuPanel extends AbstractPanelUI {
    private final FBOTexture2DProgram postFbo;

    public DefaultMainMenuPanel(PanelUI panelUI) {
        super(panelUI);
        this.postFbo = new FBOTexture2DProgram(true, false);
    }

    public static void renderMenuBackGround(Vector3f color) {
        Window window = JGems3D.get().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowSize().x, window.getWindowSize().y);
        try (Model2D model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0)) {
            JGemsResourceManager.globalShaderAssets.menu.beginShading();
            JGemsResourceManager.globalShaderAssets.menu.performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC3F(color));
            JGemsResourceManager.globalShaderAssets.menu.performUniform(new UniformString(DefaultUniformDefinitions.W_TICK), UniformFunctions.FLOAT(JGems3D.get().getScreen().getRenderTicks()));
            JGemsResourceManager.globalShaderAssets.menu.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), model, JGemsTransformManager.INSTANCE.getOrthographicMatrix());
            JGemsHelper.render().renderModel2D(model, GL46.GL_TRIANGLES);
            JGemsResourceManager.globalShaderAssets.menu.endShading();
        }
    }

    public void createFBOs(IWindow window) {
        this.postFbo.clearFBO();
        this.postFbo.createFrameBuffer2DTexture(window.getWindowSize(), new T2DAttachmentContainer(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB), false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_BORDER, null);
    }

    public void onWindowResize(IWindow window) {
        this.createFBOs(window);
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        IWindow window = ui.getWindow();
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        this.renderContent(ui, window, frameDeltaTicks);

        ui.buttonUI("DefaultMap2", JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2f(windowW / 2f - 150, windowH / 2f - 130), new Vector2f(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.map().loadMap(new ManualMapProcessor.DefaultPhysTest());
                    ui.setUiPanel(new DefaultGamePanel(null));
                });

        ui.buttonUI("DefaultMap", JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2f(windowW / 2f - 150, windowH / 2f - 30), new Vector2f(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.map().loadMap(new ExternalMapProcessor.Default(JGemsHelper.map().getMapPath("ArcticDemo"), ExternalMapProcessor.Default.getDefaultPlayerConstructor()));
                    ui.setUiPanel(new DefaultGamePanel(null));
                });

      //.setOnClick(() -> {
      //           JGemsHelper.map().loadMap(new ManualMapProcessor.DefaultPhysTest());
      //           ui.setUiPanel(new DefaultGamePanel(null));
      //       });

        ui.buttonUI(JGems3D.get().I18n("menu.main.settings"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2f(windowW / 2f - 150, windowH / 2f - 30 + 70), new Vector2f(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.ui().openPanel(new DefaultSettingsPanel(this));
                });

        ui.buttonUI(JGems3D.get().I18n("menu.main.exit"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2f(windowW / 2f - 150, windowH / 2f - 30 + 140), new Vector2f(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.ui().openPanel(new DefaultLeaveConfirmationPanel(this));
                });
    }

    @Override
    public String getPanelID() {
        return "default_main_menu_panel";
    }

    private void renderContent(JGemsUI ui, IWindow window, float frameDeltaTicks) {
        int windowW = window.getWindowSize().x;
        int windowH = window.getWindowSize().y;

        DefaultMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f));
        ui.textUI(JGems3D.get().toString(), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2f(10f, windowH - 35f), 0x00ff00, 0.5f);
    }

    @Override
    public void onConstruct(JGemsUI ui) {
        this.createFBOs(JGems3D.get().getScreen().getWindow());
    }

    @Override
    public void onDestruct(JGemsUI ui) {
        this.postFbo.clearFBO();
    }
}
