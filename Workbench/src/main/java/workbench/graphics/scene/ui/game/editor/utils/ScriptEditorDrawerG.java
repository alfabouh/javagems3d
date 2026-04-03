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
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.gaming.JGemsGaming;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import workbench.WBench;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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
    private final TextEditor aiPromptDoc;
    private ClassDocNode rootClassDoc;

    private final TextEditor textEditorGlobalVarsDocs;
    private Map<String, Integer> globalVarKeys;

    private final TextEditor textEditorEntryPointDocs;
    private Map<String, Integer> entryPointKeys;

    private StringBuilder aiPromptBuilder;

    public ScriptEditorDrawerG(APICodeEnvironmentController apiCodeEnvironmentController) {
        this.apiCodeEnvironmentController = apiCodeEnvironmentController;
        this.textEditor = new TextEditor();

        int[] paletteS = this.textEditor.getDarkPalette();
        paletteS[TextEditorPaletteIndex.Default] = 0xffffffff;
        paletteS[TextEditorPaletteIndex.Identifier] = 0xffffffff;
        this.textEditor.setPalette(paletteS);
        this.textEditor.setColorizerEnable(true);
        this.textEditor.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController(), true).getJsLang());

        this.textEditorClassesDocs = new TextEditor();
        this.textEditorClassesDocs.setPalette(this.textEditorClassesDocs.getDarkPalette());
        this.textEditorClassesDocs.setColorizerEnable(true);
        this.textEditorClassesDocs.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController(), false).getJsLang());
        this.textEditorClassesDocs.setReadOnly(true);

        this.textEditorGlobalVarsDocs = new TextEditor();
        this.textEditorGlobalVarsDocs.setPalette(this.textEditorClassesDocs.getDarkPalette());
        this.textEditorGlobalVarsDocs.setColorizerEnable(true);
        this.textEditorGlobalVarsDocs.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController(), false).getJsLang());
        this.textEditorGlobalVarsDocs.setReadOnly(true);

        this.textEditorEntryPointDocs = new TextEditor();
        this.textEditorEntryPointDocs.setPalette(this.textEditorClassesDocs.getDarkPalette());
        this.textEditorEntryPointDocs.setColorizerEnable(true);
        this.textEditorEntryPointDocs.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinition(JGemsAPI.getAPIScriptingCore().getGlobalGameContext().getApiCodeEnvironmentController(), false).getJsLang());
        this.textEditorEntryPointDocs.setReadOnly(true);

        this.aiPromptDoc = new TextEditor();
        this.aiPromptDoc.setPalette(paletteS);
        this.aiPromptDoc.setColorizerEnable(true);
        this.aiPromptDoc.setLanguageDefinition(new ScriptEditorDrawerG.JSDefinitionSimple().getJsLang());
        this.aiPromptDoc.setReadOnly(true);

        this.showDocID = 0;

        this.buildDocStrings();
        this.buildAIPrompt();
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

    private void buildAIPrompt() {
        this.aiPromptBuilder = new StringBuilder();
        final StringBuilder sb = this.aiPromptBuilder;

        sb.append("You are writing scripts for a Java-based game engine.\n");
        sb.append("All code must be written in JavaScript.\n");
        sb.append("The scripting system is powered by GraalVM.\n\n");

        sb.append("IMPORTANT:\n");
        sb.append("- The code below is Java-style pseudocode describing the available API.\n");
        sb.append("- You must ONLY use the classes, methods, and structures provided in this API.\n");
        sb.append("- Only functionality documented in the class and global variable docs is directly available.\n");
        sb.append("- BEFORE using Java.type(...), Java.extend(...), or any other Java interop, make ABSOLUTELY SURE that the same functionality is NOT already present in the API.\n");
        sb.append("- If the required functionality is missing in the API, ASK THE USER before using Java interop.\n");
        sb.append("- If something is unclear, either ASK THE USER or explicitly report that this functionality is not available.\n");
        sb.append("- DO NOT invent functions, classes, or behavior that are not present.\n");
        sb.append("- If there are inconsistencies or structural issues in the API, report them.\n");
        sb.append("- Respect Java-style access levels: do not access private members; only use public or documented APIs.\n\n");

        sb.append("OpenGL access:\n");
        sb.append("- All OpenGL constants and functions must be accessed through the global helper Js_GL.\n");
        sb.append("- Use Js_GL.getConst(\"CONSTANT_NAME\") to get constants, and Js_GL.callFunction(\"functionName\", args...) to call OpenGL functions.\n");
        sb.append("- Do not access GL11, GL30, GL46, or other LWJGL classes directly from scripts.\n");
        sb.append("- This ensures compatibility across OpenGL versions and avoids users needing to know which GL version contains a constant or function.\n\n");

        sb.append("Code style:\n");
        sb.append("- Write compact and efficient code.\n");
        sb.append("- DO NOT add comments inside the code.\n");
        sb.append("- Prefer composition over inheritance.\n");
        sb.append("- Avoid unnecessary abstractions.\n\n");

        sb.append("Behavior rules:\n");
        sb.append("- Always rely strictly on the provided API and global variables.\n");
        sb.append("- Do not assume hidden engine behavior.\n");
        sb.append("- Do not use external libraries unless explicitly available.\n");
        sb.append("- Event-driven architecture is critical: logic must be registered via game events.\n");
        sb.append("- All events are listed in JSGameRegistry and can be registered via gameRegistry.registerEvent(event, functionName).\n");
        sb.append("- Event handler functions receive a corresponding event instance as their only parameter.\n");
        sb.append("- All initialization, asset loading, UI logic, and behavior must be implemented through events.\n");
        sb.append("- Do not execute logic outside of registered event functions unless explicitly required.\n");
        sb.append("- If an API parameter expects a functional interface (Runnable, Consumer, etc.) and no argument specification is provided, assume it receives no parameters.\n");
        sb.append("- Always clear or dispose of resources after use (using clear(), destroy(), or JSRequiresClearResources) to prevent memory leaks.\n");
        sb.append("- Explicitly inform the user about required files, paths, or configurations whenever necessary.\n");
        sb.append("- Only include strictly necessary instructions for the user; avoid extraneous explanations or commentary.\n\n");

        sb.append("==== API DOCUMENTATION START ====\n\n");
        sb.append("- Always ensure that resources are properly cleaned up after use, especially using clear, destroy methods, or JSRequiresClearResources if available.\n");
        sb.append("- If user input or configuration is required (files, folders, settings), explicitly inform the user what and where to place or configure.\n");
        sb.append("- Generate instructions for the user in a clear, concise, and strictly actionable way. Do not include jokes or unnecessary commentary.\n");
        sb.append("- If anything is unclear or missing, ASK THE USER before generating code rather than guessing.\n");
        sb.append(this.textEditorClassesDocs.getText()).append("\n\n");
        sb.append(this.textEditorGlobalVarsDocs.getText()).append("\n\n");
        sb.append(this.textEditorEntryPointDocs.getText()).append("\n\n");
        sb.append("==== API DOCUMENTATION END ====\n\n");

        sb.append("==== MINIMAL EXAMPLE OF API USAGE ====\n");
        sb.append("// Demonstrates basic event subscription, UI registration, and asset loading.\n");
        sb.append("function JsSubscribeEvents(gameRegistry) {\n");
        sb.append("    gameRegistry.registerEvent(JSGameRegistry.INIT_ASSETS, \"onAssets\");\n");
        sb.append("    gameRegistry.registerEvent(JSGameRegistry.REGISTER_UI, \"onRegisterUI\");\n");
        sb.append("    gameRegistry.registerEvent(JSGameRegistry.REGISTER_UI_BEHAVIOUR, \"onUIBehaviour\");\n");
        sb.append("}\n\n");

        sb.append("function JsInit() {\n");
        sb.append("    // Initialization logic here\n");
        sb.append("}\n\n");

        sb.append("function onRegisterUI(e) {\n");
        sb.append("    e.registerMainMenuPanel(\"main_menu\");\n");
        sb.append("}\n\n");

        sb.append("function onUIBehaviour(e) {\n");
        sb.append("    e.registerUiBehaviour(\"main_menu\", JSUiBehaviourTargets.ON_DRAW, function() {\n");
        sb.append("        const ui = e.getUiDrawer();\n");
        sb.append("        const window = e.getWindow();\n");
        sb.append("        const size = window.getWindowSize();\n");
        sb.append("        const w = size.x(); const h = size.y();\n");
        sb.append("        ui.textUI(\"TEST MENU\", JSDefaultResources.buttonFont, new JSVector2f(10, h-35), 0x00ff00, 0.5);\n");
        sb.append("        ui.buttonUI(\"Play\", JSDefaultResources.buttonFont, new JSVector2f(w/2-150, h/2-30), new JSVector2f(300,60), 0xffffff, 0.5)\n");
        sb.append("            .setOnClick(function() { JSLog.warn(\"Play clicked\"); });\n");
        sb.append("    });\n");
        sb.append("}\n\n");

        sb.append("var grassTexture;\n");
        sb.append("function onAssets(e) {\n");
        sb.append("    grassTexture = e.createTexture2D(new JSPath(JSGlobal.getGameFolderPath(), \"game_assets\", \"test\", \"images.jpg\"));\n");
        sb.append("}\n");

        this.aiPromptDoc.setText(sb.toString());
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
                    final String toIns = function.funName() + "(" + Arrays.toString(function.funDescription().paramNames()) + ")";
                    if (ImGui.selectable(toIns)) {
                        editor.insertText(function.funName() + "()");
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
                if (ImGui.menuItem("AI Prompt", "##Script_docs_aiprm", this.showDocID == 3)) {
                    this.showDocID = 3;
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
                case 3: {
                    this.aiPromptDoc.render("TextEditor");
                    this.codeContext(4, this.aiPromptDoc);
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
                case 3: {
                    if (ImGui.button("Copy the text")) {
                        this.aiPromptDoc.copy();
                    }
                    if (ImGui.button("Export .txt")) {
                        String path = JGemsHelper.files().openFolderViewChooser("");
                        if (path != null && !path.isEmpty()) {
                            if (new File(path).exists()) {
                                File f = new File(path, "jgems_aiPrompt.txt");
                                try (java.io.FileWriter writer = new java.io.FileWriter(f)) {
                                    writer.write(this.aiPromptDoc.getText());
                                } catch (Exception e) {
                                    Log.get().exception(e);
                                }
                            } else {
                                Log.get().error("Couldn't find: " + path);
                                LoggingManager.showWindowWarn("Couldn't find: " + path);
                            }
                        }
                    }
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

    public StringBuilder getAiPromptBuilder() {
        return this.aiPromptBuilder;
    }

    private static class JSDefinitionSimple {
        private final TextEditorLanguageDefinition jsLang;

        public JSDefinitionSimple() {
            this.jsLang = new TextEditorLanguageDefinition();
            this.init();
        }

        private void init() {
            jsLang.setName("JavaScript");
            final Map<String, Integer> style = new HashMap<>();

            style.put(".*", TextEditorPaletteIndex.Default);

            jsLang.setTokenRegexStrings(style);
            jsLang.setAutoIdentation(true);
        }

        public TextEditorLanguageDefinition getJsLang() {
            return this.jsLang;
        }
    }

    private static class JSDefinition {
        private final TextEditorLanguageDefinition jsLang;
        private final APICodeEnvironmentController apiCodeEnvironmentController;
        private final boolean simplified;

        public JSDefinition(@NotNull APICodeEnvironmentController apiCodeEnvironmentController, boolean simplified) {
            this.jsLang = new TextEditorLanguageDefinition();
            this.apiCodeEnvironmentController = apiCodeEnvironmentController;
            this.simplified = simplified;
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
            if (!this.simplified) {
                this.apiCodeEnvironmentController.getClassRegistry().values().forEach(e -> {
                    e.functions().forEach(f -> {
                        identifiers.put(f.funName(), f.funDescription().description());
                    });
                });
            }
            this.apiCodeEnvironmentController.getEntryPointClass().functions().forEach(f -> {
                identifiers.put(f.funName(), f.funDescription().description());
            });

            //jsLang.setIdentifiers(identifiers);
            jsLang.setKeywords(Stream.concat(keyWords.stream(), Arrays.stream(keywords)).toArray(String[]::new));
            jsLang.setCommentStart("/*");
            jsLang.setCommentEnd("*/");
            jsLang.setSingleLineComment("//");

            final Set<String> allTypes = new HashSet<>(this.apiCodeEnvironmentController.getArgumentsMap().keySet());
            allTypes.add("java.lang.Enum");
            allTypes.add("java.lang.Record");
            allTypes.add("java.lang.Object");
            allTypes.addAll(this.apiCodeEnvironmentController.getFields());

            {
                String knownIdentifiersRegex = identifiers.keySet().stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining("|", "\\b(", ")\\b"));
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
            style.put("0[xX][0-9a-fA-F]+|[+-]?([0-9]*[.])?[0-9]+([eE][-+]?[0-9]+)?", TextEditorPaletteIndex.Number);
            {
                String exclude = String.join("|", new HashSet<String>() {{
                    addAll(allTypes);
                    addAll(identifiers.keySet());
                }});
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
