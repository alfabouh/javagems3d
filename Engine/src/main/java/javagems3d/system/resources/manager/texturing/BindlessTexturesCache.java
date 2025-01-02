package javagems3d.system.resources.manager.texturing;

import javagems3d.system.resources.assets.texturing.ext.IBindlessTexture;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BindlessTexturesCache {
    private final Set<Long> allHandlers;

    public BindlessTexturesCache() {
        this.allHandlers = new HashSet<>();
    }

    public void clear() {
        this.getAllHandlers().clear();
    }

    public void init(Set<BindlessTexturesArray> bindlessTexturesArrays) {
        this.clear();
        for (BindlessTexturesArray bindlessTexturesArray : bindlessTexturesArrays) {
            this.spreadArray(bindlessTexturesArray);
        }
    }

    private void spreadArray(BindlessTexturesArray array) {
        this.getAllHandlers().addAll(array.getBindlessTexturesList().stream().map(IBindlessTexture::getBindingHandler).collect(Collectors.toList()));
    }

    public Set<Long> getAllHandlers() {
        return this.allHandlers;
    }
}