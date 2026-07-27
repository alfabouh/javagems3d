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

package javagems3d.system.service.files;

import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public record JGemsPath(String fullPath) implements Serializable {
    @Serial
    private static final long serialVersionUID = 142L;

    public JGemsPath(@NotNull JGemsPath path, @NotNull JGemsPath... other) {
        this(path.fullPath(), Arrays.stream(other).map(JGemsPath::fullPath).toArray(String[]::new));
    }

    public JGemsPath(@NotNull JGemsPath path, @NotNull String... other) {
        this(path.fullPath(), other);
    }

    public JGemsPath(@NotNull String root, @NotNull String... other) {
        this(JGemsPath.concatenate(root, other));
    }

    public JGemsPath(Path path) {
        this(path.toString());
    }

    public JGemsPath(String fullPath) {
        this.fullPath = URLDecoder.decode(JGemsPath.concatenate(fullPath), StandardCharsets.UTF_8);
    }

    private static String concatenate(String root, String... other) {
        StringBuilder stringBuilder = new StringBuilder(JGemsPath.fixPath(root));
        if (other != null) {
            for (String s : other) {
                String string = JGemsPath.fixPath(s);
                stringBuilder.append(string);
            }
        }
        String path = stringBuilder.toString();
        return path.replace("\\", "/").replace("//", "/");
    }

    private static String fixPath(String path) {
        String trimmedPath = path.trim();
        String normalizedPath = trimmedPath.replace("\\", "/");
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = (!normalizedPath.startsWith(".") ? "/" : "") + normalizedPath;
        }
        return normalizedPath;
    }

    public boolean recursiveDelete() {
        return this.recursiveDelete(this.toFile());
    }

    private boolean recursiveDelete(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.exists() && file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                this.recursiveDelete(f);
            }
        }
        if (file.delete()) {
            Log.get().debug("Deleted " + file.getPath());
            return true;
        }
        return false;
    }

    public File toFile() {
        return new File(this.fullPath());
    }

    public Path toPath() {
        return this.toFile().toPath();
    }

    public JGemsPath getAbsolutePathDirectory() {
        return new JGemsPath(this.fullPath().substring(0, this.fullPath().lastIndexOf('/')));
    }

    @Override
    public @NotNull String toString() {
        return this.fullPath();
    }
}