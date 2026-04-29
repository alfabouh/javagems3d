package javagems3d.system.resources.managing.resources.data.arrays;

import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;

import java.util.ArrayList;
import java.util.List;

public final class BindlessTexturesDataArray implements IDataArray {
    private final List<ITextureBindless> bindlessTextureList;

    private BindlessTexturesDataArray(List<ITextureBindless> bindlessTextureList) {
        this.bindlessTextureList = bindlessTextureList;
    }

    public BindlessTexturesDataArray() {
        this.bindlessTextureList = new ArrayList<>();
    }

    public void clear() {
        this.getBindlessTextureList().clear();
    }

    public void add(ITextureBindless bindlessTexture) {
        this.getBindlessTextureList().add(bindlessTexture);
    }

    public List<ITextureBindless> getBindlessTextureList() {
        return this.bindlessTextureList;
    }

    public BindlessTexturesDataArray copy() {
        return new BindlessTexturesDataArray(new ArrayList<>(this.bindlessTextureList));
    }
}