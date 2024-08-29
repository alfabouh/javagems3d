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

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.AbstractPanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultGamePanel;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultLeaveConfirmationPanel;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultSettingsPanel;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.FBOTexture2DProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.system.map.loaders.custom.DefaultMap;
import ru.jgems3d.engine.system.map.loaders.tbox.TBoxMapLoader;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.helper.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.service.path.JGemsPath;

public class HorrorMainMenuPanel extends AbstractPanelUI {
    private final FBOTexture2DProgram postFbo;

    public HorrorMainMenuPanel(PanelUI panelUI) {
        super(panelUI);
        this.postFbo = new FBOTexture2DProgram(true);
    }

    public static void renderMenuBackGround(Vector3f color) {
        Window window = JGems3D.get().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowDimensions().x, window.getWindowDimensions().y);
        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0);
        JGemsResourceManager.globalShaderAssets.menu.bind();
        JGemsResourceManager.globalShaderAssets.menu.performUniform(new UniformString("color"), color);
        JGemsResourceManager.globalShaderAssets.menu.performUniform(new UniformString("w_tick"), JGems3D.get().getScreen().getRenderTicks());
        JGemsResourceManager.globalShaderAssets.menu.getUtils().performOrthographicMatrix(model);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        JGemsResourceManager.globalShaderAssets.menu.unBind();
        model.clean();
    }

    public void createFBOs(Vector2i dim) {
        this.postFbo.clearFBO();
        this.postFbo.createFrameBuffer2DTexture(dim, new T2DAttachmentContainer(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGB, GL30.GL_RGB), false, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
    }

    public void onWindowResize(Vector2i dim) {
        this.createFBOs(dim);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float frameDeltaTicks) {
        Window window = immediateUI.getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        this.renderContent(immediateUI, window, frameDeltaTicks);

        immediateUI.buttonUI("Play2", JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 100), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.GAME.loadMap(new DefaultMap());
                    JGemsHelper.UI.openUIPanel(new HorrorGamePanel(null));
                });

        immediateUI.buttonUI("Play", JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGemsHelper.GAME.loadMap(TBoxMapLoader.create(new JGemsPath("/assets/horror/map/mansion")));
                    JGemsHelper.UI.openUIPanel(new HorrorGamePanel(null));
                });

        immediateUI.buttonUI(JGems3D.get().I18n("menu.main.settings"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().openUIPanel(new DefaultSettingsPanel(this));
                });

        immediateUI.buttonUI(JGems3D.get().I18n("menu.main.exit"), JGemsResourceManager.globalTextureAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 140), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems3D.get().openUIPanel(new DefaultLeaveConfirmationPanel(this));
                });
    }

    private void renderContent(ImmediateUI immediateUI, Window window, float frameDeltaTicks) {
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        HorrorMainMenuPanel.renderMenuBackGround(new Vector3f(1.0f));
        immediateUI.textUI(JGems3D.get().toString(), JGemsResourceManager.globalTextureAssets.standardFont, new Vector2i(10, windowH - 35), 0x00ff00, 0.5f);
    }

    @Override
    public void onConstruct(ImmediateUI immediateUI) {
        this.createFBOs(JGems3D.get().getScreen().getWindowDimensions());
    }

    @Override
    public void onDestruct(ImmediateUI immediateUI) {
        this.postFbo.clearFBO();
    }
}
