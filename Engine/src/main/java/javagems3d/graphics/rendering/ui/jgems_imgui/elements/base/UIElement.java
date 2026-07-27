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

package javagems3d.graphics.rendering.ui.jgems_imgui.elements.base;

import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.JGemsTransformManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import javagems3d.JGems3D;
import javagems3d.system.controller.base.IController;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import java.util.Objects;

public abstract class UIElement implements UIScalable {
    private final JGemsShaderManager defaultShader;
    private final Vector2f scaling;
    private final float zValue;
    private int unUsedTicks;
    private JGemsShaderManager overShader;
    private final IWindow window;
    private final ScaleMode scaleMode;

    public UIElement(@NotNull IWindow window, JGemsShaderManager defaultShader, float zValue) {
        this.defaultShader = defaultShader;
        this.scaling = new Vector2f(1.0f);
        this.zValue = zValue;
        this.overShader = null;
        this.window = window;
        this.scaleMode = new ScaleMode(true, true);
        this.zeroUnusedTicks();
    }

   // public Pose2D getUIElementModelPose(Vector2i originalSize, Pose2D pose2D) {
   //     return new Pose2D(this.getAutoScaleAffectedUiPos(new Vector2i((int) pose2D.getPosition().x, (int) pose2D.getPosition().y), originalSize), pose2D.getRotation(), this.getAutoScaleAffectedUiVector(pose2D.getScale()));
   // }

    protected Vector2f getScaleAffectedUiPos(Vector2f pos) {
        Vector2f uiSize = this.getOriginalSize();
        Vector2f scaledSize = new Vector2f(uiSize).mul(this.GLOBAL_SCALE_FACTOR());
      // if (this instanceof UIPictureSizable ) {
      //     System.out.println((this.getAutoScaleMode().SCALE_AFFECT_POS_X ? (uiSize.x - scaledSize.x) * 1 : 0.0f));
      // }
        return new Vector2f(pos).add((this.getAutoScaleMode().CENTER_X_POS ? (uiSize.x - scaledSize.x) * 0.5f : 0.0f), (this.getAutoScaleMode().CENTER_Y_POS ? (uiSize.y - scaledSize.y) * 0.5f : 0.0f));
    }

    protected Vector2f getScaleAffectedUiVector(Vector2f vec) {
        if (this.getAutoScaleMode().SCALE_AFFECT_SIZE()) {
            return new Vector2f((vec.x * this.GLOBAL_SCALE_FACTOR()), (vec.y * this.GLOBAL_SCALE_FACTOR()));
        }
        return new Vector2f(vec);
    }

    public static Matrix4f getProjection(IWindow window, float scale) {
        // Vector2i orig = window.getWindowSize();
        // Matrix4f view = new Matrix4f().translate(orig.x * 0.5f, orig.y * 0.5f, 0.0f).scale(scale, scale, 1.0f).translate(-orig.x * 0.5f, -orig.y * 0.5f, 0.0f);
        return JGemsTransformManager.INSTANCE.getOrthographicMatrix(); //.mul(view);
    }

    public ScaleMode getAutoScaleMode() {
        return this.scaleMode;
    }

    public abstract void render(float frameDeltaTicks);

    public abstract void build();

    public abstract void clear();

    public abstract @NotNull Vector2f getOriginalSize();

    public abstract @NotNull Vector2f getScaledSize();

    public abstract @NotNull Vector2f getPosition();

    public abstract int calcUIHash();

    public void setDefaultScaling() {
        this.scaling.set(1.0f);
    }

    public IWindow getWindow() {
        return this.window;
    }

    public void setDefaultShader() {
        this.overShader = null;
    }

    protected float GLOBAL_SCALE_FACTOR() {
        return JGemsUI.GET_GLOBAL_UI_SCALING();
    }

    public Vector2f getScaling() {
        return new Vector2f(this.scaling);
    }

    public UIElement setScaling(Vector2f scaling) {
        this.scaling.set(scaling);
        return this;
    }

    protected IController getController() {
        return JGems3D.get().getScreen().getControllerDispatcher().getCurrentController();
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
                Objects.equals(this.getScaledSize(), uiElement.getScaledSize()) &&
                Objects.equals(this.getCurrentShader(), uiElement.getCurrentShader());
    }

    @Override
    public int hashCode() {
        return this.calcUIHash();
    }

    public static class ScaleMode {
        private boolean SCALE_AFFECT_SIZE;
        private boolean CENTER_X_POS;
        private boolean CENTER_Y_POS;

        public ScaleMode(boolean SCALE_AFFECT_SIZE, boolean SCALE_AFFECT_POS_XY) {
            this.SCALE_AFFECT_SIZE = SCALE_AFFECT_SIZE;
            this.CENTER_X_POS = SCALE_AFFECT_POS_XY;
            this.CENTER_Y_POS = SCALE_AFFECT_POS_XY;
        }

        public ScaleMode(boolean SCALE_AFFECT_SIZE, boolean CENTER_X_POS, boolean CENTER_Y_POS) {
            this.SCALE_AFFECT_SIZE = SCALE_AFFECT_SIZE;
            this.CENTER_X_POS = CENTER_X_POS;
            this.CENTER_Y_POS = CENTER_Y_POS;
        }

        public ScaleMode COPY(ScaleMode other) {
            this.SCALE_AFFECT_SIZE = other.SCALE_AFFECT_SIZE();
            this.CENTER_X_POS = other.SCALE_AFFECT_POS_X();
            this.CENTER_Y_POS = other.SCALE_AFFECT_POS_Y();
            return this;
        }

        public ScaleMode SCALE_AFFECT_SIZE(boolean SCALE_AFFECT_SIZE) {
            this.SCALE_AFFECT_SIZE = SCALE_AFFECT_SIZE;
            return this;
        }

        public ScaleMode SCALE_AFFECT_POS_XY(boolean SCALE_AFFECT_POS_XY) {
            this.CENTER_X_POS = SCALE_AFFECT_POS_XY;
            this.CENTER_Y_POS = SCALE_AFFECT_POS_XY;
            return this;
        }

        public ScaleMode SCALE_AFFECT_POS_X(boolean SCALE_AFFECT_POS_X) {
            this.CENTER_X_POS = SCALE_AFFECT_POS_X;
            return this;
        }

        public ScaleMode SCALE_AFFECT_POS_Y(boolean SCALE_AFFECT_POS_Y) {
            this.CENTER_Y_POS = SCALE_AFFECT_POS_Y;
            return this;
        }

        public boolean SCALE_AFFECT_SIZE() {
            return this.SCALE_AFFECT_SIZE;
        }

        public boolean SCALE_AFFECT_POS_X() {
            return this.CENTER_X_POS;
        }

        public boolean SCALE_AFFECT_POS_Y() {
            return this.CENTER_Y_POS;
        }
    }
}
