package api.scripting;

import api.scripting.coding.APICodingContext;
import api.scripting.coding.env.functions.JavaToJSFunctionsList;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

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
        this.getGlobalGameContext().getApiCodeEnvironmentController().scan("api.scripting.coding.env.internal.game", "api.scripting.coding.env.internal.util");
    }

    public void scanJavaCodeMap() {
        this.getLocalMapContext().getApiCodeEnvironmentController().scan("api.scripting.coding.env.internal.map", "api.scripting.coding.env.internal.util");
    }

    public void initGame(@NotNull JGemsPath absolutePathToSeekEntries) {
        Log.get().info("Init game scripting engine...");
        this.getGlobalGameContext().init();
        this.getGlobalGameContext().entry(absolutePathToSeekEntries);
        this.scanJavaCodeGame();
        this.getGlobalGameContext().callFunctionNoExc(JavaToJSFunctionsList.ENTRY_POINT_FUNCTION);
        Log.get().info("Init game scripting engine. Success.");
    }

    public void initMap(@NotNull JGemsPath absolutePathToSeekEntries) {
        Log.get().info("Init map scripting engine...");
        this.getLocalMapContext().init();
        this.getLocalMapContext().entry(absolutePathToSeekEntries);
        this.scanJavaCodeMap();
        this.getLocalMapContext().callFunctionNoExc(JavaToJSFunctionsList.ENTRY_POINT_FUNCTION);
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