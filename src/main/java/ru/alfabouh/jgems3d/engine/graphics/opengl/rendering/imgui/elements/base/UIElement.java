package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.Objects;

public abstract class UIElement implements UIScalable {
    private final JGemsShaderManager defaultShader;
    private final Vector2f scaling;
    private final float zValue;
    private int unUsedTicks;
    private JGemsShaderManager overShader;

    public UIElement(JGemsShaderManager defaultShader, float zValue) {
        this.defaultShader = defaultShader;
        this.scaling = new Vector2f(1.0f);
        this.zValue = zValue;
        this.overShader = null;
        this.zeroUnusedTicks();
    }

    public abstract void render(float partialTicks);

    public abstract void buildUI();

    public abstract void cleanData();

    public abstract @NotNull Vector2i getSize();

    public abstract @NotNull Vector2i getPosition();

    public abstract int calcUIHashCode();

    public void setDefaultScaling() {
        this.scaling.set(1.0f);
    }

    public void setDefaultShader() {
        this.overShader = null;
    }

    @Override
    public Vector2f getScaling() {
        return new Vector2f(this.scaling).mul(ImmediateUI.GET_GLOBAL_UI_SCALING());
    }

    public UIElement setScaling(Vector2f scaling) {
        this.scaling.set(scaling);
        return this;
    }

    protected IController getController() {
        return JGems.get().getScreen().getControllerDispatcher().getCurrentController();
    }

    public float getZValue() {
        return this.zValue;
    }

    protected JGemsShaderManager getCurrentShader() {
        return this.overShader != null ? this.overShader : this.defaultShader;
    }

    public UIElement setCurrentShader(JGemsShaderManager shader) {
        this.overShader = shader;
        return this;
    }

    public void zeroUnusedTicks() {
        this.unUsedTicks = -1;
    }

    public void incrementUnusedTicks() {
        this.unUsedTicks += 1;
    }

    public int getUnUsedTicks() {
        return this.unUsedTicks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !o.getClass().isAssignableFrom(this.getClass())) {
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
