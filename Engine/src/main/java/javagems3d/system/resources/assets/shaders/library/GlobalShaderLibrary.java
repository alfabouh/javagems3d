package javagems3d.system.resources.assets.shaders.library;

import java.util.HashMap;
import java.util.Map;

public final class GlobalShaderLibrary {
    private final Map<String, ShaderLibrariesContainer> containerMap;

    public GlobalShaderLibrary() {
        this.containerMap = new HashMap<>();
    }

    public boolean hasLibrary(String id) {
        return this.getContainerMap().containsKey(id);
    }

    public void addNewShaderLibrary(ShaderLibrariesContainer shaderLibrariesContainer) {
        this.getContainerMap().put(shaderLibrariesContainer.getId(), shaderLibrariesContainer);
    }

    public Map<String, ShaderLibrariesContainer> getContainerMap() {
        return this.containerMap;
    }
}
