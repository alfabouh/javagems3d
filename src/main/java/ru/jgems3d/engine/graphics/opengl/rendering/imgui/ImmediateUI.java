/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.graphics.opengl.rendering.imgui;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.UIButton;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.UIPictureSizable;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.UIPictureStatic;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.UIText;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.UIElement;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.UICarousel;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.UISlider;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ITextureSample;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.settings.objects.SettingSlot;
import ru.jgems3d.engine.system.settings.objects.SettingFloatBar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ImmediateUI {
    private final Map<Integer, UIElement> uiFrameCache;
    private final RenderUIData renderUIData;
    private boolean requestCleanFrame;
    private PanelUI currentPanel;
    private float frameDeltaTicks;

    public ImmediateUI() {
        this.uiFrameCache = new HashMap<>();
        this.currentPanel = null;
        this.requestCleanFrame = false;
        this.renderUIData = new RenderUIData();
    }

    public static Vector3f HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public static int getFontHeight(GuiFont fontTexture) {
        return fontTexture.getHeight();
    }

    public static int getTextWidth(GuiFont fontTexture, String text) {
        char[] chars = text.toCharArray();
        int startX = 0;
        for (final char aChar : chars) {
            GuiFont.CharInfo charInfo = fontTexture.getCharInfo(aChar);
            startX += charInfo.getWidth();
        }
        return startX;
    }

    public static int CALC_INT_WITH_GLOBAL_UI_SCALING(float in) {
        return (int) (in * ImmediateUI.GET_GLOBAL_UI_SCALING());
    }

    public static int CALC_INT_WITH_SCREEN_NORMALIZED_UI_SCALING(float in) {
        return (int) (in * ImmediateUI.GET_SCREEN_NORMALIZED_SCALING());
    }

    public static float GET_GLOBAL_UI_SCALING() {
        if (!JGemsSceneGlobalConstants.AUTO_SCREEN_SCALING) {
            return (float) (1.0f / Math.pow(2.0f, JGemsSceneGlobalConstants.GLOBAL_UI_SCALING));
        }
        return GET_SCREEN_NORMALIZED_SCALING();
    }

    public static float GET_SCREEN_NORMALIZED_SCALING() {
        double width = JGems3D.get().getScreen().getWindowDimensions().x;
        double height = JGems3D.get().getScreen().getWindowDimensions().y;
        float f1 = (float) (width / JGemsSceneGlobalConstants.defaultW);
        float f2 = (float) (height / JGemsSceneGlobalConstants.defaultH);
        float f1_r = (float) Math.max(Math.ceil(f1 * 2.0f) / 2.0f, 1.0f);
        float f2_r = (float) Math.max(Math.ceil(f2 * 2.0f) / 2.0f, 1.0f);
        return Math.min(f1_r, f2_r);
    }

    public void setPanel(PanelUI panelUI) {
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onDestruct(this);
        }
        this.currentPanel = panelUI;
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onConstruct(this);
        }
        this.setRequestCleanFrame();
    }

    public void removePanel() {
        this.setPanel(null);
    }

    public void renderFrame(float frameDeltaTicks) {
        this.frameDeltaTicks = frameDeltaTicks;

        if (this.requestCleanFrame) {
            this.cleanFrame();
            this.requestCleanFrame = false;
        }

        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().drawPanel(this, this.frameDeltaTicks);
        }

        this.getUiFrameCache().values().forEach(UIElement::incrementUnusedTicks);
        Iterator<UIElement> uiElementIterator = this.getUiFrameCache().values().iterator();
        while (uiElementIterator.hasNext()) {
            UIElement element = uiElementIterator.next();
            if (element.getUnUsedTicks() > JGemsSceneGlobalConstants.TICKS_TO_CLEAN_UNUSED_UI) {
                element.cleanData();
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

    public UIText textUI(String text, GuiFont guiFont, Vector2i position, int hexColor, float zValue) {
        return this.checkUIInCacheAndRender(UIText.class, new UIText(text, guiFont, hexColor, position, zValue));
    }

    public UIPictureStatic imageUI(ITextureSample iImageSample, Vector2i position, Vector2f textureXY, Vector2f textureWH, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureStatic.class, new UIPictureStatic(iImageSample, position, textureXY, textureWH, zValue));
    }

    public UIPictureSizable imageUI(ITextureSample iImageSample, Vector2i position, Vector2i size, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureSizable.class, new UIPictureSizable(iImageSample, position, size, zValue));
    }

    public UIButton buttonUI(String text, GuiFont guiFont, Vector2i position, Vector2i size, int textColorHex, float zValue) {
        return this.checkUIInCacheAndRender(UIButton.class, new UIButton(text, guiFont, position, size, textColorHex, zValue));
    }

    public UISlider settingSliderUI(String text, GuiFont guiFont, int hexColor, Vector2i position, SettingFloatBar settingFloatBar, float zValue) {
        return this.checkUIInCacheAndRender(UISlider.class, new UISlider(text, guiFont, hexColor, position, settingFloatBar, zValue));
    }

    public UICarousel settingCarouselUI(String text, GuiFont guiFont, int hexColor, Vector2i position, SettingSlot settingIntSlots, float zValue) {
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
        uiElement.buildUI();
        int hash = uiElement.hashCode();
        if (this.getUiFrameCache().containsKey(hash)) {
            UIElement cachedUiElement = this.getUiFrameCache().get(hash);
            if (uiElement.equals(cachedUiElement)) {
                cachedUiElement.zeroUnusedTicks();
                uiElement.cleanData();
                return clazz.cast(cachedUiElement);
            }
        } else {
            //System.out.println(hash);
            this.getUiFrameCache().put(uiElement.hashCode(), uiElement);
        }
        return clazz.cast(uiElement);
    }

    public void onWindowResize(Vector2i dim) {
        this.setRequestCleanFrame();
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onWindowResize(dim);
        }
    }

    public void setRequestCleanFrame() {
        this.requestCleanFrame = true;
    }

    public void destroyUI() {
        this.cleanFrame();
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onDestruct(this);
            this.currentPanel = null;
        }
    }

    protected void cleanFrame() {
        this.getUiFrameCache().forEach((key, value) -> value.cleanData());
        this.getUiFrameCache().clear();
    }

    public Window getWindow() {
        return JGemsHelper.getScreen().getWindow();
    }

    public PanelUI getCurrentPanel() {
        return this.currentPanel;
    }

    public Map<Integer, UIElement> getUiFrameCache() {
        return this.uiFrameCache;
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
