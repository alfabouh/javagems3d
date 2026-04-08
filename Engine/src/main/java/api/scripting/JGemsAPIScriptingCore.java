package api.scripting;

import api.scripting.coding.APICodingContext;
import api.system.JGemsAPI;
import api.system.scripting.JavaToJsFunctionsList;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class JGemsAPIScriptingCore implements Closeable {
    public static final String LAN = "js";
    private APICodingContext globalGameContext;
    private APICodingContext localMapContext;
    private JGemsPathSource absolutePath;

    public JGemsAPIScriptingCore() {
        this.globalGameContext = new APICodingContext();
        this.localMapContext = new APICodingContext();
    }

    public void scanJavaCodeGame() {
        final Set<String> packs = new HashSet<>();
        packs.add("api.scripting.coding.env.internal.game");
        packs.add("api.scripting.coding.env.internal.util");
        packs.addAll(JGemsAPI.getManager().getAppScriptRegistry().globalGameContextScripts);
        this.getGlobalGameContext().getApiCodeEnvironmentController().scan(packs.toArray(new String[0]));
    }

    public void scanJavaCodeMap() {
        final Set<String> packs = new HashSet<>();
        packs.add("api.scripting.coding.env.internal.map");
        packs.add("api.scripting.coding.env.internal.util");
        packs.addAll(JGemsAPI.getManager().getAppScriptRegistry().globalGameContextScripts);
        this.getLocalMapContext().getApiCodeEnvironmentController().scan(packs.toArray(new String[0]));
    }

    public void initGame(@NotNull JGemsPath absolutePathToSeekEntries) {
        Log.get().info("Init game scripting engine...");
        this.getGlobalGameContext().init();
        this.getGlobalGameContext().entry(absolutePathToSeekEntries);
        this.scanJavaCodeGame();
        this.getGlobalGameContext().callFunctionNoExc(JavaToJsFunctionsList.ENTRY_POINT_FUNCTION);
        Log.get().info("Init game scripting engine. Success.");
    }

    public void initMap(@NotNull JGemsPath absolutePathToSeekEntries) {
        Log.get().info("Init map scripting engine...");
        this.getLocalMapContext().init();
        this.getLocalMapContext().entry(absolutePathToSeekEntries);
        this.scanJavaCodeMap();
        this.getLocalMapContext().callFunctionNoExc(JavaToJsFunctionsList.ENTRY_POINT_FUNCTION);
        Log.get().info("Init map scripting engine. Success.");
    }

    public void clearGame() {
        this.globalGameContext.close();
    }

    public void clearMap() {
        this.localMapContext.close();
    }

    public void initCodeBase(@NotNull JGemsPathSource absolutePath) {
        this.absolutePath = absolutePath;
    }

    public JGemsPathSource getAbsolutePath() {
        return this.absolutePath;
    }

    public APICodingContext getGlobalGameContext() {
        return this.globalGameContext;
    }

    public JGemsAPIScriptingCore setGlobalGameContext(APICodingContext globalGameContext) {
        this.globalGameContext = globalGameContext;
        return this;
    }

    public APICodingContext getLocalMapContext() {
        return this.localMapContext;
    }

    public JGemsAPIScriptingCore setLocalMapContext(APICodingContext localMapContext) {
        this.localMapContext = localMapContext;
        return this;
    }

    @Override
    public void close() throws IOException {
        this.getGlobalGameContext().close();
        this.getLocalMapContext().close();
    }
}