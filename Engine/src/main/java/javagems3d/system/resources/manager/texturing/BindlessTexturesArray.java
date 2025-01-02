package javagems3d.system.resources.manager.texturing;

import javagems3d.system.resources.assets.texturing.ext.IBindlessTexture;

import java.util.ArrayList;
import java.util.List;

public class BindlessTexturesArray {
    private final List<IBindlessTexture> bindlessTexturesList;

    public BindlessTexturesArray() {
        this.bindlessTexturesList = new ArrayList<>();
    }

    public void clear() {
        this.getBindlessTexturesList().clear();
    }

    public int totalTextures() {
        return this.getBindlessTexturesList().size();
    }

    public void add(IBindlessTexture bindlessTexture) {
        this.getBindlessTexturesList().add(bindlessTexture);
    }

    public List<IBindlessTexture> getBindlessTexturesList() {
        return this.bindlessTexturesList;
    }
}
