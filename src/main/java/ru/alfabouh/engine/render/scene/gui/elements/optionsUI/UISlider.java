package ru.alfabouh.engine.render.scene.gui.elements.optionsUI;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.render.scene.gui.ImmediateUI;
import ru.alfabouh.engine.render.scene.gui.elements.UIPictureStatic;
import ru.alfabouh.engine.render.scene.gui.elements.UIPictureStaticSelectable;
import ru.alfabouh.engine.render.scene.gui.elements.UIText;
import ru.alfabouh.engine.render.scene.gui.elements.base.UIScalable;
import ru.alfabouh.engine.render.scene.gui.elements.base.UIInteractiveElement;
import ru.alfabouh.engine.render.scene.gui.elements.base.font.GuiFont;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.settings.objects.SettingFloatBar;

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
    public void render(double partialTicks) {
        super.render(partialTicks);
        UIPictureStatic line = new UIPictureStatic(ResourceManager.renderAssets.gui1, new Vector2i(this.getPosition()).add(0, (int) (3.0f * this.getScaling().y)), new Vector2f(0.0f, 13.0f), new Vector2f(100.0f, 1.0f), this.getZValue());
        UIPictureStaticSelectable brick = new UIPictureStaticSelectable(ResourceManager.renderAssets.gui1, new Vector2i(this.getPosition()).add((int) (this.getOptionValue() * this.getScaling().mul(98).x), 0), new Vector2f(0.0f, 14.0f), new Vector2f(2.0f, 7.0f), this.getZValue());
        brick.setSelected(this.isSelected());

        line.setScaling(this.getScaling());
        brick.setScaling(this.getScaling());

        line.render(partialTicks);
        brick.render(partialTicks);

        line.cleanData();
        brick.cleanData();

        UIText uiText = new UIText(this.title + " " + (int) (this.getOptionValue() * 100.0f) + "%", guiFont, this.hexColor, new Vector2i(this.getPosition()).add(this.getSize().x + 30, -3), this.zValue);
        uiText.render(partialTicks);
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
    protected void onMouseInside(Vector2d mouseCoordinates) {

    }

    @Override
    protected void onMouseEntered() {

    }

    @Override
    protected void onMouseLeft() {

    }

    @Override
    protected void onClicked(Vector2d mouseCoordinates) {
        float value = MathHelper.clamp((float) (mouseCoordinates.x - this.getPosition().x) / this.getSize().x, 0.0f, 1.0f);
        this.setOptionValue(value);
    }

    @Override
    protected void onUnClicked(Vector2d mouseCoordinates) {

    }
}
