package ru.alfabouh.engine.render.scene.immediate_gui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.base.UIAction;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.base.UIInteractiveElement;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.base.font.GuiFont;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.system.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;

public class UIButton extends UIInteractiveElement {
    private final Model<Format2D> buttonModel;
    private final GuiFont guiFont;
    private final UIText uiText;
    private final Vector2i position;
    private final Vector2i size;
    private UIAction onEntered;
    private UIAction onLeft;
    private UIAction onClick;
    private UIAction onUnClick;
    private UIAction onInside;

    public UIButton(@NotNull String text, @NotNull GuiFont guiFont, @NotNull Vector2i position, @NotNull Vector2i size, int textColorHex, float zValue) {
        super(ResourceManager.shaderAssets.gui_button, zValue);
        this.guiFont = guiFont;
        this.position = position;
        this.size = size;

        Vector2i fontOffset = this.getFontPos(text, this.getSize());
        this.uiText = new UIText(text, guiFont, textColorHex, new Vector2i(this.getPosition()).add(fontOffset), zValue);

        this.buttonModel = MeshHelper.generatePlane2DModel(new Vector2f(position), new Vector2f(this.getSize().x, this.getSize().y).add(position.x, position.y), zValue);

        this.onEntered = null;
        this.onLeft = null;
        this.onClick = null;
        this.onUnClick = null;
        this.onInside = null;
    }

    @Override
    public void render(double partialTicks) {
        super.render(partialTicks);

        ShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performProjectionMatrix2d(this.buttonModel);
        shaderManager.performUniform("background_color", new Vector4d(0.25d, 0.0d, 0.15d, 0.8d));
        shaderManager.performUniform("selected", this.isSelected());
        Scene.renderModel(this.buttonModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
        this.uiText.render(partialTicks);
    }

    private Vector2i getFontPos(String text, Vector2i buttonSize) {
        int posX = buttonSize.x / 2 - ImmediateUI.getTextWidth(this.guiFont, text) / 2;
        int posY = buttonSize.y / 2 - ImmediateUI.getFontHeight(this.guiFont) / 2;
        return new Vector2i(posX, posY);
    }

    @Override
    public void cleanData() {
        this.buttonModel.clean();
        this.uiText.cleanData();
    }

    @Override
    public @NotNull Vector2i getSize() {
        return new Vector2i((int) (this.size.x * this.getScaling().x), (int) (this.size.y * this.getScaling().y));
    }

    @Override
    public @NotNull Vector2i getPosition() {
        return this.position;
    }

    @Override
    public int calcUIHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.uiText.hashCode();
        result = prime * result + this.getSize().hashCode();
        return result;
    }

    @Override
    protected void onMouseInside(Vector2d mouseCoordinates) {
        if (this.onInside != null) {
            this.onInside.action();
        }
    }

    @Override
    protected void onMouseEntered() {
        if (this.onEntered != null) {
            this.onEntered.action();
        }
    }

    @Override
    protected void onMouseLeft() {
        if (this.onLeft != null) {
            this.onLeft.action();
        }
    }

    @Override
    protected void onClicked(Vector2d mouseCoordinates) {
        if (this.onClick != null) {
            JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.button, SoundType.SYSTEM, 2.0f, 1.0f);
            this.onClick.action();
        }
    }

    @Override
    protected void onUnClicked(Vector2d mouseCoordinates) {
        if (this.onUnClick != null) {
            this.onUnClick.action();
        }
    }

    public UIButton setOnUnClick(UIAction onUnClick) {
        this.onUnClick = onUnClick;
        return this;
    }

    public UIButton setOnEntered(UIAction onEntered) {
        this.onEntered = onEntered;
        return this;
    }

    public UIButton setOnClick(UIAction onClick) {
        this.onClick = onClick;
        return this;
    }

    public UIButton setOnInside(UIAction onInside) {
        this.onInside = onInside;
        return this;
    }

    public UIButton setOnLeft(UIAction onLeft) {
        this.onLeft = onLeft;
        return this;
    }
}
