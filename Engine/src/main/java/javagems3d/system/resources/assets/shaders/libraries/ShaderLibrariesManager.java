package javagems3d.system.resources.assets.shaders.libraries;

import javagems3d.JGems3D;
import javagems3d.system.service.path.JGemsPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShaderLibrariesManager {
    private final Map<String, ShaderLibrariesContainer> containerMap;
    private final JGems3D.GetSource getSource;

    public ShaderLibrariesManager(JGems3D.GetSource getSource) {
        this.getSource = getSource;
        this.containerMap = new HashMap<>();
    }

    public boolean hasLibrary(String id) {
        return this.getContainerMap().containsKey(id);
    }

    public ShaderLibrariesContainer getShaderLibrariesContainer(String id) {
        return this.getContainerMap().get(id);
    }

    public void initLibrary(JGemsPath path) {
        this.initLibrary(new ShaderLibrariesContainer(this.getSource, path));
    }

    public void initLibrary(ShaderLibrariesContainer shaderLibrariesContainer) {
        this.getContainerMap().put(shaderLibrariesContainer.getPath(), shaderLibrariesContainer);
    }

    public void reload() {
        Set<String> paths = new HashSet<>(this.getContainerMap().keySet());
        this.getContainerMap().clear();
        for (String s : paths) {
            this.getContainerMap().put(s, new ShaderLibrariesContainer(this.getSource, new JGemsPath(s)));
        }
    }

    public void clear() {
        this.getContainerMap().clear();
    }

    public Map<String, ShaderLibrariesContainer> getContainerMap() {
        return this.containerMap;
    }
}
