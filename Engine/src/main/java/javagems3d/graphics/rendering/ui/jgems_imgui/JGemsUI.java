package javagems3d.graphics.rendering.ui.jgems_imgui;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.*;
import javagems3d.graphics.screen.window.IWindow;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.settings.objects.SettingFloatBar;
import javagems3d.system.settings.objects.SettingSlot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class JGemsUI implements IWindow.ResizeEvent {
    private final Map<Integer, UIElement> uiFrameCache;
    private final RenderUIData renderUIData;
    private boolean requestCleanFrame;
    private PanelUI currentPanel;
    public float frameDeltaTicks;
    private final IWindow window;

    public JGemsUI(IWindow window) {
        this.uiFrameCache = new HashMap<>();
        this.currentPanel = null;
        this.requestCleanFrame = false;
        this.renderUIData = new RenderUIData();
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

    public static int CALC_INT_WITH_GLOBAL_UI_SCALING(float in) {
        return (int) (in * JGemsUI.GET_GLOBAL_UI_SCALING());
    }

    public static int CALC_INT_WITH_SCREEN_NORMALIZED_UI_SCALING(float in) {
        return (int) (in * JGemsUI.GET_SCREEN_NORMALIZED_SCALING());
    }

    public static float GET_GLOBAL_UI_SCALING() {
        if (!JGemsConfig.SYSTEM.AUTO_SCREEN_SCALING) {
            return (float) (1.0f / Math.pow(2.0f, JGemsConfig.SYSTEM.GLOBAL_UI_SCALING));
        }
        return GET_SCREEN_NORMALIZED_SCALING();
    }

    public static float GET_SCREEN_NORMALIZED_SCALING() {
        double width = JGems3D.get().getScreen().getWindowDimensions().x;
        double height = JGems3D.get().getScreen().getWindowDimensions().y;
        float f1 = (float) (width / JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH);
        float f2 = (float) (height / JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT);
        float f1_r = (float) Math.max(Math.ceil(f1 * 2.0f) / 2.0f, 1.0f);
        float f2_r = (float) Math.max(Math.ceil(f2 * 2.0f) / 2.0f, 1.0f);
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
    }

    public void defaultScale() {
        this.renderUIData.setScaling(null);
    }

    public void defaultShader() {
        this.renderUIData.setShaderManager(null);
    }

    public void scale(Vector2f scaling) {
        this.renderUIData.setScaling(scaling);
    }

    public void shader(JGemsShaderManager shaderManager) {
        this.renderUIData.setShaderManager(shaderManager);
    }

    public UIText textUI(String text, JGemsGuiFont guiFont, Vector2i position, int hexColor, float zValue) {
        return this.checkUIInCacheAndRender(UIText.class, new UIText(text, guiFont, hexColor, position, zValue));
    }

    public UIPictureStatic imageUI(ITexture2DProgram iImageSample, Vector2i position, Vector2f textureXY, Vector2f textureWH, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureStatic.class, new UIPictureStatic(iImageSample, position, textureXY, textureWH, zValue));
    }

    public UIPictureSizable imageUI(ITexture2DProgram iImageSample, Vector2i position, Vector2i size, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureSizable.class, new UIPictureSizable(iImageSample, position, size, zValue));
    }

    public UIDefaultButton buttonUI(String text, JGemsGuiFont guiFont, Vector2i position, Vector2i size, int textColorHex, float zValue) {
        return this.checkUIInCacheAndRender(UIDefaultButton.class, new UIDefaultButton(text, guiFont, position, size, textColorHex, zValue));
    }

    public UISlider settingSliderUI(String text, JGemsGuiFont guiFont, int hexColor, Vector2i position, SettingFloatBar settingFloatBar, float zValue) {
        return this.checkUIInCacheAndRender(UISlider.class, new UISlider(text, guiFont, hexColor, position, settingFloatBar, zValue));
    }

    public UICarousel settingCarouselUI(String text, JGemsGuiFont guiFont, int hexColor, Vector2i position, SettingSlot settingIntSlots, float zValue) {
        return this.checkUIInCacheAndRender(UICarousel.class, new UICarousel(text, guiFont, hexColor, position, settingIntSlots, zValue));
    }

    @SuppressWarnings("all")
    public <T extends UIElement> T drawUI(UIElement uiElement) {
        return (T) this.checkUIInCacheAndRender(UIElement.class, uiElement);
    }

    private <T extends UIElement> T checkUIInCacheAndRender(Class<T> clazz, UIElement uiElement) {
        if (this.renderUIData.getShaderManager() != null) {
            uiElement.setCurrentShader(this.renderUIData.getShaderManager());
        } else {
            uiElement.setDefaultShader();
        }
        if (this.renderUIData.getScaling() != null) {
            uiElement.setScaling(this.renderUIData.getScaling());
        } else {
            uiElement.setDefaultScaling();
        }
        T ui = this.addUIInCache(clazz, uiElement);
        ui.render(this.frameDeltaTicks);
        return ui;
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

    private static class RenderUIData {
        private JGemsShaderManager shaderManager;
        private Vector2f scaling;

        public RenderUIData() {
        }

        public void reset() {
            this.scaling = null;
            this.shaderManager = null;
        }

        public Vector2f getScaling() {
            return this.scaling;
        }

        public void setScaling(Vector2f scaling) {
            this.scaling = scaling;
        }

        public JGemsShaderManager getShaderManager() {
            return this.shaderManager;
        }

        public void setShaderManager(JGemsShaderManager shaderManager) {
            this.shaderManager = shaderManager;
        }
    }
}
