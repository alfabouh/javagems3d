package workbench.project.managing.instances.mapping;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import org.jetbrains.annotations.NotNull;
import workbench.project.managing.instances.IAsset;

public class GameResourceSkyboxAsset implements IAsset {
    private final String name;
    private ICubeMapProgram.CMTextures cmTextures;

    public GameResourceSkyboxAsset(String name) {
        this(name, new ICubeMapProgram.CMTextures(null, null, null, null, null, null));
    }

    public GameResourceSkyboxAsset(String name, @NotNull ICubeMapProgram.CMTextures cmTextures) {
        this.name = name;
        this.cmTextures = cmTextures;
    }

    public ICubeMapProgram.CMTextures getCmTextures() {
        return this.cmTextures;
    }

    public GameResourceSkyboxAsset setCmTextures(ICubeMapProgram.CMTextures cmTextures) {
        this.cmTextures = cmTextures;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
