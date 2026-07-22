package javagems3d.graphics.rendering.ui.jgems_imgui;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIGameInterface;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.*;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.settings.objects.SettingFloatBar;
import javagems3d.system.settings.objects.SettingSlot;

import java.util.*;

public final class JGemsUI implements IWindow.ResizeEvent {
    private final Map<Integer, UIElement> uiFrameCache;
    private boolean requestCleanFrame;
    private PanelUI currentPanel;
    public float frameDeltaTicks;
    private final IWindow window;

    private static final Deque<UIAutoScaleContext> AUTO_SCALE_CONTEXT_STACK = new ArrayDeque<>();
    private static final Deque<UIRenderSettings> UI_RENDER_SETTINGS_STACK = new ArrayDeque<>();
    private static final Deque<UIElement.ScaleMode> SCALE_MODE_STACK = new ArrayDeque<>();

    public JGemsUI(IWindow window) {
        this.uiFrameCache = new HashMap<>();
        this.currentPanel = null;
        this.requestCleanFrame = false;
        this.window = window;
    }

    public static Vector3f HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public static int getFontHeight(JGemsGuiFont fontTexture) {
        return fontTexture.getHeight();
    }

    public static int getTextWidth(JGemsGuiFont fontTexture, String text) {
        char[] chars = text.toCharArray();
        int startX = 0;
        for (final char aChar : chars) {
            JGemsGuiFont.CharInfo charInfo = fontTexture.getCharInfo(aChar);
            startX += charInfo.width();
        }
        return startX;
    }

    public static float globalScaledVal(float x) {
        return x * (JGemsUI.GET_GLOBAL_UI_SCALING());
    }

    public static Vector2f globalScaledVector(float x, float y) {
        return new Vector2f(x, y).mul(JGemsUI.GET_GLOBAL_UI_SCALING());
    }

    public static Vector2f globalScaledVector(Vector2f vector2f) {
        return vector2f.mul(JGemsUI.GET_GLOBAL_UI_SCALING());
    }

    public static float GET_GLOBAL_UI_SCALING() {
        if (JGemsUI.AUTO_SCALE_CONTEXT_STACK.isEmpty()) {
            return JGemsConfig.SYSTEM.GLOBAL_UI_PROJECTION_SCALING + DearUIGameInterface.param2[0];
        }
        return GET_SCREEN_NORMALIZED_SCALING() + DearUIGameInterface.param2[0];
    }

    public static float GET_SCREEN_NORMALIZED_SCALING() {
        if (JGemsUI.getCurrentAutoScaleContext() == null) {
            return 1.0f;
        }

        final float basicDimX = 1920.0f;
        final float basicDimY = 1080.0f;

        final double width = JGems3D.get().getScreen().getWindowDimensions().x;
        final double height = JGems3D.get().getScreen().getWindowDimensions().y;
        final float f1 = (float) (width / basicDimX);
        final float f2 = (float) (height / basicDimY);

        final float scaleStep = JGemsUI.getCurrentAutoScaleContext().scaleStep();
        final boolean scaleUp = JGemsUI.getCurrentAutoScaleContext().canScaleUp();
        final boolean scaleDown = JGemsUI.getCurrentAutoScaleContext().canScaleDown();

        final float f1_r = (float) JGemsHelper.math().clamp(Math.ceil(f1 * scaleStep) / scaleStep,
                 !scaleDown ? 1.0f : 0.0f,
                 !scaleUp ? 1.0f : 128.0f);

        final float f2_r = (float) JGemsHelper.math().clamp(Math.ceil(f2 * scaleStep) / scaleStep,
                !scaleDown ? 1.0f : 0.0f,
                !scaleUp ? 1.0f : 128.0f);

        return Math.min(f1_r, f2_r);
    }

