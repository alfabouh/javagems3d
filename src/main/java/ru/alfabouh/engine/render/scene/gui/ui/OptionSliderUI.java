package ru.alfabouh.engine.render.scene.gui.ui;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.game.settings.SettingFloatBar;
import ru.alfabouh.engine.math.MathHelper;

public class OptionSliderUI extends InteractiveUI {
    private final ImageStaticUI line;
    private final ImageStaticUI brick;
    private final SettingFloatBar settingFloatBar;

    public OptionSliderUI(Vector3f position, SettingFloatBar settingFloatBar) {
        super(position, new Vector2f(100.0f, 10.0f));
        this.line = new ImageStaticUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f, 12.0f), new Vector2f(100.0f, 2.0f));
        this.brick = new ImageStaticUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f, 14.0f), new Vector2f(2.0f, 6.0f));
        this.settingFloatBar = settingFloatBar;
    }

    public SettingFloatBar getSettingFloatBar() {
        return this.settingFloatBar;
    }

    public float scaling() {
        return 3.0f;
    }

    public float getValue() {
        return this.getSettingFloatBar().getValue();
    }

    public void setValue(float value) {
        this.getSettingFloatBar().setValue(value);
    }

    @Override
    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        super.render(partialTicks);

        this.line.setScaling(this.scaling());
        this.brick.setScaling(this.scaling());

        this.line.setPosition(this.getPosition());
        this.brick.setPosition(new Vector3f(this.getPosition()).add(((int) (this.getValue() * (this.scaling() * 100.0f))), -3.0f, 0.0f));

        this.brick.setTextureXY(new Vector2f(0.0f, 15.0f));
        this.brick.setTextureWH(new Vector2f(2.0f, 6.0f));

        this.line.render(partialTicks);
        this.brick.render(partialTicks);
    }

    @Override
    protected boolean interruptMouseAfterClick() {
        return false;
    }

    public Vector2f getSize() {
        return super.getSize().mul(this.scaling());
    }

    @Override
    public void onMouseInside(Vector2d mouseCoordinates) {

    }

    @Override
    public void onMouseEntered() {

    }

    @Override
    public void onMouseLeft() {

    }

    @Override
    public void onClicked(Vector2d mouseCoordinates) {
        float value = MathHelper.clamp((float) (mouseCoordinates.x - this.getPosition().x) / this.getSize().x, 0.0f, 1.0f);
        this.setValue(value);
    }

    @Override
    public void onUnClicked(Vector2d mouseCoordinates) {
        this.getSettingFloatBar().performSetting();
    }

    @Override
    public void clear() {
        this.line.clear();
        this.brick.clear();
    }

    @Override
    public ShaderManager getCurrentShader() {
        return null;
    }
}
