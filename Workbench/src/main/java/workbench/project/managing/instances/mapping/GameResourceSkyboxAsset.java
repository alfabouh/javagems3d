package workbench.project.managing.instances.mapping;

import workbench.project.managing.instances.IAsset;

public class GameResourceSkyboxAsset implements IAsset {
    private final String name;
    private String textureUPRelativePath;
    private String textureBOTTOMRelativePath;
    private String textureFRONTRelativePath;
    private String textureBACKRelativePath;
    private String textureLEFTRelativePath;
    private String textureRIGHTRelativePath;

    public GameResourceSkyboxAsset(String name) {
        this(name, null, null, null, null, null, null);
    }

    public GameResourceSkyboxAsset(String name, String textureUPRelativePath, String textureBOTTOMRelativePath, String textureFRONTRelativePath, String textureBACKRelativePath, String textureLEFTRelativePath, String textureRIGHTRelativePath) {
        this.name = name;
        this.textureUPRelativePath = textureUPRelativePath;
        this.textureBOTTOMRelativePath = textureBOTTOMRelativePath;
        this.textureFRONTRelativePath = textureFRONTRelativePath;
        this.textureBACKRelativePath = textureBACKRelativePath;
        this.textureLEFTRelativePath = textureLEFTRelativePath;
        this.textureRIGHTRelativePath = textureRIGHTRelativePath;
    }

    public String getTextureUPRelativePath() {
        return this.textureUPRelativePath;
    }

    public GameResourceSkyboxAsset setTextureUPRelativePath(String textureUPRelativePath) {
        this.textureUPRelativePath = textureUPRelativePath;
        return this;
    }

    public String getTextureBOTTOMRelativePath() {
        return this.textureBOTTOMRelativePath;
    }

    public GameResourceSkyboxAsset setTextureBOTTOMRelativePath(String textureBOTTOMRelativePath) {
        this.textureBOTTOMRelativePath = textureBOTTOMRelativePath;
        return this;
    }

    public String getTextureFRONTRelativePath() {
        return this.textureFRONTRelativePath;
    }

    public GameResourceSkyboxAsset setTextureFRONTRelativePath(String textureFRONTRelativePath) {
        this.textureFRONTRelativePath = textureFRONTRelativePath;
        return this;
    }

    public String getTextureBACKRelativePath() {
        return this.textureBACKRelativePath;
    }

    public GameResourceSkyboxAsset setTextureBACKRelativePath(String textureBACKRelativePath) {
        this.textureBACKRelativePath = textureBACKRelativePath;
        return this;
    }

    public String getTextureLEFTRelativePath() {
        return this.textureLEFTRelativePath;
    }

    public GameResourceSkyboxAsset setTextureLEFTRelativePath(String textureLEFTRelativePath) {
        this.textureLEFTRelativePath = textureLEFTRelativePath;
        return this;
    }

    public String getTextureRIGHTRelativePath() {
        return this.textureRIGHTRelativePath;
    }

    public GameResourceSkyboxAsset setTextureRIGHTRelativePath(String textureRIGHTRelativePath) {
        this.textureRIGHTRelativePath = textureRIGHTRelativePath;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
