package api.scripting;

import api.scripting.coding.APICodingContext;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
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
        this.getLocalMapContext().getApiCodeEnvironmentController().scan("api.scripting.coding.env.internal.game", "api.scripting.coding.env.internal.util");
    }

    public void initGame(@NotNull JGemsPath relativePath) {
        this.getGlobalGameContext().init();
        this.scanJavaCodeGame();
        this.getGlobalGameContext().entryPoint(relativePath);
    }

    public void initMap(@NotNull JGemsPath relativePath) {
        this.getLocalMapContext().init();
        this.scanJavaCodeMap();
        this.getLocalMapContext().entryPoint(relativePath);
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