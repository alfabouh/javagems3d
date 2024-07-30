package ru.jgems3d.engine.system.misc;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public final class JGPath {
    private final String finalPath;

    public JGPath(JGPath path, String... other) {
        this(path.getSPath(), other);
    }

    public JGPath(String root, String... other) {
        this.finalPath = this.concatenate(root, other);
    }

    public JGPath(String path) {
        this.finalPath = this.concatenate(path);
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
        return FileSystems.getDefault().getPath(this.getSPath());
    }

    public String getSPath() {
        return this.finalPath;
    }

    @Override
    public String toString() {
        return this.getSPath();
    }
}
