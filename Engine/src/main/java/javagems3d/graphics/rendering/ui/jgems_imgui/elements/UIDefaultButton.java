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

package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.audio.data.SoundType;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIAction;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIInteractiveElement;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;

import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIDefaultButton extends UIInteractiveElement {
    private final JGemsGuiFont guiFont;
    private final UIText uiText;
    private final Vector2f position;
    private final Vector2f size;
    private Model2D buttonModel;
    private UIAction onEntered;
    private UIAction onLeft;
    private UIAction onClick;
    private UIAction onUnClick;
    private UIAction onInside;

    public UIDefaultButton(IWindow window, @NotNull String text, @NotNull JGemsGuiFont guiFont, @NotNull Vector2f position, @NotNull Vector2f size, int textColorHex, float zValue) {
        super(window, JGemsResourceManager.globalShaderAssets.gui_button, zValue);
        this.guiFont = guiFont;
        this.position = position;
        this.size = size;

        Vector2f fontOffset = this.getFontPos(text, this.getScaledSize());
        this.uiText = new UIText(this.getWindow(), text, guiFont, textColorHex, new Vector2f(this.getPosition()).add(fontOffset), zValue);
        this.uiText.getAutoScaleMode().SCALE_AFFECT_POS_XY(true);

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
        shaderManager.beginShading();
        shaderManager.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX),
                this.buttonModel.getPose(),
                UIElement.getProjection(this.getWindow(), JGemsUI.GET_GLOBAL_UI_SCALING()));
        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.BACKGROUND_COLOR), UniformFunctions.VEC4F(new Vector4f(0.25f, 0.0f, 0.15f, 0.8f)));
        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.SELECTED), UniformFunctions.BOOLEAN(this.isSelected()));
        JGemsHelper.render().renderModel2D(this.buttonModel, GL46.GL_TRIANGLES);
        shaderManager.endShading();
        this.uiText.render(frameDeltaTicks);
    }

    @Override
    public void build() {
        this.buttonModel = MeshHelper.generatePlane2DModel(new Vector2f(0.0f), new Vector2f(this.getScaledSize().x, this.getScaledSize().y), this.getZValue());
        this.buttonModel.getPose().setPosition(new Vector2f(this.getPosition().x, this.getPosition().y));
        this.uiText.build();
    }

    private Vector2f getFontPos(String text, Vector2f buttonSize) {
        final float uiAutoFactor = 1.0f;
        int posX = (int) (buttonSize.x / 2.f - (JGemsUI.getTextWidth(this.guiFont, text) * uiAutoFactor) / 2);
        int posY = (int) (buttonSize.y / 2.f - (JGemsUI.getFontHeight(this.guiFont) * uiAutoFactor) / 2);
        return new Vector2f(posX, posY);
    }

    @Override
    public void clear() {
        this.buttonModel.clear();
        this.uiText.clear();
        this.onEntered = null;
        this.onLeft = null;
        this.onClick = null;
        this.onUnClick = null;
        this.onInside = null;
    }

    @Override
    public @NotNull Vector2f getOriginalSize() {
        return new Vector2f(this.size);
    }

    @Override
    public @NotNull Vector2f getPosition() {
        return super.getScaleAffectedUiPos(this.position);
    }

    @Override
    public Vector2f getScaling() {
        return super.getScaleAffectedUiVector(super.getScaling());
    }

    @Override
    public @NotNull Vector2f getScaledSize() {
        return this.getOriginalSize().mul(this.getScaling());
    }

    @Override
    public int calcUIHash() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.uiText.hashCode();
        result = prime * result + this.getScaledSize().hashCode();
        result = prime * result + this.getPosition().hashCode();
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
    protected void onReleased(Vector2f mouseCoordinates) {
        if (this.onUnClick != null) {
            this.onUnClick.action();
        }
    }

    public UIDefaultButton setOnUnClick(UIAction onUnClick) {
        this.onUnClick = onUnClick;
        return this;
    }

    public UIDefaultButton setOnEntered(UIAction onEntered) {
        this.onEntered = onEntered;
        return this;
    }

    public UIDefaultButton setOnClick(UIAction onClick) {
        this.onClick = onClick;
        return this;
    }

    public UIDefaultButton setOnInside(UIAction onInside) {
        this.onInside = onInside;
        return this;
    }

    public UIDefaultButton setOnLeft(UIAction onLeft) {
        this.onLeft = onLeft;
        return this;
    }
}
