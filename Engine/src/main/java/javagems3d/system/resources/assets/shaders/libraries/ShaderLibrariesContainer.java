package javagems3d.system.resources.assets.shaders.libraries;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.shaders.base.ShaderType;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public final class ShaderLibrariesContainer {
    private final String path;
    private final ShaderLibrary vertexShaderLibrary;
    private final ShaderLibrary fragmentShaderLibrary;
    private final ShaderLibrary geometryShaderLibrary;

    public ShaderLibrariesContainer(@NotNull JGems3D.GetSource source, JGemsPath shaderPath) {
        this.path = shaderPath.getFullPath();

        ShaderLibrary geometricShader1 = null;
        ShaderLibrary vertexShader1 = null;
        ShaderLibrary fragmentShader1 = null;

        if (ShaderLibrary.checkIfShaderExistsInJar(source, shaderPath, ShaderType.FRAGMENT)) {
            fragmentShader1 = new ShaderLibrary(source, ShaderType.FRAGMENT, shaderPath);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(source, shaderPath, ShaderType.VERTEX)) {
            vertexShader1 = new ShaderLibrary(source, ShaderType.VERTEX, shaderPath);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(source, shaderPath, ShaderType.GEOMETRIC)) {
            geometricShader1 = new ShaderLibrary(source, ShaderType.GEOMETRIC, shaderPath);
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
        return this.getPath();
    }

    public String getPath() {
        return this.path;
    }
}
