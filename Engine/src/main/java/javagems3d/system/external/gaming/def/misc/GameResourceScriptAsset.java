package javagems3d.system.external.gaming.def.misc;

import javagems3d.system.external.gaming.def.IAsset;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record GameResourceScriptAsset(String name, String relativePath, String scriptText) implements IAsset {
    public void save(@NotNull JGemsPath scriptsFolder, @Nullable String scriptText) {
        {
            final JGemsPath absPath = new JGemsPath(scriptsFolder, this.relativePath());
            try (FileOutputStream fos = new FileOutputStream(absPath.toFile())) {
                fos.write((scriptText == null ? this.scriptText : scriptText).trim().getBytes(StandardCharsets.UTF_8));
                Log.get().debug(("Saved JS file: " + this.relativePath));
            } catch (IOException ex) {
                Log.get().exception(new RuntimeException("Failed to create JS file: " + this.relativePath, ex));
            }
        }
    }

    @Override
    public String name() {
        return this.name;
    }
}
