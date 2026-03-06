package workbench.graphics.scene.ui.map.editor.scenes.resources;

import api.scripting.legacy.doc.JGemsScriptingDocs;
import api.scripting.legacy.functions.APIScriptingFunction;
import api.scripting.legacy.functions.APIScriptsListing;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.help.JGemsHelper;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import workbench.WBench;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.project.map.WBenchMapProject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InterfaceResourcesScriptsM {
    private static JGemsScriptingDocs scriptingDocs;
    private static InterfaceResourcesScriptsM.CodeMode currentCodeMode;
    private final ImInt currentSelectedScript;
    private final ImString newScriptName;
    private final MapEditorInterface mapEditorInterface;
    private String scriptTextTemplate;

    static {
        InterfaceResourcesScriptsM.scriptingDocs = new JGemsScriptingDocs();
        InterfaceResourcesScriptsM.currentCodeMode = InterfaceResourcesScriptsM.CodeMode.CODE;
    }

    enum CodeMode {
        DOC,
        CODE
    }

    public InterfaceResourcesScriptsM(@NotNull MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.currentSelectedScript = new ImInt(-1);
        this.newScriptName = new ImString(64);
        this.reset();
    }

    public void reset() {
        this.getEditorInterface().getTextEditor().setPalette(this.getEditorInterface().getTextEditor().getDarkPalette());
        this.getEditorInterface().getTextEditor().setColorizerEnable(true);
        this.getEditorInterface().getTextEditor().setLanguageDefinition(new InterfaceResourcesScriptsM.JSDefinition().getJsLang());
    }

    public void render() {
        if (ImGui.collapsingHeader("Scripts")) {
            ImGui.treePush();
            final WBenchMapProject wBenchProject = WBench.get().getMapProjectManager().getCurrentMapProject();
            final List<String> scriptPaths = wBenchProject.getScriptFiles();

            List<String> itemsList = new ArrayList<>();
            itemsList.add("+ Create new script");
            itemsList.addAll(scriptPaths);
            final String[] items = itemsList.toArray(new String[0]);

            if (ImGui.combo("##Scripts", this.currentSelectedScript, items, 6)) {
                if (this.currentSelectedScript.get() == 0) {
                    this.currentSelectedScript.set(-1);
                    ImGui.openPopup("NewScriptPopup");
                    this.getEditorInterface().getTextEditor().setText("");
                } else {
                    wBenchProject.reviseScripts();
                    try {
                        final JGemsPath path = wBenchProject.getScriptPathTo(scriptPaths.get(this.currentSelectedScript.get() - 1));
                        final String readText = JGemsHelper.files().readTextFromFile(new JGemsPathSource(path, ISource.Source.OUTSIDE_JAR));
                        this.getEditorInterface().getTextEditor().setText(readText);
                        this.scriptTextTemplate = this.getEditorInterface().getTextEditor().getText();
                    } catch (Exception e) {
                        Log.get().exception(e);
                    }
                }
            }
            if (ImGui.beginPopup("NewScriptPopup")) {
                ImGui.text("Enter script name:");
                ImGui.inputText("##scriptName", this.newScriptName);

                if (ImGui.button("Create")) {
                    String name = this.newScriptName.get();
                    if (!name.isEmpty()) {
                        this.newScriptName.clear();
                        WBench.get().getMapProjectManager().getCurrentMapProject().createNewScript(name);
                        ImGui.closeCurrentPopup();
                    }
                }
                ImGui.sameLine();
                if (ImGui.button("Cancel")) {
                    this.newScriptName.clear();
                    ImGui.closeCurrentPopup();
                }

                ImGui.endPopup();
            }

            int realIndex = this.currentSelectedScript.get() - 1;
            if (realIndex >= 0 && realIndex < scriptPaths.size()) {
                String selectedPath = scriptPaths.get(realIndex);

                try {
                    if (ImGui.button("Folder")) {
                        Desktop.getDesktop().open(new File(wBenchProject.getScriptPathTo(selectedPath).getFullPath()).getParentFile());
                    }
                } catch (IOException e) {
                    Log.get().exception(e);
                }
                ImGui.sameLine();
                if (ImGui.button("Delete")) {
                    if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                        wBenchProject.deleteScript(realIndex);
                        this.currentSelectedScript.set(0);
                    }
                }

                final Vector2i wSize = this.getEditorInterface().getOpenGLRenderer().getWindow().getWindowSize();
                ImGui.setNextWindowPos(wSize.x / 2.0f - wSize.x / 4.0f, wSize.y / 2.0f - wSize.y / 4.0f, ImGuiCond.Appearing);
                ImGui.setNextWindowSize(wSize.x / 2.0f, wSize.y / 2.0f);
                ImBoolean scriptingOpened = new ImBoolean(true);
                boolean shouldSave = ProjectUIUtils.ctrlS();
                if (ImGui.begin("Scripting", scriptingOpened, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.MenuBar)) {
                    if (ImGui.beginMenuBar()) {
                        if (ImGui.beginMenu("View")) {
                            if (ImGui.selectable("Code", InterfaceResourcesScriptsM.currentCodeMode == CodeMode.CODE)) {
                                InterfaceResourcesScriptsM.currentCodeMode = CodeMode.CODE;
                            }
                            if (ImGui.selectable("Documentary", InterfaceResourcesScriptsM.currentCodeMode == CodeMode.DOC)) {
                                InterfaceResourcesScriptsM.currentCodeMode = CodeMode.DOC;
                            }
                            ImGui.endMenu();
                        }
                        if (ImGui.beginMenu("Actions")) {
                            if (this.getEditorInterface().getTextEditor().canUndo()) {
                                if (ImGui.menuItem("Undo")) {
                                    this.getEditorInterface().getTextEditor().undo(1);
                                }
                            }
                            if (this.getEditorInterface().getTextEditor().canRedo()) {
                                if (ImGui.menuItem("Redo")) {
                                    this.getEditorInterface().getTextEditor().redo(1);
                                }
                            }
                            ImGui.endMenu();
                        }
                        if (!this.scriptTextTemplate.equals(this.getEditorInterface().getTextEditor().getText()) && ImGui.button("Save")) {
                            shouldSave = true;
                        }
                        ImGui.endMenuBar();
                    }

                    if (shouldSave) {
                        final String textToSave = this.getEditorInterface().getTextEditor().getText();
                        WBench.get().getMapProjectManager().getCurrentMapProject().writeScriptFile(selectedPath, textToSave);
                        this.scriptTextTemplate = textToSave;
                    }
                }
                if (!scriptingOpened.get()) {
                    this.currentSelectedScript.set(-1);
                }
                switch (InterfaceResourcesScriptsM.currentCodeMode) {
                    case CODE: {
                        this.getEditorInterface().getTextEditor().render("TextEditor");
                        break;
                    }
                    case DOC: {
                        this.renderDocs();
                        break;
                    }
                }
                ImGui.end();
            }
            ImGui.treePop();
        }
    }

    private void renderDocs() {
        ImGui.separator();
        ImGui.text("Types");
        ImGui.separator();
        ImGui.treePush();

        for (JGemsScriptingDocs.ClassDesc classDesc : InterfaceResourcesScriptsM.scriptingDocs.getTypes()) {
            if (classDesc.parent() != null && !classDesc.parent().equals("Object")) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffffffc1);
                ImGui.textWrapped(classDesc.parent() + " ->");
                ImGui.popStyleColor();
                ImGui.sameLine();
            }
            ImGui.pushStyleColor(ImGuiCol.Text, 0xffc1ffc1);
            ImGui.textWrapped(classDesc.classSimpleName());
            ImGui.popStyleColor();
            ImGui.sameLine();

            ImGui.pushStyleColor(ImGuiCol.Text, 0xffa8a8a8);
            ImGui.textWrapped(classDesc.classVarName());
            ImGui.popStyleColor();
            ImGui.sameLine();

            ImGui.pushStyleColor(ImGuiCol.Text, 0xffaaffff);
            ImGui.textWrapped(classDesc.description());
            ImGui.popStyleColor();

            if (classDesc.globalVarName() != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                ImGui.bullet();
                ImGui.textWrapped("Global variable. var = " + classDesc.globalVarName());
                ImGui.popStyleColor();
            }

            if (classDesc.commentary() != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffc8c8c8);
                ImGui.bullet();
                ImGui.textWrapped(classDesc.commentary());
                ImGui.popStyleColor();
            }

            if (!classDesc.getMethods().isEmpty()) {
                for (JGemsScriptingDocs.MethodDesc methodDesc : classDesc.getMethods()) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffffaaff);

                    final String methodName = methodDesc.methodName();
                    final StringBuilder argsNames = new StringBuilder();
                    final StringBuilder argsTypes = new StringBuilder();

                    for (int i = 0; i < methodDesc.args().size(); i++) {
                        argsNames.append(methodDesc.args().get(i).getSecond());
                        argsTypes.append(methodDesc.args().get(i).getFirst());
                        if (i != methodDesc.args().size() - 1) {
                            argsNames.append(", ");
                            argsTypes.append(", ");
                        }
                    }

                    ImGui.bullet();
                    ImGui.textWrapped(methodName);
                    ImGui.popStyleColor();
                    ImGui.sameLine();
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.pushTextWrapPos(ImGui.getFontSize() * 35);
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xffaaffff);
                        ImGui.textWrapped(methodDesc.description());
                        ImGui.popStyleColor();
                        ImGui.popTextWrapPos();
                        ImGui.endTooltip();
                    }

                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffc1ffc1);
                    ImGui.textWrapped("(" + argsNames + ")");
                    ImGui.popStyleColor();
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xffc1ffc1);
                        ImGui.text("args: (" + argsTypes + ")");
                        ImGui.popStyleColor();
                        ImGui.endTooltip();
                    }
                    ImGui.sameLine();
                    if (methodDesc.returnValue() != null) {
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xffc8c8c8);
                        ImGui.textWrapped("Return: " + methodDesc.returnValue());
                        ImGui.popStyleColor();
                    }

                    if (methodDesc.commentary() != null) {
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xffc8c8c8);
                        ImGui.bullet();
                        ImGui.textWrapped(methodDesc.commentary());
                        ImGui.popStyleColor();
                        ImGui.newLine();
                    }
                }
            }

            ImGui.newLine();
        }
        ImGui.treePop();

        ImGui.separator();
        ImGui.text("Event handling functions");
        ImGui.separator();
        ImGui.treePush();

        for (APIScriptingFunction apiScriptingFunction : APIScriptsListing.getAllFunctions()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xffffaaff);
            ImGui.bullet();
            ImGui.textWrapped(apiScriptingFunction.getName());
            ImGui.popStyleColor();
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.pushTextWrapPos(ImGui.getFontSize() * 35);
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffaaffff);
                ImGui.textWrapped(apiScriptingFunction.getDescription());
                ImGui.popStyleColor();
                ImGui.popTextWrapPos();
                ImGui.endTooltip();
            }

            final StringBuilder funArgsNames = new StringBuilder();
            final StringBuilder funArgsTypes = new StringBuilder();

            for (int i = 0; i < apiScriptingFunction.getArgs().size(); i++) {
                funArgsNames.append(apiScriptingFunction.getArgs().get(i).getSecond());
                funArgsTypes.append(apiScriptingFunction.getArgs().get(i).getFirst().getSimpleName());
                if (i != apiScriptingFunction.getArgs().size() - 1) {
                    funArgsNames.append(", ");
                    funArgsTypes.append(", ");
                }
            }
            ImGui.sameLine();

            ImGui.pushStyleColor(ImGuiCol.Text, 0xffc1ffc1);
            ImGui.textWrapped("(" + funArgsNames + ")");
            ImGui.popStyleColor();
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffc1ffc1);
                ImGui.text("args: (" + funArgsTypes + ")");
                ImGui.popStyleColor();
                ImGui.endTooltip();
            }

            ImGui.newLine();
        }

        ImGui.treePop();
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }

    private static class JSDefinition {
        private final TextEditorLanguageDefinition jsLang;

        public JSDefinition() {
            this.jsLang = new TextEditorLanguageDefinition();
            this.init();
        }

        private void init() {
            jsLang.setName("JavaScript");

            final Set<String> keyWords = new HashSet<>();
            final Map<String, String> identifiers = new HashMap<>();
            final Map<String, Integer> style = new HashMap<>();
            final String[] keywords = new String[] {
                    "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete",
                    "do", "else", "export", "extends", "finally", "for", "function", "if", "import", "in",
                    "instanceof", "let", "new", "return", "super", "switch", "this", "throw", "try",
                    "typeof", "var", "void", "while", "with", "yield"
            };

            jsLang.setKeywords(keywords);
            for (APIScriptingFunction apiScriptingFunction : APIScriptsListing.getAllFunctions()) {
                identifiers.put(apiScriptingFunction.getName(), apiScriptingFunction.getDescription());
            }
            for (JGemsScriptingDocs.ClassDesc classDesc : InterfaceResourcesScriptsM.scriptingDocs.getTypes()) {
                identifiers.put(classDesc.classSimpleName(), classDesc.description());
                identifiers.put(classDesc.classVarName(), classDesc.description());
                for (JGemsScriptingDocs.MethodDesc methodDesc : classDesc.getMethods()) {
                    identifiers.put(methodDesc.methodName(), methodDesc.description());
                }
                if (classDesc.globalVarName() != null) {
                    identifiers.put(classDesc.globalVarName(), classDesc.description());
                }
            }
            jsLang.setIdentifiers(identifiers);

            jsLang.setCommentStart("/*");
            jsLang.setCommentEnd("*/");
            jsLang.setSingleLineComment("//");

            String knownIdentifiersRegex = identifiers.keySet().stream().filter(s -> s != null && !s.isEmpty()).map(Pattern::quote).collect(Collectors.joining("|", "\\b(", ")\\b"));
            style.put(knownIdentifiersRegex, TextEditorPaletteIndex.KnownIdentifier);

            style.put("\\b(Object|Function|Array|String|Boolean|Number|Math|Date|RegExp|JSON|Error|TypeError|ReferenceError)\\b", TextEditorPaletteIndex.KnownIdentifier);
            style.put("\\b(break|case|catch|class|const|continue|debugger|default|delete|do|else|export|extends|finally|for|function|if|import|in|instanceof|let|new|return|super|switch|this|throw|try|typeof|var|void|while|with|yield)\\b", TextEditorPaletteIndex.Keyword);
            style.put("[+-]?([0-9]*[.])?[0-9]+([eE][-+]?[0-9]+)?", TextEditorPaletteIndex.Number);
            style.put("'([^'\\\\]|\\\\.)*'", TextEditorPaletteIndex.String);
            style.put("\"([^\"\\\\]|\\\\.)*\"", TextEditorPaletteIndex.String);
            style.put("`([^`\\\\]|\\\\.)*`", TextEditorPaletteIndex.String);
            style.put("\\b[_a-zA-Z][_a-zA-Z0-9]*\\b", TextEditorPaletteIndex.Identifier);
            style.put("//.*", TextEditorPaletteIndex.Comment);
            style.put("/\\*.*?\\*/", TextEditorPaletteIndex.MultiLineComment);
            style.put("[\\[\\]{}();,.<>!=:+\\-*/%&|^~?]", TextEditorPaletteIndex.Punctuation);


            jsLang.setTokenRegexStrings(style);
            jsLang.setAutoIdentation(true);
        }

        public TextEditorLanguageDefinition getJsLang() {
            return this.jsLang;
        }
    }
}
