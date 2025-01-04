package javagems3d.system.resources.managing.arrays;

import javagems3d.system.resources.assets.texturing.ext.IBindlessTexture;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BindlessTexturesArray {
    private final Map<IBindlessTexture, Integer> bindlessTexturesIdMap;

    public BindlessTexturesArray() {
        this.bindlessTexturesIdMap = new LinkedHashMap<>();
    }

    public void clear() {
        this.getBindlessTexturesIdMap().clear();
    }

    public int totalTextures() {
        return this.getBindlessTexturesIdMap().size();
    }

    public int getTextureId(IBindlessTexture bindlessTexture) {
        if (!this.getBindlessTexturesIdMap().containsKey(bindlessTexture)) {
            throw new JGemsRuntimeException("BindlessTextureArray doesn't contain bindless texture: " + bindlessTexture.getBindingHandler());
        }
        return this.getBindlessTexturesIdMap().get(bindlessTexture);
    }

    public void add(IBindlessTexture bindlessTexture) {
        this.getBindlessTexturesIdMap().put(bindlessTexture, this.totalTextures());
    }

    public final Map<IBindlessTexture, Integer> getBindlessTexturesIdMap() {
        return this.bindlessTexturesIdMap;
    }
}