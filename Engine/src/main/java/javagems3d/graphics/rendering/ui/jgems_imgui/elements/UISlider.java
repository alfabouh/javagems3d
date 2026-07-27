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

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.help.JGemsHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIInteractiveElement;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIScalable;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.settings.objects.SettingFloatBar;

public class UISlider extends UIInteractiveElement implements UIScalable {
    private final SettingFloatBar settingFloatBar;
    private final Vector2f position;
    private final String title;
    private final float zValue;
    private final JGemsGuiFont guiFont;
    private final int hexColor;

    public UISlider(IWindow window, @NotNull String text, @NotNull JGemsGuiFont guiFont, int hexColor, @NotNull Vector2f position, @NotNull SettingFloatBar settingFloatBar, float zValue) {
        super(window, null, zValue);
        this.position = position;
        this.title = text;
        this.zValue = zValue;
        this.settingFloatBar = settingFloatBar;
        this.guiFont = guiFont;
        this.hexColor = hexColor;
        this.getAutoScaleMode().SCALE_AFFECT_POS_XY(false);
        this.getAutoScaleMode().SCALE_AFFECT_SIZE(false);
    }

    @Override
    public void render(float frameDeltaTicks) {
        super.render(frameDeltaTicks);
        UIPictureStatic line = new UIPictureStatic(this.getWindow(), JGemsResourceManager.globalTextureAssets.gui1, new Vector2f(this.getPosition()).add(0, (int) (3.0f * this.getScaling().y)), new Vector2f(0.0f, 13.0f), new Vector2f(100.0f, 1.0f), this.getZValue());
        line.getAutoScaleMode().COPY(this.getAutoScaleMode());

        UIPictureStaticSelectable brick = new UIPictureStaticSelectable(this.getWindow(), JGemsResourceManager.globalTextureAssets.gui1, new Vector2f(this.getPosition()).add((int) (this.getOptionValue() * this.getScaling().mul(98).x), 0), new Vector2f(0.0f, 14.0f), new Vector2f(2.0f, 7.0f), this.getZValue());
        brick.getAutoScaleMode().COPY(this.getAutoScaleMode());

        brick.setSelected(this.isSelected());

        line.build();
        brick.build();

        line.setScaling(this.getScaling());
        brick.setScaling(this.getScaling());

        line.render(frameDeltaTicks);
        brick.render(frameDeltaTicks);

        line.clear();
        brick.clear();

        UIText uiText = new UIText(this.getWindow(), this.title + " " + (int) (this.getOptionValue() * 100.0f) + "%", guiFont, this.hexColor, new Vector2f(this.getPosition()).add(this.getScaledSize().x + 30, -3), this.zValue);
        uiText.getAutoScaleMode().SCALE_AFFECT_POS_X(false);
        uiText.getAutoScaleMode().SCALE_AFFECT_POS_Y(true);
        uiText.build();
        uiText.render(frameDeltaTicks);
        uiText.clear();
    }

    @Override
    public void build() {

    }

    @Override
    public void clear() {
    }

    @Override
    public Vector2f getScaling() {
        return super.getScaleAffectedUiVector(super.getScaling()).mul(3.0f);
    }

    @Override
    public @NotNull Vector2f getScaledSize() {
        return this.getOriginalSize().mul(this.getScaling());
    }

    @Override
    public @NotNull Vector2f getOriginalSize() {
        return new Vector2f(100.0f, 7.0f);
    }

    @Override
    public @NotNull Vector2f getPosition() {
        return super.getScaleAffectedUiPos(this.position);
    }

    @Override
    public int calcUIHash() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.hexColor;
        result = prime * result + this.getPosition().hashCode();
        result = prime * result + this.getScaledSize().hashCode();
        result = prime * result + this.title.hashCode();
        result = prime * result + this.guiFont.hashCode();
        result = prime * result + Float.floatToIntBits(this.zValue);
        return result;
    }

    @Override
    protected boolean interruptMouseAfterClick() {
        return false;
    }

    protected boolean handleClickOutsideBorder() {
        return true;
    }

    public SettingFloatBar getSettingFloatBar() {
        return this.settingFloatBar;
    }

    public float getOptionValue() {
        return this.getSettingFloatBar().getValue();
    }

    public void setOptionValue(float value) {
        this.getSettingFloatBar().setValue(value);
    }

    @Override
    protected void onMouseInside(Vector2f mouseCoordinates) {

    }

    @Override
    protected void onMouseEntered() {

    }

    @Override
    protected void onMouseLeft() {

    }

    @Override
    protected void onClicked(Vector2f mouseCoordinates) {
        float value = JGemsHelper.math().clamp((mouseCoordinates.x - this.getPosition().x) / this.getScaledSize().x, 0.0f, 1.0f);
        this.setOptionValue(value);
    }

    @Override
    protected void onReleased(Vector2f mouseCoordinates) {

    }
}
