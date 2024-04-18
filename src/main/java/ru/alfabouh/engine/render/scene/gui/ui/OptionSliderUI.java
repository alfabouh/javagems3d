package ru.alfabouh.engine.render.scene.gui.ui;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.game.settings.SettingFloatBar;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.render.scene.Scene;

public class OptionSliderUI extends InteractiveUI {
    private final ImageStaticUI line;
    private final ImageStaticUI brick;
    private final SettingFloatBar settingFloatBar;
    private final String title;
    private final TextUI titleText;

    public OptionSliderUI(String title, Vector3f position, SettingFloatBar settingFloatBar) {
        super(position, new Vector2f(100.0f, 7.0f));
        this.line = new ImageStaticUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f, 13.0f), new Vector2f(100.0f, 1.0f));
        this.brick = new ImageSelectableUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f, 14.0f), new Vector2f(2.0f, 7.0f));
        this.settingFloatBar = settingFloatBar;

        this.title = title;
        this.titleText = new TextUI(ResourceManager.renderAssets.standardFont);
    }

    public SettingFloatBar getSettingFloatBar() {
        return this.settingFloatBar;
    }

    public float scaling() {
        return 3.0f;
    }

    public float getValue() {
        return this.getSettingFloatBar().getValue();
    }

    public void setValue(float value) {
        this.getSettingFloatBar().setValue(value);
    }

    @Override
    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        super.render(partialTicks);

        this.line.setScaling(this.scaling());
        this.brick.setScaling(this.scaling());

        this.line.setPosition(new Vector3f(this.getPosition()).add(0.0f, 8.0f, 0.0f));
        this.brick.setPosition(new Vector3f(this.getPosition()).add(((int) (this.getValue() * (this.scaling() * 98.0f))), 0.0f, 0.0f));

        this.titleText.setText(this.title + " " + (int) (this.getValue() * 100.0f) + "%");
        this.titleText.setPosition(new Vector3f(this.getPosition()).add(new Vector3f(this.getSize().x, -3.0f, 0.0f)).add(30.0f, 0.0f, 0.0f));
        this.titleText.render(partialTicks);

        this.line.render(partialTicks);
        this.brick.render(partialTicks);
    }

    @Override
    protected boolean interruptMouseAfterClick() {
        return false;
    }

    public Vector2f getSize() {
        return super.getSize().mul(this.scaling());
    }

    protected boolean handleClickOutsideBorder() {
        return true;
    }

    @Override
    public void onMouseInside(Vector2d mouseCoordinates) {

    }

    @Override
    public void onMouseEntered() {

    }

    @Override
    public void onMouseLeft() {

    }

    @Override
    public void onClicked(Vector2d mouseCoordinates) {
        float value = MathHelper.clamp((float) (mouseCoordinates.x - this.getPosition().x) / this.getSize().x, 0.0f, 1.0f);
        this.setValue(value);
    }

    @Override
    public void onUnClicked(Vector2d mouseCoordinates) {
    }

    @Override
    public void clear() {
        this.line.clear();
        this.brick.clear();
        this.titleText.clear();
    }

    @Override
    public ShaderManager getCurrentShader() {
        return null;
    }

    private class ImageSelectableUI extends ImageStaticUI {
        public ImageSelectableUI(@NotNull TextureSample image, Vector3f position, Vector2f textureXY, Vector2f textureWH) {
            super(image, position, textureXY, textureWH);
        }

        @Override
        public ShaderManager getCurrentShader() {
            return ResourceManager.shaderAssets.gui_image_selectable;
        }

        @Override
        public void render(double partialTicks) {
            if (!this.isVisible()) {
                return;
            }
            ShaderManager shaderManager = this.getCurrentShader();
            shaderManager.bind();
            Model<Format2D> model = this.getImageModel();
            model.getFormat().setScale(new Vector2d(this.scaling()));
            model.getFormat().setPosition(new Vector2d(this.getPosition().x, this.getPosition().y));
            shaderManager.getUtils().performProjectionMatrix2d(model);
            GL30.glActiveTexture(GL13.GL_TEXTURE0);
            this.getImage().bindTexture();
            shaderManager.performUniform("texture_sampler", 0);
            this.getCurrentShader().performUniform("selected", OptionSliderUI.this.isSelected());
            Scene.renderModel(this.getImageModel(), GL30.GL_TRIANGLES);
            shaderManager.unBind();
        }
    }
}
