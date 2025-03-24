package javagems3d.system.service.path;

import java.io.Serializable;
import java.nio.file.Path;

public final class JGemsPath implements Serializable {
    private static final long serialVersionUID = 142L;
    private final String fullPath;

    public JGemsPath(JGemsPath path, String... other) {
        this(path.getFullPath(), other);
    }

    public JGemsPath(String root, String... other) {
        this.fullPath = this.concatenate(root, other);
    }

    public JGemsPath(Path path) {
        this(path.toString());
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
        String path = stringBuilder.toString();
        return path.replace("\\", "/").replace("//", "/");
    }

    private String fixPath(String path) {
        String trimmedPath = path.trim();
        String normalizedPath = trimmedPath.replace("\\", "/");
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        return normalizedPath;
    }

    public JGemsPath getDirectory() {
        return new JGemsPath(this.getFullPath().substring(0, this.getFullPath().lastIndexOf('/')));
    }

    public String getFullPath() {
        return this.fullPath;
    }

    @Override
    public String toString() {
        return this.getFullPath();
    }
}
