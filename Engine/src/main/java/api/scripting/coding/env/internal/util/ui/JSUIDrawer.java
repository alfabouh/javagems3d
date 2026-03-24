package api.scripting.coding.env.internal.util.ui;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.global.JSScriptGlobalData;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.resources.instances.font.JSFont;
import api.scripting.coding.env.internal.util.screen.JSScreen;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingFloat;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingSlotI;
import api.scripting.coding.env.internal.util.ui.instances.*;
import api.system.scripting.JavaToJsAPI;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.*;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.system.settings.objects.SettingFloatBar;
import javagems3d.system.settings.objects.SettingSlot;
import org.joml.Vector2f;
import org.joml.Vector2i;

@JSCodingClass(binding = "JSUIDrawer", description = "...")
public class JSUIDrawer {
    @JSHideFromDoc private final JGemsUI jGemsUI;
    @JSHideFromDoc private final JSScreen screen;

    @JSHideFromDoc
    public JSUIDrawer(JGemsUI jGemsUI, JSScreen screen) {
        this.jGemsUI = jGemsUI;
        this.screen = screen;
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSUIText textUI(String text, JSFont guiFont, JSVector2f position, int hexColor, float zValue) {
        return new JSUIText(this.jGemsUI.textUI(text, guiFont.getJavaGuiFont(), new Vector2i((int) position.x(), (int) position.y()), hexColor, zValue));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSUIPictureStatic imageUI(ITexture2DProgram iImageSample, JSVector2f position, JSVector2f textureXY, JSVector2f textureWH, float zValue) {
        return new JSUIPictureStatic(this.jGemsUI.imageUI(iImageSample, position.toVec2i(), textureXY.toVec2f(), textureWH.toVec2f(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSUIPictureSizable imageUI(ITexture2DProgram iImageSample, JSVector2f position, JSVector2f size, float zValue) {
        return new JSUIPictureSizable(this.jGemsUI.imageUI(iImageSample, position.toVec2i(), size.toVec2i(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSUIDefaultButton buttonUI(String text, JSFont guiFont, JSVector2f position, JSVector2f size, int textColorHex, float zValue) {
        return new JSUIDefaultButton(this.jGemsUI.buttonUI(text, guiFont.getJavaGuiFont(), position.toVec2i(), size.toVec2i(), textColorHex, zValue));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSUISlider settingSliderUI(String text, JSFont guiFont, int hexColor, JSVector2f position, JSSettingFloat settingFloatBar, float zValue) {
        return new JSUISlider(this.jGemsUI.settingSliderUI(text, guiFont.getJavaGuiFont(), hexColor, position.toVec2i(), settingFloatBar.getJavaSetting(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public JSUICarousel settingCarouselUI(String text, JSFont guiFont, int hexColor, JSVector2f position, JSSettingSlotI settingIntSlots, float zValue) {
        return new JSUICarousel(this.jGemsUI.settingCarouselUI(text, guiFont.getJavaGuiFont(), hexColor, position.toVec2i(), settingIntSlots.getJavaSettingSlot(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"panelId"})
    public void setPanel(String panelId) {
        this.getJavaUI().setPanel(JavaToJsAPI.uiContainer.getPanelUIMap().get(panelId));
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void removeCurrentPanel() {
        this.getJavaUI().removePanel();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSScreen getScreen() {
        return this.screen;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JGemsUI getJavaUI() {
        return this.jGemsUI;
    }
}
