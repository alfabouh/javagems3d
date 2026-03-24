package javagems3d.system.resources.managing.resources.data.bindless_rendering_cache;

import javagems3d.graphics.rendering.programs.textures.Texture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class BindlessTexturesDataCache implements IBindlessDataCache {
    private final Map<ITextureBindless, Integer> bindlessTexturesIdMap;

    public BindlessTexturesDataCache() {
        this.bindlessTexturesIdMap = new LinkedHashMap<>();
    }

    public void writeData(Set<BindlessTexturesDataArray> arraySet) {
        this.add((Texture2DProgram) ResourceManager.DEFAULT_TEXTURE());
        for (BindlessTexturesDataArray bindlessTexturesDataArray : arraySet) {
            for (ITextureBindless bindlessTexture : bindlessTexturesDataArray.getBindlessTextureList()) {
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

    public int getTextureId(ITextureBindless bindlessTexture) {
        if (!this.getBindlessTexturesIdMap().containsKey(bindlessTexture)) {
            throw new JGemsRuntimeException("BindlessTextureArray doesn't contain bindless texture: " + bindlessTexture.getBindingHandler());
        }
        return this.getBindlessTexturesIdMap().get(bindlessTexture);
    }

    private void add(ITextureBindless bindlessTexture) {
        this.bindlessTexturesIdMap.put(bindlessTexture, this.totalTextures());
    }

    public Map<ITextureBindless, Integer> getBindlessTexturesIdMap() {
        return new LinkedHashMap<>(this.bindlessTexturesIdMap);
    }
}
