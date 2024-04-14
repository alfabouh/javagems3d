package ru.alfabouh.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.audio.sound.GameSound;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.map.Map01;
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
import ru.alfabouh.engine.render.screen.window.Window;

public class MainMenuGUI implements GUI {
    private final boolean isVisible;
    private TextUI gameOver;
    private TextUI gameVictory;
    private TextUI gameVer;
    private final GameSound horror;
    public boolean victory;
    public boolean showBlood;
    private ButtonUI playButton;
    private ButtonUI exitButton;

    private OptionSliderUI soundSliderUI;
    private TextUI soundSliderText;

    public MainMenuGUI(boolean showBlood) {
        this.isVisible = true;
        this.showBlood = showBlood;

        this.horror = Game.getGame().getSoundManager().createSound(ResourceManager.soundAssetsLoader.horror2, SoundType.BACKGROUND_AMBIENT_SOUND, 1.0f, 1.0f, 1.0f);
    }

    public void startMusic() {
        this.horror.playSound();
    }

    public void stopMusic() {
        this.horror.stopSound();
    }

    @Override
    public void onRender(double partialTicks) {
        Window window = Game.getGame().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowDimensions().x, window.getWindowDimensions().y);

        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0);
        ResourceManager.shaderAssets.menu.bind();
        ResourceManager.shaderAssets.menu.performUniform("resolution", res);
        ResourceManager.shaderAssets.menu.performUniform("show_blood", this.showBlood);
        ResourceManager.shaderAssets.menu.performUniform("texture_blood", 0);
        ResourceManager.shaderAssets.menu.performUniform("w_tick", Game.getGame().getScreen().getRenderTicks());
        ResourceManager.shaderAssets.menu.getUtils().performProjectionMatrix2d(model);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        ResourceManager.renderAssets.blood.bindTexture();

        Scene.renderModel(model, GL30.GL_TRIANGLES);
        ResourceManager.shaderAssets.menu.unBind();
        model.clean();

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

        this.playButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f, 0.5f));
        this.playButton.render(partialTicks);
        this.exitButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.playButton.getSize().x / 2.0f, window.getHeight() / 2.0f - this.playButton.getSize().y / 2.0f + 70.0f, 0.5f));
        this.exitButton.render(partialTicks);

        this.gameVer.setText(Game.getGame().toString());
        this.gameVer.setPosition(new Vector3f(10.0f, window.getHeight() - this.gameVer.getTextHeight() - 5.0f, 0.5f));
        this.gameVer.render(partialTicks);

        this.soundSliderUI.render(partialTicks);
        this.soundSliderText.setText("Sound " + ((int) (this.soundSliderUI.getValue() * 100.0f)) + "%");
        this.soundSliderText.render(partialTicks);
    }

    @Override
    public void onStartRender() {
        this.startMusic();
        this.playButton = new ButtonUI("Play", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.playButton.setOnClick(() -> {
            Game.getGame().showGui(new InGameGUI());
            Game.getGame().loadMap(new Map01());
        });
        this.exitButton = new ButtonUI("Exit", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(300.0f, 60.0f));
        this.exitButton.setOnClick(() -> {
            Game.getGame().destroyGame();
        });

        this.gameOver = new TextUI(ResourceManager.renderAssets.standardFont, 0xff3333);
        this.gameVictory = new TextUI(ResourceManager.renderAssets.standardFont, 0x33ff33);
        this.gameVer = new TextUI(Game.GAME_NAME, ResourceManager.renderAssets.standardFont, 0x00ff00);
        this.soundSliderUI = new OptionSliderUI(new Vector3f(20.0f, 20.0f, 0.5f), Game.getGame().getGameSettings().soundGain);
        this.soundSliderText = new TextUI("", ResourceManager.renderAssets.standardFont, new Vector3f(330.0f, 12.0f, 0.5f));
    }

    @Override
    public void onStopRender() {
        this.stopMusic();
        this.playButton.clear();
        this.exitButton.clear();
        this.soundSliderUI.clear();
        this.soundSliderText.clear();
        this.gameOver.clear();
        this.gameVer.clear();
        this.gameVictory.clear();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
}
