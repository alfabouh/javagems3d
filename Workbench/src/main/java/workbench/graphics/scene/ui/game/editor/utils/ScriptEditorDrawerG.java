package workbench.graphics.scene.ui.game.editor.utils;

import api.scripting.coding.env.APICodeEnvironmentController;
import api.system.JGemsAPI;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.system.external.gaming.JGemsGaming;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import workbench.WBench;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptEditorDrawerG {
    private final TextEditor textEditor;

    private final APICodeEnvironmentController apiCodeEnvironmentController;
    private ScriptAssetPreview scriptPreviewObject;
    private String initText;
    private boolean showDocs;

    private int showDocID;

    private final TextEditor textEditorClassesDocs;
    private ClassDocNode rootClassDoc;

    private final TextEditor textEditorGlobalVarsDocs;
    private Map<String, Integer> globalVarKeys;

    private final TextEditor textEditorEntryPointDocs;
    private Map<String, Integer> entryPointKeys;

    public ScriptEditorDrawerG(APICodeEnvironmentController apiCodeEnvironmentController) {
        this.apiCodeEnvironmentController = apiCodeEnvironmentController;
        this.textEditor = new TextEditor();

        this.textEditor.setPalette(this.textEditor.getDarkPalette());
        this.textEditor.setColorizerEnable(true);
        this.textEditor.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController()).getJsLang());

        this.textEditorClassesDocs = new TextEditor();
        this.textEditorClassesDocs.setPalette(this.textEditorClassesDocs.getDarkPalette());
        this.textEditorClassesDocs.setColorizerEnable(true);
        this.textEditorClassesDocs.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController()).getJsLang());
        this.textEditorClassesDocs.setReadOnly(true);

        this.textEditorGlobalVarsDocs = new TextEditor();
        this.textEditorGlobalVarsDocs.setPalette(this.textEditorClassesDocs.getDarkPalette());
        this.textEditorGlobalVarsDocs.setColorizerEnable(true);
        this.textEditorGlobalVarsDocs.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController()).getJsLang());
        this.textEditorGlobalVarsDocs.setReadOnly(true);

        this.textEditorEntryPointDocs = new TextEditor();
        this.textEditorEntryPointDocs.setPalette(this.textEditorClassesDocs.getDarkPalette());
        this.textEditorEntryPointDocs.setColorizerEnable(true);
        this.textEditorEntryPointDocs.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController()).getJsLang());
        this.textEditorEntryPointDocs.setReadOnly(true);

        this.showDocID = 0;

        this.buildDocStrings();
    }

    private void buildDocStrings() {
        this.rootClassDoc = new ClassDocNode(new HashMap<>(), new HashMap<>());
        this.globalVarKeys = new HashMap<>();
        this.entryPointKeys = new HashMap<>();

        {
            StringBuilder stringBuilder = new StringBuilder();
            this.apiCodeEnvironmentController.getClassRegistry().forEach((k, v) -> {
                {
                    String[] parts = v.path().split("/");
                    ClassDocNode current = this.rootClassDoc;
                    for (String part : parts) {
                        current = current.children.computeIfAbsent(part, s -> new ClassDocNode(new HashMap<>(), new HashMap<>()));
                    }
                    current.classes.put(k, (int) stringBuilder.chars().filter(c -> c == '\n').count() + 1);
                }
                stringBuilder.append(v.docBuilder().toString());
                stringBuilder.append("\n\n");
            });
            this.textEditorClassesDocs.setText(stringBuilder.toString().trim());
        }

        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("// Just global variables.\n");
            this.apiCodeEnvironmentController.getGlobalVarFactoryKeys().values().forEach((k) -> {
                final APICodeEnvironmentController.JSClassData classData = this.apiCodeEnvironmentController.getClassRegistry().get(k.varKey());
                this.globalVarKeys.put(k.varName(), (int) stringBuilder.chars().filter(c -> c == '\n').count());
                stringBuilder.append("var ").append(k.varName()).append(" = new ").append(k.varKey()).append("(...);").append(" // ").append(classData.codingClass().description());
                stringBuilder.append("\n\n");
            });
            this.textEditorGlobalVarsDocs.setText(stringBuilder.toString().trim());
        }

        {
            String stringBuilder = "\n// Entry point - main file of JS Scripting system.\n" + this.apiCodeEnvironmentController.getEntryPointClass().sampleCode();
            this.textEditorEntryPointDocs.setText(stringBuilder.trim());
        }
    }

    public boolean canBeSaved() {
        return this.scriptPreviewObject != null && !this.textEditor.getText().trim().equals(this.initText.trim());
    }

    public void save() {
        if (this.canBeSaved()) {
            this.scriptPreviewObject.getAsset().save(JGemsGaming.getScriptsFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), this.textEditor.getText());
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
        final float fullH = ImGui.getWindowHeight() - 60;
        final float fullW = ImGui.getWindowWidth();
        ImGui.beginChild("##scripting_editor", fullW, this.showDocs ? fullH * 0.5f : fullH, true, ImGuiWindowFlags.MenuBar);
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
        this.codeContext(0, this.textEditor);
        this.codeCompletionContext(this.textEditor, 0, () -> ImGui.isKeyPressed(GLFW.GLFW_KEY_PERIOD), this::getWordBeforeCursor, (editor, word) -> {
            APICodeEnvironmentController.JSClassData classData = this.apiCodeEnvironmentController.getClassRegistry().get(word);
            if (classData == null) {
                final APICodeEnvironmentController.JSGlobalVarData globalVarData = this.apiCodeEnvironmentController.getGlobalVarFactoryKeys().get(word);
                if (globalVarData != null) {
                    classData = this.apiCodeEnvironmentController.getClassRegistry().get(globalVarData.varKey());
                }
            }
            if (classData != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff66ff66);
                classData.functions().forEach((function) -> {
                    final String toIns = function.funName() + "()";
                    if (ImGui.selectable(toIns)) {
                        editor.insertText(toIns);
                        editor.setCursorPosition(editor.getCursorPositionLine(), editor.getCursorPositionColumn() - 1);
                    }
                });
                ImGui.popStyleColor();
                return true;
            }
            return false;
        });
        this.codeCompletionContext(this.textEditor, 1, this::isLetterPressed, this::getWordBeforeCursor, (editor, word) -> {
            if (word.length() <= 2) {
                return false;
            }
            boolean flag = false;
            {
                final Set<String> matchWords1 = this.apiCodeEnvironmentController.getClassRegistry().keySet().stream().filter(e -> e.startsWith(word)).collect(Collectors.toSet());
                if (!matchWords1.isEmpty()) {
                    flag = true;
                }
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff6666ff);
                matchWords1.forEach(e -> {
                    if (ImGui.selectable(e)) {
                        editor.insertText(e.substring(word.length()));
                    }
                });
                ImGui.popStyleColor();
            }
            {
                final Set<String> matchWords2 = this.apiCodeEnvironmentController.getGlobalVarFactoryKeys().values().stream().map(APICodeEnvironmentController.JSGlobalVarData::varName).filter(e -> e.startsWith(word)).collect(Collectors.toSet());
                if (!matchWords2.isEmpty()) {
                    flag = true;
                }
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffff6666);
                matchWords2.forEach(e -> {
                    if (ImGui.selectable(e)) {
                        editor.insertText(e.substring(word.length()));
                    }
                });
                ImGui.popStyleColor();
            }
            return flag;
        });
        ImGui.endChild();

        if (this.showDocs) {
            ImGui.beginChild("##scripting_docs", fullW * 0.7f, fullH * 0.5f, true, ImGuiWindowFlags.MenuBar);
            if (ImGui.beginMenuBar()) {
                if (ImGui.menuItem("Classes", "##Script_docs_classes", this.showDocID == 0)) {
                    this.showDocID = 0;
                }
                if (ImGui.menuItem("Global Variables", "##Script_docs_glvars", this.showDocID == 1)) {
                    this.showDocID = 1;
                }
                if (ImGui.menuItem("Entry Point", "##Script_docs_evfun", this.showDocID == 2)) {
                    this.showDocID = 2;
                }
                ImGui.endMenuBar();
            }
            switch (this.showDocID) {
                case 0: {
                    this.textEditorClassesDocs.render("TextEditor");
                    this.codeContext(1, this.textEditorClassesDocs);
                    break;
                }
                case 1: {
                    this.textEditorGlobalVarsDocs.render("TextEditor");
                    this.codeContext(2, this.textEditorGlobalVarsDocs);
                    break;
                }
                case 2: {
                    this.textEditorEntryPointDocs.render("TextEditor");
                    this.codeContext(3, this.textEditorEntryPointDocs);
                    break;
                }
            }
            ImGui.endChild();
            ImGui.sameLine();
            ImGui.beginChild("##scripting_docs_heads", ImGui.getColumnWidth(), fullH * 0.5f, true, ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.HorizontalScrollbar);
            if (ImGui.beginMenuBar()) {
                ImGui.text("Items");
                ImGui.endMenuBar();
            }
            AtomicInteger i = new AtomicInteger(1);
            switch (this.showDocID) {
                case 0: {
                    this.renderClassDocHierarchy(true, i, this.rootClassDoc, this.textEditorClassesDocs);
                    break;
                }
                case 1: {
                    this.globalVarKeys.forEach((k, v) -> {
                        if (ImGui.selectable(i.getAndIncrement() + ". " + k)) {
                            this.textEditorGlobalVarsDocs.setCursorPosition(v, 0);
                        }
                    });
                    break;
                }
                case 2: {
                    ImGui.text("Nothing to show.");
                    break;
                }
            }
            ImGui.endChild();
        }
    }

    private void renderClassDocHierarchy(boolean root, AtomicInteger i, ClassDocNode classDocNode, TextEditor textEditor) {
        classDocNode.classes().forEach((k, v) -> {
            ImGui.bullet();
            if (ImGui.selectable(k)) {
                textEditor.setCursorPosition(v, 0);
            }
        });

        classDocNode.children().forEach((k, v) -> {
            ImGui.pushStyleColor(ImGuiCol.Text, root ? 0xff00ff00 : 0xff6666ff);
            if (ImGui.treeNodeEx(k.isEmpty() ? "Misc" : k.toUpperCase(Locale.ROOT), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.popStyleColor();
                this.renderClassDocHierarchy(false, i, v, textEditor);
                ImGui.treePop();
            } else {
                ImGui.popStyleColor();
            }
        });
    }

    private boolean isLetterPressed() {
        for (int key = GLFW.GLFW_KEY_A; key <= GLFW.GLFW_KEY_Z; key++) {
            if (ImGui.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    private String getWordBeforeCursor(TextEditor editor) {
        String line = editor.getCurrentLineText();
        int cursor = editor.getCursorPositionColumn();
        if (line == null || line.isEmpty()) {
            return "";
        }
        cursor = Math.min(cursor, line.length());
        int i = cursor - 1;
        while (i >= 0 && !(Character.isSpaceChar(line.charAt(i)) || line.charAt(i) == '(')) {
            i--;
        }
        return line.substring(i + 1, cursor).replaceAll("\t", "").replaceAll("\\.", "").replaceAll("\\)o", "");
    }

    private void codeCompletionContext(TextEditor editor, int id, Supplier<Boolean> openPopup, Function<TextEditor, String> getStr, BiFunction<TextEditor, String, Boolean> onWordMatch) {
        String popup = "##AutoCompletionPeriod_" + id;

        if (openPopup.get()) {
            ImGui.openPopup(popup);
        }

        float charWidth = ImGui.calcTextSize("A").x;
        float lineHeight = ImGui.getTextLineHeight();

        ImGui.setNextWindowPos(ImGui.getWindowPosX() + (editor.getCursorPositionColumn() * charWidth) + 70.0f, ImGui.getWindowPosY() + (editor.getCursorPositionLine() * lineHeight) + 30.0f);
        if (ImGui.beginPopup(popup, ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav)) {
            String word = getStr.apply(editor);
            if (!onWordMatch.apply(editor, word)) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }

    private void codeContext(int id, TextEditor textEditor) {
        if (ImGui.beginPopupContextItem("rightMouseContext_" + id)) {
            {
                ImGui.beginDisabled(!this.canBeSaved());
                if (ImGui.selectable("Save")) {
                    this.save();
                }
                ImGui.endDisabled();
            }
            ImGui.separator();
            {
                ImGui.beginDisabled(!textEditor.hasSelection());
                if (ImGui.selectable("Copy")) {
                    textEditor.copy();
                }
                if (ImGui.selectable("Cut")) {
                    textEditor.cut();
                }
                ImGui.endDisabled();
            }
            ImGui.separator();
            {
                {
                    ImGui.beginDisabled(!textEditor.canUndo());
                    if (ImGui.selectable("Undo")) {
                        textEditor.undo(1);
                    }
                    ImGui.endDisabled();
                }
                {
                    ImGui.beginDisabled(!textEditor.canRedo());
                    if (ImGui.selectable("Redo")) {
                        textEditor.redo(1);
                    }
                    ImGui.endDisabled();
                }
            }
            ImGui.separator();
            {
                if (ImGui.selectable("SelectAll")) {
                    textEditor.selectAll();
                }
                if (ImGui.selectable("Paste")) {
                    textEditor.paste();
                }
            }
            ImGui.endPopup();
        }
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

            final Set<String> functions = new HashSet<>();
            final Set<String> keyWords = new HashSet<>();
            final Map<String, String> identifiers = new HashMap<>();
            final Map<String, Integer> style = new HashMap<>();
            final String[] keywords = new String[]{
                    "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete",
                    "do", "else", "export", "extends", "finally", "for", "function", "if", "import", "in",
                    "instanceof", "let", "new", "return", "super", "switch", "this", "throw", "try",
                    "typeof", "var", "void", "while", "with", "yield", "public", "private", "final",
                    "protected", "interface", "abstract", "static", "record", "enum", "implements", "null", "Java"
            };
            this.apiCodeEnvironmentController.getGlobalVarFactoryKeys().values().forEach(e -> {
                identifiers.put(e.varName(), this.apiCodeEnvironmentController.getClassRegistry().get(e.varKey()).codingClass().description());
            });
            this.apiCodeEnvironmentController.getClassRegistry().forEach((k, v) -> {
                identifiers.put(k, v.codingClass().description());
            });
            this.apiCodeEnvironmentController.getClassRegistry().values().forEach(e -> {
                e.functions().forEach(f -> {
                    identifiers.put(f.funName(), f.funDescription().description());
                });
            });
            this.apiCodeEnvironmentController.getEntryPointClass().functions().forEach(f -> {
                identifiers.put(f.funName(), f.funDescription().description());
            });

            jsLang.setIdentifiers(identifiers);
            jsLang.setKeywords(Stream.concat(keyWords.stream(), Arrays.stream(keywords)).toArray(String[]::new));
            jsLang.setCommentStart("/*");
            jsLang.setCommentEnd("*/");
            jsLang.setSingleLineComment("//");

            final Set<String> allTypes = new HashSet<>(this.apiCodeEnvironmentController.getArgumentsMap().keySet());
            allTypes.add("java.lang.Enum");
            allTypes.add("java.lang.Record");
            allTypes.addAll(this.apiCodeEnvironmentController.getFields());

            {
                String knownIdentifiersRegex = identifiers.keySet().stream().filter(s -> s != null && !s.isEmpty()).map(Pattern::quote).collect(Collectors.joining("|", "\\b(", ")\\b"));
                style.put(knownIdentifiersRegex, TextEditorPaletteIndex.KnownIdentifier);
            }
            {
                String knownStringsRegex = allTypes.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining("|", "\\b(", ")\\b"));
                style.put(knownStringsRegex, TextEditorPaletteIndex.CharLiteral);
            }
            //{
            //    String knownFunctionsRegex = functions.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining("|", "\\b(", ")\\b"));
            //    style.put(knownFunctionsRegex, TextEditorPaletteIndex.CharLiteral);
            //}

            style.put("\\b(Object|Function|Array|Number|Date|Exception|Error|TypeError|ReferenceError|" +
                          "Boolean|Byte|Short|Integer|Long|Float|Double|Character|" +
                          "boolean|byte|short|int|long|float|double|char)\\b", TextEditorPaletteIndex.KnownIdentifier);
            //style.put("\\b(break|case|catch|class|const|continue|debugger|default|delete|do|else|export|extends|finally|for|function|if|import|in|instanceof|let|new|return|super|switch|this|throw|try|typeof|var|void|while|with|yield)\\b", TextEditorPaletteIndex.Keyword);
            style.put("[+-]?([0-9]*[.])?[0-9]+([eE][-+]?[0-9]+)?", TextEditorPaletteIndex.Number);
            {
                String exclude = String.join("|", allTypes);
                String identifierRegex = "\\b(?!(" + exclude + ")\\b)[_a-zA-Z][_a-zA-Z0-9]*\\b";
                style.put(identifierRegex, TextEditorPaletteIndex.Identifier);
            }
            style.put("'([^'\\\\]|\\\\.)*'", TextEditorPaletteIndex.String);
            style.put("\"([^\"\\\\]|\\\\.)*\"", TextEditorPaletteIndex.String);
            style.put("`([^`\\\\]|\\\\.)*`", TextEditorPaletteIndex.String);
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

    record ClassDocNode(Map<String, ClassDocNode> children, Map<String, Integer> classes) {};
}
