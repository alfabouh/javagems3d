package workbench.graphics.scene.ui.game.editor.utils;

import api.scripting.coding.env.APICodeEnvironmentController;
import api.system.JGemsAPI;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiWindowFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import workbench.WBench;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.game.editor.ActionsInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptEditorDrawerG {
    private final TextEditor textEditor;
    private final ActionsInterfaceComponentG actionsInterfaceComponentG;
    private ScriptAssetPreview scriptPreviewObject;
    private String initText;
    private boolean showDocs;

    public ScriptEditorDrawerG(ActionsInterfaceComponentG actionsInterfaceComponentG) {
        this.actionsInterfaceComponentG = actionsInterfaceComponentG;
        this.textEditor = new TextEditor();

        this.textEditor.setPalette(this.textEditor.getDarkPalette());
        this.textEditor.setColorizerEnable(true);
        this.textEditor.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController()).getJsLang());
    }

    public boolean canBeSaved() {
        return this.scriptPreviewObject != null && !this.textEditor.getText().trim().equals(this.initText.trim());
    }

    public void save() {
        if (this.canBeSaved()) {
            this.scriptPreviewObject.getAsset().save(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath(), this.textEditor.getText());
            this.initText = this.textEditor.getText();
        }
    }

    public void setScriptPreviewObject(@Nullable ScriptAssetPreview scriptPreviewObject) {
        this.scriptPreviewObject = scriptPreviewObject;
        if (scriptPreviewObject != null) {
            this.textEditor.setText(scriptPreviewObject.getAsset().scriptText());
            this.initText = scriptPreviewObject.getAsset().scriptText();
        }
    }

    public void render(ScriptAssetPreview scriptAssetPreview) {
        if (this.scriptPreviewObject == null) {
            return;
        }
        if (ProjectUIUtils.ctrlS()) {
            this.save();
        }
        ImGui.beginChild("##scripting_editor", 0, 0, true, ImGuiWindowFlags.MenuBar);
        if (ImGui.beginMenuBar()) {
            if (ImGui.menuItem("Docs", "##Script_docs", this.showDocs)) {
                this.showDocs = !this.showDocs;
            }
            ImGui.beginDisabled(!this.canBeSaved());
            if (ImGui.button("Save")) {
                this.save();
            }
            ImGui.endDisabled();
            {
                ImGui.beginDisabled(!this.textEditor.canUndo());
                if (ImGui.menuItem("<---")) {
                    this.textEditor.undo(1);
                }
                ImGui.endDisabled();
            }
            {
                ImGui.beginDisabled(!this.textEditor.canRedo());
                if (ImGui.menuItem("--->")) {
                    this.textEditor.redo(1);
                }
                ImGui.endDisabled();
            }
            ImGui.endMenuBar();
        }
        this.textEditor.render("TextEditor");
        if (ImGui.isItemHovered() || ImGui.isItemActive()) {
            ImGui.setMouseCursor(ImGuiMouseCursor.TextInput);
        }
        ImGui.endChild();
    }

    private static class JSDefinition {
        private final TextEditorLanguageDefinition jsLang;
        private final APICodeEnvironmentController apiCodeEnvironmentController;

        public JSDefinition(@NotNull APICodeEnvironmentController apiCodeEnvironmentController) {
            this.jsLang = new TextEditorLanguageDefinition();
            this.apiCodeEnvironmentController = apiCodeEnvironmentController;
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
            this.apiCodeEnvironmentController.getClassRegistry().forEach((k, v) -> {
                identifiers.put(k, v.codingClass().description());
            });
            this.apiCodeEnvironmentController.getClassRegistry().values().forEach(e -> {
                e.functions().forEach(f -> {
                    identifiers.put(f.funName(), f.funDescription().description());
                });
            });
            keyWords.addAll(this.apiCodeEnvironmentController.getGlobalVarFactoryKeys());
            this.apiCodeEnvironmentController.getEntryPointClass().functions().forEach(f -> {
                identifiers.put(f.funName(), f.funDescription().description());
            });

            jsLang.setIdentifiers(identifiers);
            jsLang.setKeywords(Stream.concat(keyWords.stream(), Arrays.stream(keywords)).toArray(String[]::new));
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
