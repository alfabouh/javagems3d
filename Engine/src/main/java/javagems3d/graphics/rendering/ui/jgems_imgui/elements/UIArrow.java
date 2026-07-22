package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import javagems3d.JGems3D;
import javagems3d.audio.data.SoundType;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIInteractiveElement;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.settings.objects.SettingSlot;

public class UIArrow extends UIInteractiveElement {
    private final UIPictureStaticSelectable imageStaticUI;
    private final SettingSlot settingIntSlots;
    private final int vector;
    private final Vector2f position;
    private final Vector2f size;

    public UIArrow(IWindow window, int vector, @NotNull SettingSlot settingIntSlots, @NotNull Vector2f position, float zValue) {
        super(window, null, zValue);
        this.getAutoScaleMode().SCALE_AFFECT_POS_XY(false);
        this.getAutoScaleMode().SCALE_AFFECT_SIZE(false);
        this.position = position;
        this.size = new Vector2f(4, 8);
        this.imageStaticUI = new UIPictureStaticSelectable(window, JGemsResourceManager.globalTextureAssets.gui1, position, new Vector2f(vector == -1 ? 5.0f : 0.0f, 22.0f), new Vector2f(this.size), zValue);
        this.settingIntSlots = settingIntSlots;
        this.vector = vector;
    }

    @Override
    public void render(float frameDeltaTicks) {
        super.render(frameDeltaTicks);
        this.imageStaticUI.getAutoScaleMode().COPY(this.getAutoScaleMode());
        this.imageStaticUI.setScaling(this.getScaling());
        this.imageStaticUI.setSelected(this.isSelected());
        this.imageStaticUI.render(frameDeltaTicks);
    }

    @Override
    public void build() {
        this.imageStaticUI.build();
    }

    @Override
    public void clear() {
        this.imageStaticUI.clear();
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
        result = prime * result + this.vector;
        result = prime * result + this.imageStaticUI.hashCode();
        return result;
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
        if (this.vector == 1) {
            if (!this.settingIntSlots.goRight()) {
                this.settingIntSlots.setValue(0);
            }
        }
        if (this.vector == -1) {
            if (!this.settingIntSlots.goLeft()) {
                this.settingIntSlots.setValue(this.settingIntSlots.getMax());
            }
        }
        JGems3D.get().getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssets.button, SoundType.SYSTEM, 2.0f, 1.0f);
    }

    @Override
    protected void onReleased(Vector2f mouseCoordinates) {

    }
}
