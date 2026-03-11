package javagems3d.system.service.files.source;

import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class JGemsPathSource implements ISource {
    private final JGemsPath jGemsPath;
    private final ISource.Source source;

    public JGemsPathSource(JGemsStringSource stringSource) {
        this.jGemsPath = new JGemsPath(stringSource.toString());
        this.source = stringSource.getSource();
    }

    public JGemsPathSource(@NotNull String fullPath, @NotNull ISource.Source source) {
        this.source = source;
        this.jGemsPath = new JGemsPath(fullPath);
    }
    public JGemsPathSource(@NotNull JGemsPath jGemsPath, @NotNull ISource.Source source) {
        this.source = source;
        this.jGemsPath = jGemsPath;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JGemsPathSource that)) {
            return false;
        }
        return that.getSource().equals(this.getSource()) && that.getPath().equals(this.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPath(), this.source);
    }

    @Override
    public String toString() {
        return this.getPath().toString();
    }

    public JGemsPath getPath() {
        return this.jGemsPath;
    }

    public ISource.Source getSource() {
        return this.source;
    }
}
