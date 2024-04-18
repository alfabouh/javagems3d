package ru.alfabouh.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.alfabouh.engine.audio.sound.GameSound;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.map.Map01;
import ru.alfabouh.engine.game.map.Map02;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.ui.ButtonUI;
import ru.alfabouh.engine.render.scene.gui.ui.OptionSliderUI;
import ru.alfabouh.engine.render.scene.gui.ui.TextUI;
import ru.alfabouh.engine.render.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.engine.render.screen.window.Window;

public class MainMenuGUI extends AbstractGUI {
    private final boolean isVisible;
    private TextUI gameOver;
    private TextUI gameVictory;
    private TextUI gameVer;
    private final GameSound horror;
    public boolean victory;
    public boolean showBlood;
    private ButtonUI settingsButton;
    private ButtonUI playButton;
    private ButtonUI exitButton;
    private final FBOTexture2DProgram postFbo;

    public MainMenuGUI(boolean showBlood) {
        super(null);
        this.isVisible = true;
        this.showBlood = showBlood;
        this.postFbo = new FBOTexture2DProgram(true);
        this.horror = Game.getGame().getSoundManager().createSound(ResourceManager.soundAssetsLoader.horror2, SoundType.BACKGROUND_AMBIENT_SOUND, 1.0f, 1.0f, 1.0f);
    }

    public void createFBOs(Vector2i dim) {
        this.postFbo.clearFBO();
        this.postFbo.createFrameBuffer2DTexture(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0}, false, GL43.GL_RGB, GL30.GL_RGB, GL30.GL_LINEAR, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
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
    public void onRender(double partialTicks) {
        Window window = Game.getGame().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowDimensions().x, window.getWindowDimensions().y);
        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0);
        this.postFbo.bindFBO();
        this.renderContent(window, partialTicks, model);
        this.postFbo.unBindFBO();

        ResourceManager.shaderAssets.menu_psx.bind();
        ResourceManager.shaderAssets.menu_psx.performUniform("w_tick", Game.getGame().getScreen().getRenderTicks());
        ResourceManager.shaderAssets.menu_psx.performUniform("texture_sampler", 0);
        ResourceManager.shaderAssets.menu_psx.performUniform("offset", Scene.PSX_SCREEN_OFFSET);
        ResourceManager.shaderAssets.menu_psx.getUtils().performProjectionMatrix2d(model);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        this.postFbo.bindTexture(0);
        Scene.renderModel(model, GL30.GL_TRIANGLES);

        ResourceManager.shaderAssets.menu_psx.unBind();
        model.clean();

        this.playButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f, 0.5f));
        this.playButton.render(partialTicks);

        this.settingsButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.settingsButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.settingsButton.getSize().y / 2.0f + 70.0f, 0.5f));
        this.settingsButton.render(partialTicks);

        this.exitButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f + 140.0f, 0.5f));
        this.exitButton.render(partialTicks);
    }

    private void renderContent(Window window, double partialTicks, Model<Format2D> model) {
        ResourceManager.shaderAssets.menu.bind();
        ResourceManager.shaderAssets.menu.performUniform("show_blood", this.showBlood);
        ResourceManager.shaderAssets.menu.performUniform("texture_blood", 0);
        ResourceManager.shaderAssets.menu.performUniform("w_tick", Game.getGame().getScreen().getRenderTicks());
        ResourceManager.shaderAssets.menu.getUtils().performProjectionMatrix2d(model);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        ResourceManager.renderAssets.blood.bindTexture();

        Scene.renderModel(model, GL30.GL_TRIANGLES);
        ResourceManager.shaderAssets.menu.unBind();

        this.gameVer.setText(Game.getGame().toString());
        this.gameVer.setPosition(new Vector3f(10.0f, window.getHeight() - this.gameVer.getTextHeight() - 5.0f, 0.5f));
        this.gameVer.render(partialTicks);

        if (this.showBlood) {
            int seconds = Game.getGame().getPhysicsWorld().getTicks() / PhysicThreadManager.TICKS_PER_SECOND;
            String time = (seconds / 60) + "m, " + (seconds % 60) + "s";
            this.gameOver.setText("Status: Dead. Recorded time: " + time);
            this.gameOver.setPosition(new Vector3f(window.getWidth() / 2.0f - this.gameOver.getTextWidth() / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f - 100.0f, 0.5f));
            this.gameOver.render(partialTicks);
            this.playButton.setText("Retry");
        } else if (this.victory) {
            int seconds = Game.getGame().getPhysicsWorld().getTicks() / PhysicThreadManager.TICKS_PER_SECOND;
            String time = (seconds / 60) + "m, " + (seconds % 60) + "s";
            this.gameVictory.setText("Status: Alive. Recorded time: " + time);
            this.gameVictory.setPosition(new Vector3f(window.getWidth() / 2.0f - this.gameVictory.getTextWidth() / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f - 100.0f, 0.5f));
            this.gameVictory.render(partialTicks);
            this.playButton.setText("Retry");
        }
    }

    @Override
    public void onStartRender() {
        this.createFBOs(Game.getGame().getScreen().getDimensions());

        this.startMusic();
        this.settingsButton = new ButtonUI("Options", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.settingsButton.setOnClick(() -> {
            Game.getGame().showGui(new SettingsMenuGUI(this));
        });

        this.playButton = new ButtonUI("Play", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.playButton.setOnClick(() -> {
            Game.getGame().loadMap(new Map01());
        });

        this.exitButton = new ButtonUI("Exit", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.exitButton.setOnClick(() -> {
            Game.getGame().destroyGame();
        });

        this.gameOver = new TextUI(ResourceManager.renderAssets.standardFont, 0xff3333);
        this.gameVictory = new TextUI(ResourceManager.renderAssets.standardFont, 0x33ff33);
        this.gameVer = new TextUI(Game.GAME_NAME, ResourceManager.renderAssets.standardFont, 0x00ff00);
    }

    @Override
    public void onStopRender() {
        this.postFbo.clearFBO();

        this.stopMusic();
        this.playButton.clear();
        this.exitButton.clear();
        this.settingsButton.clear();
        this.gameOver.clear();
        this.gameVer.clear();
        this.gameVictory.clear();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
}
