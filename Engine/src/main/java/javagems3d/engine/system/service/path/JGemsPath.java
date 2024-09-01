/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.service.path;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public final class JGemsPath {
    private final String fullPath;

    public JGemsPath(JGemsPath path, String... other) {
        this(path.getFullPath(), other);
    }

    public JGemsPath(String root, String... other) {
        this.fullPath = this.concatenate(root, other);
    }

    public JGemsPath(String path) {
        this.fullPath = this.concatenate(path);
    }

    private String concatenate(String root, String... other) {
        StringBuilder stringBuilder = new StringBuilder(this.fixPath(root));
        if (other != null) {
            for (String s : other) {
                String string = this.fixPath(s);
                stringBuilder.append(string);
            }
        }
        return (stringBuilder.toString()).replaceAll("//", "/");
    }

    private String fixPath(String path) {
        String trimmedPath = path.trim();
        String normalizedPath = trimmedPath.replace("\\", "/");
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        return normalizedPath;
    }

    public String getParentPath() {
        return this.getPath().getParent().toString() + "/";
    }

    public Path getPath() {
        return FileSystems.getDefault().getPath(this.getFullPath());
    }

    public String getFullPath() {
        return this.fullPath;
    }

    @Override
    public String toString() {
        return this.getFullPath();
    }
}
