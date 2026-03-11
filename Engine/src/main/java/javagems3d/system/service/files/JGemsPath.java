package javagems3d.system.service.files;

import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public record JGemsPath(String fullPath) implements Serializable {
    @Serial
    private static final long serialVersionUID = 142L;

    public JGemsPath(JGemsPath path, JGemsPath... other) {
        this(path.fullPath(), Arrays.stream(other).map(JGemsPath::fullPath).toArray(String[]::new));
    }

    public JGemsPath(JGemsPath path, String... other) {
        this(path.fullPath(), other);
    }

    public JGemsPath(String root, String... other) {
        this(JGemsPath.concatenate(root, other));
    }

    public JGemsPath(Path path) {
        this(path.toString());
    }

    public JGemsPath(String fullPath) {
        this.fullPath = JGemsPath.concatenate(fullPath);
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
            normalizedPath = "/" + normalizedPath;
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