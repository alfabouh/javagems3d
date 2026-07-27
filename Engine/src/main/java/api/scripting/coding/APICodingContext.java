/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.scripting.coding;

import api.scripting.JGemsAPIScriptingCore;
import api.scripting.coding.env.APICodeEnvironmentController;
import javagems3d.system.service.exceptions.JGemsAPIException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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

    public void entry(JGemsPath absolutePathToSeekEntries) {
        this.close();
        this.init();
        final List<File> files = new ArrayList<>();
        this.findEntryScripts(files, absolutePathToSeekEntries.toFile());
        files.forEach(this::loadCodeInside_EXTERNALFS);
    }

    private void findEntryScripts(List<File> filesArray, File rootDir) {
        if (!rootDir.exists()) {
            return;
        }
        File[] files = rootDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                this.findEntryScripts(filesArray, file);
            } else if (file.getName().endsWith(".js")) {
                Log.get().debug("Found: " + file.getPath());
                if (this.containsEntryPoint(file)) {
                    filesArray.add(file);
                }
            }
        }
    }

    private boolean containsEntryPoint(File file) {
        try {
            String content = Files.readString(file.toPath());
            return content.contains("function JsInit");
        } catch (IOException e) {
            throw new JGemsAPIException(e);
        }
    }

    @Override
    public void close() {
        this.getApiCodeEnvironmentController().close();
        if (this.context != null) {
            this.context.close();
        }
    }

    public Value callFunctionNoExc(@NotNull String funName, Object... args) {
        try {
            return this.callFunction(funName, args);
        } catch (Exception e) {
            Log.get().error("Failed to call function " + funName + " : " + e.getMessage());
        }
        return null;
    }

    public Value callFunction(@NotNull String funName, Object... args) throws JGemsAPIException {
        try {
            Value fun = this.getBindings().getMember(funName);
            if (fun == null) {
                throw new JGemsRuntimeException("Function not found: " + funName);
            }
            return fun.execute(args);
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
            //.mimeType("application/javascript+module")
            Source src = Source.newBuilder(JGemsAPIScriptingCore.LAN, file).build();
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
