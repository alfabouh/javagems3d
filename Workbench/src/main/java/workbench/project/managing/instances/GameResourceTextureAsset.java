package workbench.project.managing.instances;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;

public class GameResourceTextureAsset implements IAsset {
    private final String name;
    private final String relativePath;
    private final ITexture2DProgram texture2DProgram;

    public GameResourceTextureAsset(String name, String relativePath, ITexture2DProgram texture2DProgram) {
        this.name = name;
        this.relativePath = relativePath;
        this.texture2DProgram = texture2DProgram;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public String getName() {
        return this.name;
    }

    public ITexture2DProgram getTexture2DProgram() {
        return this.texture2DProgram;
    }
}
