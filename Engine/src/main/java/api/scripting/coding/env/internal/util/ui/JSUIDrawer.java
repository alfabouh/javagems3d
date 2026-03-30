package api.scripting.coding.env.internal.util.ui;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.resources.instances.font.JSFont;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingFloat;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingSlotI;
import api.scripting.coding.env.internal.util.ui.instances.*;
import api.system.scripting.JavaToJsAPI;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import org.joml.Vector2i;

@JSCodingClass(binding = "JSUIDrawer", description = "UI drawing API used to create and control interface elements during rendering.")
public class JSUIDrawer {
    @JSHideFromDoc private final JGemsUI jGemsUI;
    @JSHideFromDoc private final JSScreen screen;

    @JSHideFromDoc
    public JSUIDrawer(JGemsUI jGemsUI, JSScreen screen) {
        this.jGemsUI = jGemsUI;
        this.screen = screen;
    }

    @JSCodingFunctionOrMethod(description = "Draw text on screen.", paramNames = {"text", "guiFont", "position", "hexColor", "zValue"})
    public JSUIText textUI(String text, JSFont guiFont, JSVector2f position, int hexColor, float zValue) {
        return new JSUIText(this.jGemsUI.textUI(text, guiFont.getJavaGuiFont(), new Vector2i((int) position.x(), (int) position.y()), hexColor, zValue));
    }

    @JSCodingFunctionOrMethod(description = "Draw static image using texture region (UV).", paramNames = {"image", "position", "textureXY", "textureWH", "zValue"})
    public JSUIPictureStatic imageUI(ITexture2DProgram iImageSample, JSVector2f position, JSVector2f textureXY, JSVector2f textureWH, float zValue) {
        return new JSUIPictureStatic(this.jGemsUI.imageUI(iImageSample, position.toVec2i(), textureXY.toVec2f(), textureWH.toVec2f(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "Draw image with specified size.", paramNames = {"image", "position", "size", "zValue"})
    public JSUIPictureSizable imageUI(ITexture2DProgram iImageSample, JSVector2f position, JSVector2f size, float zValue) {
        return new JSUIPictureSizable(this.jGemsUI.imageUI(iImageSample, position.toVec2i(), size.toVec2i(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "Create a clickable button.", paramNames = {"text", "guiFont", "position", "size", "textColorHex", "zValue"})
    public JSUIDefaultButton buttonUI(String text, JSFont guiFont, JSVector2f position, JSVector2f size, int textColorHex, float zValue) {
        return new JSUIDefaultButton(this.jGemsUI.buttonUI(text, guiFont.getJavaGuiFont(), position.toVec2i(), size.toVec2i(), textColorHex, zValue));
    }

    @JSCodingFunctionOrMethod(description = "Create slider bound to float setting.", paramNames = {"text", "guiFont", "hexColor", "position", "setting", "zValue"})
    public JSUISlider settingSliderUI(String text, JSFont guiFont, int hexColor, JSVector2f position, JSSettingFloat settingFloatBar, float zValue) {
        return new JSUISlider(this.jGemsUI.settingSliderUI(text, guiFont.getJavaGuiFont(), hexColor, position.toVec2i(), settingFloatBar.getJavaSetting(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "Create carousel (selector) bound to slot-based setting.", paramNames = {"text", "guiFont", "hexColor", "position", "setting", "zValue"})
    public JSUICarousel settingCarouselUI(String text, JSFont guiFont, int hexColor, JSVector2f position, JSSettingSlotI settingIntSlots, float zValue) {
        return new JSUICarousel(this.jGemsUI.settingCarouselUI(text, guiFont.getJavaGuiFont(), hexColor, position.toVec2i(), settingIntSlots.getJavaSettingSlot(), zValue));
    }

    @JSCodingFunctionOrMethod(description = "Switch active UI panel by its id.", paramNames = {"panelId"})
    public void setPanel(String panelId) {
        this.getJavaUI().setUiPanel(JavaToJsAPI.uiContainer.getPanelUIMap().get(panelId));
    }

    @JSCodingFunctionOrMethod(description = "Remove current UI panel.")
    public void removeCurrentPanel() {
        this.getJavaUI().removePanel();
    }

    @JSCodingFunctionOrMethod(description = "Get current screen.")
    public JSScreen getScreen() {
        return this.screen;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java UI instance.")
    public JGemsUI getJavaUI() {
        return this.jGemsUI;
    }
}