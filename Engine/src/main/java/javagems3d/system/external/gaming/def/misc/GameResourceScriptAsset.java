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
