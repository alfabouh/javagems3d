package javagems3d.system.resources.managing.resources.data.cache;

import javagems3d.system.resources.assets.texturing.ext.IBindlessTexture;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class BindlessTexturesDataCache implements IDataCache {
    private final Map<IBindlessTexture, Integer> bindlessTexturesIdMap;

    public BindlessTexturesDataCache() {
        this.bindlessTexturesIdMap = new LinkedHashMap<>();
    }

    public void writeData(Set<BindlessTexturesDataArray> arraySet) {
        for (BindlessTexturesDataArray bindlessTexturesDataArray : arraySet) {
            for (IBindlessTexture bindlessTexture : bindlessTexturesDataArray.getBindlessTextureList()) {
                this.add(bindlessTexture);
            }
        }
    }

    public void clear() {
        this.bindlessTexturesIdMap.clear();
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

    private void add(IBindlessTexture bindlessTexture) {
        this.bindlessTexturesIdMap.put(bindlessTexture, this.totalTextures());
    }

    public Map<IBindlessTexture, Integer> getBindlessTexturesIdMap() {
        return new LinkedHashMap<>(this.bindlessTexturesIdMap);
    }
}
