package ru.alfabouh.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.ui.ButtonUI;
import ru.alfabouh.engine.render.scene.gui.ui.TextUI;
import ru.alfabouh.engine.render.screen.window.Window;

public class LeaveConfirmationGUI extends AbstractGUI {
    private final boolean isVisible;
    private ButtonUI yesButton;
    private ButtonUI noButton;
    private TextUI textUI;

    public LeaveConfirmationGUI(GUI gui) {
        super(gui);
        this.isVisible = true;
    }

    @Override
    public void onRender(double partialTicks) {
        Window window = Game.getGame().getScreen().getWindow();
        Vector2f res = new Vector2f(window.getWindowDimensions().x, window.getWindowDimensions().y);
        Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), res, 0);
        this.renderContent(window, partialTicks, model);
    }

    private void renderContent(Window window, double partialTicks, Model<Format2D> model) {
        ResourceManager.shaderAssets.menu.bind();
        ResourceManager.shaderAssets.menu.performUniform("show_blood", false);
        ResourceManager.shaderAssets.menu.performUniform("texture_blood", 0);
        ResourceManager.shaderAssets.menu.performUniform("w_tick", Game.getGame().getScreen().getRenderTicks());
        ResourceManager.shaderAssets.menu.getUtils().performProjectionMatrix2d(model);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        ResourceManager.renderAssets.blood.bindTexture();
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        ResourceManager.shaderAssets.menu.unBind();

        this.noButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.noButton.getSize().x / 2.0f + 110.0f, window.getHeight() / 2.0f, 0.5f));
        this.yesButton.setPosition(new Vector3f(window.getWidth() / 2.0f - this.yesButton.getSize().x / 2.0f - 110.0f, window.getHeight() / 2.0f, 0.5f));
        this.textUI.setPosition(new Vector3f(window.getWidth() / 2.0f - this.textUI.getTextWidth() / 2.0f, window.getHeight() / 2.0f - 60.0f, 0.5f));

        this.textUI.render(partialTicks);
        this.yesButton.render(partialTicks);
        this.noButton.render(partialTicks);
    }

    @Override
    public void onStartRender() {
        Window window = Game.getGame().getScreen().getWindow();
        this.textUI = new TextUI("You really want to leave the game?", ResourceManager.renderAssets.buttonFont);

        this.yesButton = new ButtonUI("Yes", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f), new Vector2f(200.0f, 60.0f));

        this.yesButton.setOnClick(() -> {
            Game.getGame().destroyGame();
        });

        this.noButton = new ButtonUI("No", ResourceManager.renderAssets.buttonFont, new Vector3f(0.0f), new Vector2f(200.0f, 60.0f));
        this.noButton.setOnClick(this::goBack);
    }

    @Override
    public void onStopRender() {
        this.yesButton.clear();
        this.noButton.clear();
        this.textUI.clear();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
}
