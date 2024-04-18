package ru.alfabouh.engine.render.scene.gui.ui;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.game.settings.SettingIntSlots;
import ru.alfabouh.engine.render.scene.Scene;

public class OptionArrowsUI implements BasicUI {
    private final ArrowUI left;
    private final ArrowUI right;
    private final SettingIntSlots settingIntSlots;
    private Vector3f position;
    private final TextUI textUI;
    private boolean visible;
    private final String title;
    private final TextUI titleText;

    public OptionArrowsUI(String title, Vector3f position, SettingIntSlots settingIntSlots) {
        this.left = new ArrowUI(-1, settingIntSlots, position, new Vector2f(4.0f, 8.0f));
        this.right = new ArrowUI(1, settingIntSlots, new Vector3f(position).add(300.0f, 0.0f, 0.0f), new Vector2f(4.0f, 8.0f));
        this.textUI = new TextUI(ResourceManager.renderAssets.buttonFont);
        this.settingIntSlots = settingIntSlots;
        this.visible = true;

        this.title = title;
        this.titleText = new TextUI(ResourceManager.renderAssets.standardFont);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public SettingIntSlots getSettingIntSlots() {
        return this.settingIntSlots;
    }

    public float scaling() {
        return 3.0f;
    }

    public float getValue() {
        return this.getSettingIntSlots().getValue();
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        this.left.setPosition(position);
        this.right.setPosition(new Vector3f(position).add(300.0f, 0.0f, 0.0f));
    }

    @Override
    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }

        float centerX = (this.left.getPosition().x + this.right.getPosition().x + this.right.getSize().x) / 2.0f;
        this.textUI.setText(this.getSettingIntSlots().getCurrentName());
        this.textUI.setPosition(new Vector3f(centerX - this.textUI.getTextWidth() / 2.0f, this.getPosition().y, 0.5f));
        this.textUI.render(partialTicks);

        this.titleText.setText(this.title);
        this.titleText.setPosition(new Vector3f(this.right.getPosition()).add(this.right.getSize().x + 30.0f, 0.0f, 0.0f));
        this.titleText.render(partialTicks);

        this.right.render(partialTicks);
        this.left.render(partialTicks);
    }

    @Override
    public void clear() {
        this.textUI.clear();
        this.left.clear();
        this.right.clear();
        this.titleText.clear();
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public ShaderManager getCurrentShader() {
        return null;
    }

    private class ArrowUI extends InteractiveUI {
        private final ImageSelectableUI imageStaticUI;
        private final SettingIntSlots settingIntSlots;
        private final int vector;

        public ArrowUI(int vector, SettingIntSlots settingIntSlots, Vector3f position, Vector2f size) {
            super(position, size);
            this.imageStaticUI = new ImageSelectableUI(ResourceManager.renderAssets.gui1, new Vector3f(position), new Vector2f(vector == -1 ? 5.0f : 0.0f, 21.0f), new Vector2f(size));
            this.settingIntSlots = settingIntSlots;

            this.vector = vector;
        }

        public SettingIntSlots getSettingIntSlots() {
            return this.settingIntSlots;
        }

        public ImageStaticUI getImageStaticUI() {
            return this.imageStaticUI;
        }

        public void setPosition(Vector3f position) {
            super.setPosition(position);
            this.imageStaticUI.setPosition(position);
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

        public float scaling() {
            return OptionArrowsUI.this.scaling();
        }

        public Vector2f getSize() {
            return super.getSize().mul(this.scaling());
        }

        @Override
        public void onClicked(Vector2d mouseCoordinates) {
            if (this.vector == 1) {
                if (!this.getSettingIntSlots().goRight()) {
                    this.getSettingIntSlots().setValue(0);
                }
            }
            if (this.vector == -1) {
                if (!this.getSettingIntSlots().goLeft()) {
                    this.getSettingIntSlots().setValue(this.getSettingIntSlots().getMax());
                }
            }
            Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.button, SoundType.SYSTEM, 2.0f, 1.0f);
        }

        @Override
        public void onUnClicked(Vector2d mouseCoordinates) {

        }

        @Override
        public void render(double partialTicks) {
            super.render(partialTicks);
            this.getImageStaticUI().setScaling(this.scaling());
            this.getImageStaticUI().render(partialTicks);
        }

        @Override
        public void clear() {
            this.getImageStaticUI().clear();
        }

        @Override
        public ShaderManager getCurrentShader() {
            return ResourceManager.shaderAssets.gui_image_selectable;
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
                this.getCurrentShader().performUniform("selected", ArrowUI.this.isSelected());
                Scene.renderModel(this.getImageModel(), GL30.GL_TRIANGLES);
                shaderManager.unBind();
            }
        }
    }
}
