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
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.UIElement;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.system.settings.objects.SettingSlot;

public class UICarousel extends UIElement {
    private final UIArrow left;
    private final UIArrow right;
    private final UIText uiText;
    private final UIText uiTitle;
    private final Vector2i position;

    public UICarousel(@NotNull String text, @NotNull GuiFont guiFont, int hexColor, @NotNull Vector2i position, @NotNull SettingSlot settingIntSlots, float zValue) {
        super(null, zValue);
        this.position = position;

        this.left = new UIArrow(-1, settingIntSlots, position, zValue);
        this.right = new UIArrow(1, settingIntSlots, new Vector2i(position.x + this.getSize().x, position.y), zValue);

        this.left.setScaling(this.getScaling());
        this.right.setScaling(this.getScaling());
        this.right.getPosition().sub(this.right.getSize().x, 0);

        int centerX = (this.left.getPosition().x + this.right.getPosition().x + this.right.getSize().x) / 2;
        int textWidth = ImmediateUI.getTextWidth(guiFont, settingIntSlots.getCurrentName());

        this.uiText = new UIText(settingIntSlots.getCurrentName(), guiFont, hexColor, new Vector2i(centerX - textWidth / 2, position.y), zValue);
        this.uiTitle = new UIText(text, guiFont, hexColor, new Vector2i(position.x + this.getSize().x + 30, position.y), zValue);
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.uiText.render(frameDeltaTicks);
        this.uiTitle.render(frameDeltaTicks);
        this.right.render(frameDeltaTicks);
        this.left.render(frameDeltaTicks);
    }

    @Override
    public void buildUI() {
        this.uiText.buildUI();
        this.uiTitle.buildUI();
        this.right.buildUI();
        this.left.buildUI();
    }

    @Override
    public void cleanData() {
        this.left.cleanData();
        this.right.cleanData();
        this.uiText.cleanData();
        this.uiTitle.cleanData();
    }

    @Override
    public Vector2f getScaling() {
        return super.getScaling().mul(3.0f);
    }

    @Override
    public @NotNull Vector2i getSize() {
        return new Vector2i(100, 8).mul((int) this.getScaling().x, (int) this.getScaling().y);
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
        result = prime * result + this.uiTitle.hashCode();
        result = prime * result + this.left.hashCode();
        result = prime * result + this.right.hashCode();
        return result;
    }
}
