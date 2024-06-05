package ru.alfabouh.engine.render.scene.immediate_gui.elements.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.engine.system.controller.input.IController;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;

import java.util.Objects;

public abstract class UIElement implements UIScalable {
    private ShaderManager defaultShader;
    private ShaderManager overridenShader;
    private final Vector2f scaling;
    private final float zValue;

    public UIElement(ShaderManager defaultShader, float zValue) {
        this.defaultShader = defaultShader;
        this.scaling = new Vector2f(1.0f);
        this.zValue = zValue;
        this.overridenShader = null;
    }

    public abstract void render(double partialTicks);
    public abstract void cleanData();
    public abstract @NotNull Vector2i getSize();
    public abstract @NotNull Vector2i getPosition();
    public abstract int calcUIHashCode();

    public void setDefaultScaling() {
        this.scaling.set(1.0f);
    }

    public void setDefaultShader() {
        this.overridenShader = null;
    }

    public UIElement setCurrentShader(ShaderManager shader) {
        this.overridenShader = shader;
        return this;
    }

    public UIElement setScaling(Vector2f scaling) {
        this.scaling.set(scaling);
        return this;
    }

    @Override
    public Vector2f getScaling() {
        return new Vector2f(this.scaling).mul(ImmediateUI.GET_GLOBAL_UI_SCALING());
    }

    protected IController getController() {
        return JGems.get().getScreen().getControllerDispatcher().getCurrentController();
    }

    public float getZValue() {
        return this.zValue;
    }

    protected ShaderManager getCurrentShader() {
        return this.overridenShader != null ? this.overridenShader : this.defaultShader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final UIElement uiElement = (UIElement) o;
        return Float.compare(this.getZValue(), uiElement.getZValue()) == 0 &&
                Objects.equals(this.getPosition(), uiElement.getPosition()) &&
                Objects.equals(this.getScaling(), uiElement.getScaling()) &&
                Objects.equals(this.getSize(), uiElement.getSize()) &&
                Objects.equals(this.getCurrentShader(), uiElement.getCurrentShader());
    }

    @Override
    public int hashCode() {
        return this.calcUIHashCode();
    }
}
