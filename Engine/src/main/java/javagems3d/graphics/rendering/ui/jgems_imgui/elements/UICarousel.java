package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.system.settings.objects.SettingSlot;

public class UICarousel extends UIElement {
    private final UIArrow left;
    private final UIArrow right;
    private final UIText uiText;
    private final UIText uiTitle;
    private final Vector2f position;

    public UICarousel(IWindow window, @NotNull String text, @NotNull JGemsGuiFont guiFont, int hexColor, @NotNull Vector2f position, @NotNull SettingSlot settingIntSlots, float zValue) {
        super(window, null, zValue);
        //this.getAutoScaleMode().SCALE_AFFECT_POS(false);
        //this.getAutoScaleMode().SCALE_AFFECT_SIZE(false);

        this.position = position;

        this.left = new UIArrow(window, -1, settingIntSlots, position, zValue);
        this.right = new UIArrow(window, 1, settingIntSlots, new Vector2f(position.x + this.getScaledSize().x, position.y), zValue);

        this.left.setScaling(this.getScaling());
        this.right.setScaling(this.getScaling());
        this.right.getPosition().sub(this.right.getScaledSize().x, 0);

        float centerX = (this.left.getPosition().x + this.right.getPosition().x + this.right.getScaledSize().x) / 2;
        int textWidth = JGemsUI.getTextWidth(guiFont, settingIntSlots.getCurrentName());

        this.uiText = new UIText(this.getWindow(), settingIntSlots.getCurrentName(), guiFont, hexColor, new Vector2f(centerX - textWidth / 2f, position.y), zValue);
        this.uiTitle = new UIText(this.getWindow(), text, guiFont, hexColor, new Vector2f(position.x + this.getScaledSize().x + 30, position.y), zValue);
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.uiText.getAutoScaleMode().SCALE_AFFECT_POS_X(true);
        this.uiTitle.getAutoScaleMode().SCALE_AFFECT_POS_X(false);

        this.uiText.render(frameDeltaTicks);
        this.uiTitle.render(frameDeltaTicks);
        this.right.render(frameDeltaTicks);
        this.left.render(frameDeltaTicks);
    }

    @Override
    public void build() {
        this.uiText.build();
        this.uiTitle.build();
        this.right.build();
        this.left.build();
    }

    @Override
    public void clear() {
        this.left.clear();
        this.right.clear();
        this.uiText.clear();
        this.uiTitle.clear();
    }

    @Override
    public @NotNull Vector2f getOriginalSize() {
        return new Vector2f(100f, 8f);
    }

    @Override
    public @NotNull Vector2f getPosition() {
        return super.getScaleAffectedUiPos(this.position);
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
    public int calcUIHash() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.uiText.hashCode();
        result = prime * result + this.uiTitle.hashCode();
        result = prime * result + this.left.hashCode();
        result = prime * result + this.right.hashCode();
        return result;
    }
}