    public void setUiPanel(PanelUI panelUI) {
        if (panelUI == null) {
            this.currentPanel = null;
            return;
        }
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onDestruct(this);
        }
        this.currentPanel = panelUI;
        this.getCurrentPanel().onConstruct(this);
        this.setRequestCleanFrame();
    }

    public void removePanel() {
        this.setUiPanel(null);
    }

    public void renderFrame(float frameDeltaTicks) {
        this.frameDeltaTicks = frameDeltaTicks;

        if (this.requestCleanFrame) {
            this.clearFrame();
            this.requestCleanFrame = false;
        }

        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().drawPanel(this, this.frameDeltaTicks);
        }

        this.getUiFrameCache().values().forEach(UIElement::incrementUnusedTicks);
        Iterator<UIElement> uiElementIterator = this.getUiFrameCache().values().iterator();
        while (uiElementIterator.hasNext()) {
            UIElement element = uiElementIterator.next();
            if (element.getUnUsedTicks() > JGemsConfig.SYSTEM.TICKS_TO_CLEAN_UNUSED_UI) {
                element.clear();
                uiElementIterator.remove();
            }
        }
        JGemsUI.AUTO_SCALE_CONTEXT_STACK.clear();
        JGemsUI.SCALE_MODE_STACK.clear();
        JGemsUI.UI_RENDER_SETTINGS_STACK.clear();
    }

    public UIText textUI(String text, JGemsGuiFont guiFont, Vector2f position, int hexColor, float zValue) {
        return this.checkUIInCacheAndRender(UIText.class, new UIText(this.getWindow(), text, guiFont, hexColor, position, zValue));
    }

    public UIPictureStatic imageUI(ITexture2DProgram iImageSample, Vector2f position, Vector2f textureXY, Vector2f textureWH, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureStatic.class, new UIPictureStatic(this.getWindow(), iImageSample, position, textureXY, textureWH, zValue));
    }

    public UIPictureSizable imageUI(ITexture2DProgram iImageSample, Vector2f position, Vector2f size, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureSizable.class, new UIPictureSizable(this.getWindow(), iImageSample, position, size, zValue));
    }

    public UIDefaultButton buttonUI(String text, JGemsGuiFont guiFont, Vector2f position, Vector2f size, int textColorHex, float zValue) {
        return this.checkUIInCacheAndRender(UIDefaultButton.class, new UIDefaultButton(this.getWindow(), text, guiFont, position, size, textColorHex, zValue));
    }

    public UISlider settingSliderUI(String text, JGemsGuiFont guiFont, int hexColor, Vector2f position, SettingFloatBar settingFloatBar, float zValue) {
        return this.checkUIInCacheAndRender(UISlider.class, new UISlider(this.getWindow(), text, guiFont, hexColor, position, settingFloatBar, zValue));
    }

    public UICarousel settingCarouselUI(String text, JGemsGuiFont guiFont, int hexColor, Vector2f position, SettingSlot settingIntSlots, float zValue) {
        return this.checkUIInCacheAndRender(UICarousel.class, new UICarousel(this.getWindow(), text, guiFont, hexColor, position, settingIntSlots, zValue));
    }

    @SuppressWarnings("all")
    public <T extends UIElement> T drawUI(UIElement uiElement) {
        return (T) this.checkUIInCacheAndRender(UIElement.class, uiElement);
    }

    private <T extends UIElement> T checkUIInCacheAndRender(Class<T> clazz, UIElement uiElement) {
        if (JGemsUI.getCurrentRenderSettings() != null && JGemsUI.getCurrentRenderSettings().overrideCurrentDrawShader() != null) {
            uiElement.setCurrentShader(JGemsUI.getCurrentRenderSettings().overrideCurrentDrawShader());
        } else {
            uiElement.setDefaultShader();
        }
        if (JGemsUI.getCurrentRenderSettings() != null && JGemsUI.getCurrentRenderSettings().setCurrentDrawScaling() != null) {
            uiElement.setScaling(JGemsUI.getCurrentRenderSettings().setCurrentDrawScaling());
        } else {
            uiElement.setDefaultScaling();
        }
        T ui = this.addUIInCache(clazz, uiElement);
        if (!this.requestCleanFrame) {
            if (JGemsUI.getCurrentScaleMode() != null) {
                ui.getAutoScaleMode().COPY(JGemsUI.getCurrentScaleMode());
            }
            ui.render(this.frameDeltaTicks);
        }
        return ui;
    }

    public void pushAutoScaleContext(@NotNull UIAutoScaleContext context) {
        JGemsUI.AUTO_SCALE_CONTEXT_STACK.push(context);
    }

    public void popAutoScaleContext() {
        JGemsUI.AUTO_SCALE_CONTEXT_STACK.poll();
    }

    private static @Nullable UIAutoScaleContext getCurrentAutoScaleContext() {
        return JGemsUI.AUTO_SCALE_CONTEXT_STACK.peek();
    }

    public void pushRenderSettings(@NotNull UIRenderSettings settings) {
        JGemsUI.UI_RENDER_SETTINGS_STACK.push(settings);
    }

    public void popRenderSettings() {
        JGemsUI.UI_RENDER_SETTINGS_STACK.poll();
    }

    private static @Nullable UIRenderSettings getCurrentRenderSettings() {
        return JGemsUI.UI_RENDER_SETTINGS_STACK.peek();
    }

    public void pushScaleMode(@NotNull UIElement.ScaleMode scaleMode) {
        JGemsUI.SCALE_MODE_STACK.push(scaleMode);
    }

    public void popScaleMode() {
        JGemsUI.SCALE_MODE_STACK.poll();
    }

    private static @Nullable UIElement.ScaleMode getCurrentScaleMode() {
        return JGemsUI.SCALE_MODE_STACK.peek();
    }

    private <T extends UIElement> T addUIInCache(Class<T> clazz, UIElement uiElement) {
        uiElement.build();
        int hash = uiElement.hashCode();
        if (this.getUiFrameCache().containsKey(hash)) {
            UIElement cachedUiElement = this.getUiFrameCache().get(hash);
            if (uiElement.equals(cachedUiElement)) {
                cachedUiElement.zeroUnusedTicks();
                uiElement.clear();
                return clazz.cast(cachedUiElement);
            }
        } else {
            //System.out.println(hash);
            this.getUiFrameCache().put(uiElement.hashCode(), uiElement);
        }
        return clazz.cast(uiElement);
    }

    public void onWindowResize(IWindow window) {
        this.setRequestCleanFrame();
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onWindowResize(window);
        }
    }

    public void setRequestCleanFrame() {
        this.requestCleanFrame = true;
    }

    public void destroyUI() {
        this.clearFrame();
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onDestruct(this);
            this.currentPanel = null;
        }
    }

    private void clearFrame() {
        this.getUiFrameCache().values().forEach(UIElement::clear);
        this.getUiFrameCache().clear();
    }

    public PanelUI getCurrentPanel() {
        return this.currentPanel;
    }

    public Map<Integer, UIElement> getUiFrameCache() {
        return this.uiFrameCache;
    }

    public IWindow getWindow() {
        return this.window;
    }

    public record UIAutoScaleContext(boolean autoScale, boolean canScaleUp, boolean canScaleDown, float scaleStep) {
        public UIAutoScaleContext(boolean autoScale, boolean canScaleUp, boolean canScaleDown) {
            this(autoScale, canScaleUp, canScaleDown, 8.0f);
        }
    }
    public record UIRenderSettings(@Nullable JGemsShaderManager overrideCurrentDrawShader, @Nullable Vector2f setCurrentDrawScaling) { }
}
