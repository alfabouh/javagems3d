package javagems3d.system.service.files.source;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class JGemsStringSource implements ISource {
    private final String string;
    private final ISource.Source source;

    public JGemsStringSource(JGemsPathSource pathSource) {
        this.string = pathSource.toString();
        this.source = pathSource.getSource();
    }

    public JGemsStringSource(@NotNull String string, @NotNull ISource.Source source) {
        this.string = string;
        this.source = source;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JGemsStringSource)) {
            return false;
        }
        JGemsStringSource that = (JGemsStringSource) object;
        return that.getSource().equals(this.getSource()) && that.getString().equals(this.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.string, this.source);
    }

    @Override
    public String toString() {
        return this.getString();
    }

    public String getString() {
        return this.string;
    }

    public ISource.Source getSource() {
        return this.source;
    }
}