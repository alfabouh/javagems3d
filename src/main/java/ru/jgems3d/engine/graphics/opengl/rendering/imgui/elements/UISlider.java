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

package ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.UIInteractiveElement;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.UIScalable;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.settings.objects.SettingFloatBar;

public class UISlider extends UIInteractiveElement implements UIScalable {
    private final SettingFloatBar settingFloatBar;
    private final Vector2i position;
    private final String title;
    private final float zValue;
    private final GuiFont guiFont;
    private final int hexColor;

    public UISlider(@NotNull String text, @NotNull GuiFont guiFont, int hexColor, @NotNull Vector2i position, @NotNull SettingFloatBar settingFloatBar, float zValue) {
        super(null, zValue);
        this.position = position;
        this.title = text;
        this.zValue = zValue;
        this.settingFloatBar = settingFloatBar;
        this.guiFont = guiFont;
        this.hexColor = hexColor;
    }

    @Override
    public void render(float frameDeltaTicks) {
        super.render(frameDeltaTicks);
        UIPictureStatic line = new UIPictureStatic(JGemsResourceManager.globalTextureAssets.gui1, new Vector2i(this.getPosition()).add(0, (int) (3.0f * this.getScaling().y)), new Vector2f(0.0f, 13.0f), new Vector2f(100.0f, 1.0f), this.getZValue());
        UIPictureStaticSelectable brick = new UIPictureStaticSelectable(JGemsResourceManager.globalTextureAssets.gui1, new Vector2i(this.getPosition()).add((int) (this.getOptionValue() * this.getScaling().mul(98).x), 0), new Vector2f(0.0f, 14.0f), new Vector2f(2.0f, 7.0f), this.getZValue());
        brick.setSelected(this.isSelected());

        line.buildUI();
        brick.buildUI();

        line.setScaling(this.getScaling());
        brick.setScaling(this.getScaling());

        line.render(frameDeltaTicks);
        brick.render(frameDeltaTicks);

        line.cleanData();
        brick.cleanData();

        UIText uiText = new UIText(this.title + " " + (int) (this.getOptionValue() * 100.0f) + "%", guiFont, this.hexColor, new Vector2i(this.getPosition()).add(this.getSize().x + 30, -3), this.zValue);
        uiText.buildUI();
        uiText.render(frameDeltaTicks);
        uiText.cleanData();
    }

    @Override
    public void buildUI() {

    }

    @Override
    public void cleanData() {
    }

    @Override
    public @NotNull Vector2i getSize() {
        return new Vector2i((int) (100 * this.getScaling().x), (int) (7 * this.getScaling().y));
    }

    @Override
    public @NotNull Vector2i getPosition() {
        return this.position;
    }

    @Override
    public Vector2f getScaling() {
        return super.getScaling().mul(3.0f);
    }

    @Override
    public int calcUIHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.hexColor;
        result = prime * result + this.getPosition().hashCode();
        result = prime * result + this.getSize().hashCode();
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
        float value = JGemsHelper.UTILS.clamp((mouseCoordinates.x - this.getPosition().x) / this.getSize().x, 0.0f, 1.0f);
        this.setOptionValue(value);
    }

    @Override
    protected void onUnClicked(Vector2f mouseCoordinates) {

    }
}
