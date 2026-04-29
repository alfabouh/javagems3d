package javagems3d.system.resources.assets.shaders.libraries;

import javagems3d.system.resources.assets.shaders.base.ShaderType;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public final class ShaderLibrariesContainer {
    private final JGemsStringSource path;
    private final ShaderLibrary vertexShaderLibrary;
    private final ShaderLibrary fragmentShaderLibrary;
    private final ShaderLibrary geometryShaderLibrary;

    public ShaderLibrariesContainer(@NotNull JGemsPathSource shaderPath) {
        this.path = new JGemsStringSource(shaderPath);

        ShaderLibrary geometricShader1 = null;
        ShaderLibrary vertexShader1 = null;
        ShaderLibrary fragmentShader1 = null;

        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.FRAGMENT)) {
            fragmentShader1 = new ShaderLibrary(shaderPath, ShaderType.FRAGMENT);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.VERTEX)) {
            vertexShader1 = new ShaderLibrary(shaderPath, ShaderType.VERTEX);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.GEOMETRIC)) {
            geometricShader1 = new ShaderLibrary(shaderPath, ShaderType.GEOMETRIC);
        }

        this.vertexShaderLibrary = vertexShader1;
        this.fragmentShaderLibrary = fragmentShader1;
        this.geometryShaderLibrary = geometricShader1;

        Log.get().trace("Initialized shader library: " + this);
    }

    public ShaderLibrary getVertexShaderLibrary() {
        return this.vertexShaderLibrary;
    }

    public ShaderLibrary getFragmentShaderLibrary() {
        return this.fragmentShaderLibrary;
    }

    public ShaderLibrary getGeometryShaderLibrary() {
        return this.geometryShaderLibrary;
    }

    public ShaderLibrary getShaderLibraryByType(ShaderType shaderType) {
        switch (shaderType) {
            case VERTEX: {
                return this.getVertexShaderLibrary();
            }
            case FRAGMENT: {
                return this.getFragmentShaderLibrary();
            }
            case GEOMETRIC: {
                return this.getGeometryShaderLibrary();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getStringSource().toString();
    }

    public JGemsStringSource getStringSource() {
        return this.path;
    }
}
