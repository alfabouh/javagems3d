package api.scripting.coding.env.internal.util.ui;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;

@JSCodingClass(binding = "JSImGui", description = "Utility wrapper for commonly used ImGui functions. You can access all functions via Java.type(\"imgui.ImGui\")")
public class JSImGui implements JSGlobalVarFactory<JSImGui> {
    @JSCodingFunctionOrMethod(description = "Display text.")
    public static void text(String text) {
        ImGui.text(text);
    }

    @JSCodingFunctionOrMethod(description = "Display colored text.", paramNames = {"r","g","b","a","text"})
    public static void textColored(float r, float g, float b, float a, String text) {
        ImGui.textColored(r, g, b, a, text);
    }

    @JSCodingFunctionOrMethod(description = "Draw separator line.")
    public static void separator() {
        ImGui.separator();
    }

    @JSCodingFunctionOrMethod(description = "Same line.")
    public static void sameLine() {
        ImGui.sameLine();
    }

    @JSCodingFunctionOrMethod(description = "Add spacing.")
    public static void spacing() {
        ImGui.spacing();
    }

    @JSCodingFunctionOrMethod(description = "Create button.", paramNames = {"label"})
    public static boolean button(String label) {
        return ImGui.button(label);
    }

    @JSCodingFunctionOrMethod(description = "Create checkbox.", paramNames = {"label","value"})
    public static boolean checkbox(String label, boolean value) {
        if (ImGui.checkbox(label, value)) {
            return !value;
        }
        return value;
    }

    @JSCodingFunctionOrMethod(description = "Input text field.", paramNames = {"label","value"})
    public static String inputText(String label, String value) {
        ImString str = new ImString(value, 256);
        if (ImGui.inputText(label, str)) {
            return str.get();
        }
        return value;
    }

    @JSCodingFunctionOrMethod(description = "Drag float.", paramNames = {"label","value","speed","min","max"})
    public static float dragFloat(String label, float value, float speed, float min, float max) {
        float[] v = new float[]{value};
        if (ImGui.dragFloat(label, v, speed, min, max)) {
            return v[0];
        }
        return value;
    }

    @JSCodingFunctionOrMethod(description = "Drag int.", paramNames = {"label","value","speed","min","max"})
    public static int dragInt(String label, int value, int speed, int min, int max) {
        int[] v = new int[]{value};
        if (ImGui.dragInt(label, v, speed, min, max)) {
            return v[0];
        }
        return value;
    }

    @JSCodingFunctionOrMethod(description = "Color editor (RGB).", paramNames = {"label","r","g","b"})
    public static float[] colorEdit3(String label, float r, float g, float b) {
        float[] col = new float[]{r, g, b};
        if (ImGui.colorEdit3(label, col)) {
            return col;
        }
        return new float[]{r, g, b};
    }

    @JSCodingFunctionOrMethod(description = "Color editor (RGBA).", paramNames = {"label","r","g","b","a"})
    public static float[] colorEdit4(String label, float r, float g, float b, float a) {
        float[] col = new float[]{r, g, b, a};
        if (ImGui.colorEdit4(label, col)) {
            return col;
        }
        return new float[]{r, g, b, a};
    }

    @JSCodingFunctionOrMethod(description = "Combo box.", paramNames = {"label","currentIndex","items"})
    public static int combo(String label, int currentIndex, String[] items) {
        ImInt index = new ImInt(currentIndex);
        if (ImGui.combo(label, index, items)) {
            return index.get();
        }
        return currentIndex;
    }

    @JSCodingFunctionOrMethod(description = "Create window begin.", paramNames = {"name"})
    public static boolean begin(String name) {
        return ImGui.begin(name);
    }

    @JSCodingFunctionOrMethod(description = "End window.")
    public static void end() {
        ImGui.end();
    }

    @JSHideFromDoc
    @Override
    public JSImGui newGlobalVar() {
        return new JSImGui();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "Js_ImGui";
    }
}