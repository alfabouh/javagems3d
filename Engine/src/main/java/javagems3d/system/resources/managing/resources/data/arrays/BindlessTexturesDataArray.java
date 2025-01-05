package javagems3d.system.resources.managing.resources.data.arrays;

import javagems3d.system.resources.assets.texturing.ext.IBindlessTexture;

import java.util.ArrayList;
import java.util.List;

public final class BindlessTexturesDataArray implements IDataArray {
    private final List<IBindlessTexture> bindlessTextureList;

    public BindlessTexturesDataArray() {
        this.bindlessTextureList = new ArrayList<>();
    }

    public void clear() {
        this.getBindlessTextureList().clear();
    }

    public void add(IBindlessTexture bindlessTexture) {
        this.getBindlessTextureList().add(bindlessTexture);
    }

    public List<IBindlessTexture> getBindlessTextureList() {
        return this.bindlessTextureList;
    }
}