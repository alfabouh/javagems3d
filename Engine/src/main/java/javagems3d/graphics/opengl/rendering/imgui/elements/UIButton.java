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

package javagems3d.graphics.opengl.rendering.imgui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import javagems3d.JGems3D;
import javagems3d.audio.sound.data.SoundType;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.imgui.ImmediateUI;
import javagems3d.graphics.opengl.rendering.imgui.elements.base.UIAction;
import javagems3d.graphics.opengl.rendering.imgui.elements.base.UIInteractiveElement;
import javagems3d.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.base.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.manager.JGemsResourceManager;

public class UIButton extends UIInteractiveElement {
    private final GuiFont guiFont;
    private final UIText uiText;
    private final Vector2i position;
    private final Vector2i size;
    private Model<Format2D> buttonModel;
    private UIAction onEntered;
    private UIAction onLeft;
    private UIAction onClick;
    private UIAction onUnClick;
    private UIAction onInside;

    public UIButton(@NotNull String text, @NotNull GuiFont guiFont, @NotNull Vector2i position, @NotNull Vector2i size, int textColorHex, float zValue) {
        super(JGemsResourceManager.globalShaderAssets.gui_button, zValue);
        this.guiFont = guiFont;
        this.position = position;
        this.size = size;

        Vector2i fontOffset = this.getFontPos(text, this.getSize());
        this.uiText = new UIText(text, guiFont, textColorHex, new Vector2i(this.getPosition()).add(fontOffset), zValue);

        this.onEntered = null;
        this.onLeft = null;
        this.onClick = null;
        this.onUnClick = null;
        this.onInside = null;
    }

    @Override
    public void render(float frameDeltaTicks) {
        super.render(frameDeltaTicks);

        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(this.buttonModel);
        shaderManager.performUniform(new UniformString("background_color"), new Vector4f(0.25f, 0.0f, 0.15f, 0.8f));
        shaderManager.performUniform(new UniformString("selected"), this.isSelected());
        JGemsSceneUtils.renderModel(this.buttonModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
        this.uiText.render(frameDeltaTicks);
    }

    @Override
    public void buildUI() {
        this.buttonModel = MeshHelper.generatePlane2DModel(new Vector2f(position), new Vector2f(this.getSize().x, this.getSize().y).add(position.x, position.y), this.getZValue());
        this.uiText.buildUI();
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
    protected void onMouseInside(Vector2f mouseCoordinates) {
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
    protected void onClicked(Vector2f mouseCoordinates) {
        if (this.onClick != null) {
            JGems3D.get().getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssets.button, SoundType.SYSTEM, 2.0f, 1.0f);
            this.onClick.action();
        }
    }

    @Override
    protected void onUnClicked(Vector2f mouseCoordinates) {
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
