package javagems3d.system.resources.assets.shaders.libraries;

import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShaderLibrariesManager {
    private final Map<JGemsStringSource, ShaderLibrariesContainer> containerMap;

    public ShaderLibrariesManager() {
        this.containerMap = new HashMap<>();
    }

    public boolean hasLibrary(JGemsStringSource id) {
        return this.getContainerMap().containsKey(id);
    }

    public ShaderLibrariesContainer getShaderLibrariesContainer(JGemsStringSource id) {
        return this.getContainerMap().get(id);
    }

    public void createLibrary(@NotNull JGemsPathSource pathSource) {
        this.createLibrary(new ShaderLibrariesContainer(pathSource));
    }

    public void createLibrary(ShaderLibrariesContainer shaderLibrariesContainer) {
        this.getContainerMap().put(shaderLibrariesContainer.getStringSource(), shaderLibrariesContainer);
    }

    public void reload() {
        Set<JGemsStringSource> paths = new HashSet<>(this.getContainerMap().keySet());
        this.getContainerMap().clear();
        for (JGemsStringSource s : paths) {
            this.getContainerMap().put(s, new ShaderLibrariesContainer(new JGemsPathSource(new JGemsPath(s.getString()), s.getSource())));
        }
    }

    public void clear() {
        this.getContainerMap().clear();
    }

    public Map<JGemsStringSource, ShaderLibrariesContainer> getContainerMap() {
        return this.containerMap;
    }
}
