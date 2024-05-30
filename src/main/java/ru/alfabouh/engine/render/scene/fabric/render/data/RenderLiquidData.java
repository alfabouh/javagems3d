package ru.alfabouh.engine.render.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.exception.GameException;
import ru.alfabouh.engine.system.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.render.scene.programs.CubeMapProgram;

public final class RenderLiquidData {
    private final TextureSample liquidTexture;
    private final TextureSample liquidNormals;
    private final ShaderManager shaderManager;
    private final boolean reflections;

    public RenderLiquidData(TextureSample liquidNormals, @NotNull TextureSample liquidTexture, boolean reflections, ShaderManager shaderManager) {
        this.liquidTexture = liquidTexture;
        if ((liquidNormals != null && !liquidNormals.isValid()) || !liquidTexture.isValid()) {
            throw new GameException("Wrong liquid textures!");
        }
        this.liquidNormals = liquidNormals;
        this.shaderManager = shaderManager;
        this.reflections = reflections;
    }

    public boolean reflections() {
        return this.reflections;
    }

    public CubeMapProgram getAmbient() {
        return JGems.get().getScreen().getRenderWorld().getEnvironment().getSky().getSkyBox().cubeMapTexture();
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