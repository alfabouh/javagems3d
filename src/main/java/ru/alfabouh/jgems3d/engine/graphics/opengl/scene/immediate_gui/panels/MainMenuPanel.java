package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.GameSound;
import ru.alfabouh.jgems3d.engine.audio.sound.data.SoundType;
import ru.alfabouh.jgems3d.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.AbstractPanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.system.map.current.MapLoaderTBox;
import ru.alfabouh.jgems3d.engine.system.map.legacy.Map02;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format2D;

public class MainMenuPanel extends AbstractPanelUI {
    public static boolean showBlood = false;
    public static boolean victory = false;
    private final GameSound horror;
    private final FBOTexture2DProgram postFbo;

    public MainMenuPanel(PanelUI panelUI) {
        super(panelUI);
        this.postFbo = new FBOTexture2DProgram(true);
        this.horror = JGems.get().getSoundManager().createSound(JGemsResourceManager.soundAssetsLoader.horror2, SoundType.BACKGROUND_AMBIENT_SOUND, 1.5f, 1.0f, 1.0f);
    }

    public static void renderMenuBackGround(boolean showBlood, Vector3f color) {
        Window window = JGems.get().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowDimensions().x, window.getWindowDimensions().y);
        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0);
        JGemsResourceManager.globalShaderAssets.menu.bind();
        JGemsResourceManager.globalShaderAssets.menu.performUniform("show_blood", showBlood);
        JGemsResourceManager.globalShaderAssets.menu.performUniform("color", color);
        JGemsResourceManager.globalShaderAssets.menu.performUniform("texture_blood", 0);
        JGemsResourceManager.globalShaderAssets.menu.performUniform("w_tick", JGems.get().getScreen().getRenderTicks());
        JGemsResourceManager.globalShaderAssets.menu.getUtils().performOrthographicMatrix(model);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        JGemsResourceManager.renderAssets.blood.bindTexture();
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        JGemsResourceManager.globalShaderAssets.menu.unBind();
        model.clean();
    }

    public void createFBOs(Vector2i dim) {
        this.postFbo.clearFBO();
        FBOTexture2DProgram.FBOTextureInfo[] FBOs = new FBOTexture2DProgram.FBOTextureInfo[]{new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL43.GL_RGB, GL30.GL_RGB)};
        this.postFbo.createFrameBuffer2DTexture(dim, FBOs, false, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
    }

    public void startMusic() {
        this.horror.playSound();
    }

    public void stopMusic() {
        this.horror.stopSound();
    }

    public void onWindowResize(Vector2i dim) {
        this.createFBOs(dim);
    }

    @Override
    public void drawPanel(ImmediateUI immediateUI, float partialTicks) {
        Window window = JGems.get().getScreen().getWindow();
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(windowW, windowH), 0);
        this.postFbo.bindFBO();
        this.renderContent(immediateUI, window, partialTicks);
        this.postFbo.unBindFBO();

        JGemsResourceManager.globalShaderAssets.menu_psx.bind();
        JGemsResourceManager.globalShaderAssets.menu_psx.performUniform("w_tick", JGems.get().getScreen().getRenderTicks());
        JGemsResourceManager.globalShaderAssets.menu_psx.performUniform("texture_sampler", 0);
        JGemsResourceManager.globalShaderAssets.menu_psx.performUniform("offset", JGemsSceneRender.PSX_SCREEN_OFFSET);
        JGemsResourceManager.globalShaderAssets.menu_psx.getUtils().performOrthographicMatrix(model);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.postFbo.bindTexture(0);
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);

        JGemsResourceManager.globalShaderAssets.menu_psx.unBind();
        model.clean();

        immediateUI.buttonUI(MainMenuPanel.showBlood ? JGems.get().I18n("menu.main.retry") : JGems.get().I18n("menu.main.play"), JGemsResourceManager.renderAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().loadMap(new MapLoaderTBox(MapLoaderTBox.readMapFromJar("default_map")));
                    //JGems.get().loadMap(new Map02());
                });

        immediateUI.buttonUI(JGems.get().I18n("menu.main.settings"), JGemsResourceManager.renderAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 70), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().setUIPanel(new SettingsPanel(this));
                });

        immediateUI.buttonUI(JGems.get().I18n("menu.main.exit"), JGemsResourceManager.renderAssets.buttonFont, new Vector2i(windowW / 2 - 150, windowH / 2 - 30 + 140), new Vector2i(300, 60), 0xffffff, 0.5f)
                .setOnClick(() -> {
                    JGems.get().setUIPanel(new LeaveConfirmationPanel(this));
                });
    }

    private void renderContent(ImmediateUI immediateUI, Window window, float partialTicks) {
        int windowW = window.getWindowDimensions().x;
        int windowH = window.getWindowDimensions().y;

        MainMenuPanel.renderMenuBackGround(MainMenuPanel.showBlood, new Vector3f(1.0f));
        immediateUI.textUI(JGems.get().toString(), JGemsResourceManager.renderAssets.standardFont, new Vector2i(10, windowH - 35), 0x00ff00, 0.5f);

        if (MainMenuPanel.showBlood) {
            int seconds = JGems.get().getPhysicsWorld().getTicks() / PhysicThreadManager.TICKS_PER_SECOND;
            String time = (seconds / 60) + "min, " + (seconds % 60) + "sec";
            String gOverText = JGems.get().I18n("menu.main.gameplay.dead", time);

            int tWidth = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, gOverText);
            int tHeight = ImmediateUI.getFontHeight(JGemsResourceManager.renderAssets.standardFont);
            immediateUI.textUI(gOverText, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - tWidth / 2, windowH / 2 - tHeight / 2 - 100), 0xff0000, 0.5f);
        } else if (MainMenuPanel.victory) {
            int seconds = JGems.get().getPhysicsWorld().getTicks() / PhysicThreadManager.TICKS_PER_SECOND;
            String time = (seconds / 60) + "min, " + (seconds % 60) + "sec";
            String gOverText = JGems.get().I18n("menu.main.gameplay.alive", time);

            int tWidth = ImmediateUI.getTextWidth(JGemsResourceManager.renderAssets.standardFont, gOverText);
            int tHeight = ImmediateUI.getFontHeight(JGemsResourceManager.renderAssets.standardFont);
            immediateUI.textUI(gOverText, JGemsResourceManager.renderAssets.standardFont, new Vector2i(windowW / 2 - tWidth / 2, windowH / 2 - tHeight / 2 - 100), 0x00ff00, 0.5f);
        }
    }

    @Override
    public void onConstruct(ImmediateUI immediateUI) {
        JGems.get().getSoundManager().playLocalSound(JGemsResourceManager.soundAssetsLoader.menu, SoundType.SYSTEM, 2.0f, 1.0f);
        this.createFBOs(JGems.get().getScreen().getWindowDimensions());
        this.startMusic();
    }

    @Override
    public void onDestruct(ImmediateUI immediateUI) {
        this.postFbo.clearFBO();
        this.stopMusic();
    }
}
