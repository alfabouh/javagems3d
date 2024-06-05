package ru.alfabouh.engine.render.scene.immediate_gui.elements.optionsUI;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.UIPictureStaticSelectable;
import ru.alfabouh.engine.render.scene.immediate_gui.elements.base.UIInteractiveElement;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.settings.basic.SettingSlot;

public class UIArrow extends UIInteractiveElement {
    private final UIPictureStaticSelectable imageStaticUI;
    private final SettingSlot settingIntSlots;
    private final int vector;
    private final Vector2i position;
    private final Vector2i size;

    public UIArrow(int vector, @NotNull SettingSlot settingIntSlots, @NotNull Vector2i position, float zValue) {
        super(null, zValue);
        this.position = position;
        this.size = new Vector2i(4, 8);

        this.imageStaticUI = new UIPictureStaticSelectable(ResourceManager.renderAssets.gui1, position, new Vector2f(vector == -1 ? 5.0f : 0.0f, 22.0f), new Vector2f(this.size), zValue);

        this.settingIntSlots = settingIntSlots;
        this.vector = vector;
    }

    @Override
    public void render(double partialTicks) {
        super.render(partialTicks);

        this.imageStaticUI.setScaling(this.getScaling());
        this.imageStaticUI.setSelected(this.isSelected());
        this.imageStaticUI.render(partialTicks);
    }

    @Override
    public void cleanData() {
        this.imageStaticUI.cleanData();
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
        result = prime * result + this.vector;
        result = prime * result + this.imageStaticUI.hashCode();
        return result;
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
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.button, SoundType.SYSTEM, 2.0f, 1.0f);
    }

    @Override
    protected void onUnClicked(Vector2d mouseCoordinates) {

    }
}