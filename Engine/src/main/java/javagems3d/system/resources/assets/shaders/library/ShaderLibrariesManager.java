package javagems3d.system.resources.assets.shaders.library;

import javagems3d.system.service.path.JGemsPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShaderLibrariesManager {
    private final Map<String, ShaderLibrariesContainer> containerMap;

    public ShaderLibrariesManager() {
        this.containerMap = new HashMap<>();
    }

    public boolean hasLibrary(String id) {
        return this.getContainerMap().containsKey(id);
    }

    public ShaderLibrariesContainer getShaderLibrariesContainer(String id) {
        return this.getContainerMap().get(id);
    }

    public void addNewShaderLibrary(ShaderLibrariesContainer shaderLibrariesContainer) {
        this.getContainerMap().put(shaderLibrariesContainer.getPath(), shaderLibrariesContainer);
    }

    public void reload() {
        Set<String> paths = new HashSet<>(this.getContainerMap().keySet());
        this.getContainerMap().clear();
        for (String s : paths) {
            this.getContainerMap().put(s, new ShaderLibrariesContainer(new JGemsPath(s)));
        }
    }

    public void clean() {
        this.getContainerMap().clear();
    }

    public Map<String, ShaderLibrariesContainer> getContainerMap() {
        return this.containerMap;
    }
}
