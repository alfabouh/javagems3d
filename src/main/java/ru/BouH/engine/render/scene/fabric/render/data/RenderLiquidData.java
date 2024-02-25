package ru.BouH.engine.render.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;

public final class RenderLiquidData {
    private final TextureSample liquidTexture;
    private final TextureSample liquidNormals;
    private final ShaderManager shaderManager;
    private final CubeMapProgram ambient;

    public RenderLiquidData(@NotNull TextureSample liquidNormals, @NotNull TextureSample liquidTexture, @NotNull CubeMapProgram ambient, ShaderManager shaderManager) {
        this.liquidTexture = liquidTexture;
        if (!liquidNormals.isValid() || !liquidTexture.isValid()) {
            throw new GameException("Wrong liquid textures!");
        }
        this.liquidNormals = liquidNormals;
        this.shaderManager = shaderManager;
        this.ambient = ambient;
    }

    public CubeMapProgram getAmbient() {
        return this.ambient;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public TextureSample getLiquidNormals() {
        return this.liquidNormals;
    }

    public TextureSample getLiquidTexture() {
        return this.liquidTexture;
    }
}