package api.scripting.coding;

import api.scripting.JGemsAPIScriptingCore;
import api.scripting.coding.env.APICodeEnvironmentController;
import javagems3d.system.service.exceptions.JGemsAPIException;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.File;
import java.util.Objects;

public class APICodingContext implements Closeable {
    private Context context;
    private final APICodeEnvironmentController apiCodeEnvironmentController;

    public APICodingContext() {
        this.apiCodeEnvironmentController = new APICodeEnvironmentController(this);
    }

    public void init() {
        this.context = Context.newBuilder(JGemsAPIScriptingCore.LAN).allowHostAccess(HostAccess.ALL).allowHostClassLookup(s -> true).allowIO(IOAccess.ALL).option("js.esm-eval-returns-exports", "true").option("js.ecmascript-version", "2022").build();
    }

    public void entryPoint(JGemsPath entryPoint) {
        this.close();
        this.init();
        this.loadCodeInside_EXTERNALFS(entryPoint);
    }

    @Override
    public void close() {
        this.getApiCodeEnvironmentController().close();
        if (this.context != null) {
            this.context.close();
        }
    }

    public void callFunction(@NotNull String funName, Object... args) throws JGemsAPIException {
        try {
            Value fun = this.getBindings().getMember("funName");
            fun.execute(args);
        } catch (Exception e) {
            throw new JGemsAPIException(e);
        }
    }

    private void loadCodeInside_EXTERNALFS(@NotNull JGemsPath relativePath) {
        try {
            this.loadCodeInside_EXTERNALFS(relativePath.toFile());
        } catch (Exception e) {
            Log.get().exception(new JGemsAPIException("Failed to load path " + relativePath, e));
        }
    }

    private void loadCodeInside_EXTERNALFS(File file) {
        try {
            Source src = Source.newBuilder(JGemsAPIScriptingCore.LAN, file).mimeType("application/javascript+module").build();
            Objects.requireNonNull(this.getContext()).eval(src);
        } catch (Exception e) {
            Log.get().exception(new JGemsAPIException("Failed to load code", e));
        }
    }

    public APICodeEnvironmentController getApiCodeEnvironmentController() {
        return this.apiCodeEnvironmentController;
    }

    public Value getBindings() {
        return Objects.requireNonNull(this.getContext()).getBindings(JGemsAPIScriptingCore.LAN);
    }

    public @Nullable Context getContext() {
        return this.context;
    }
}
