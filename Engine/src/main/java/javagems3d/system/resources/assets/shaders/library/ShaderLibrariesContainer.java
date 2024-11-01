package javagems3d.system.resources.assets.shaders.library;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.shaders.ShaderType;
import javagems3d.system.service.path.JGemsPath;

public final class ShaderLibrariesContainer {
    private final String path;
    private final ShaderLibrary vertexShaderLibrary;
    private final ShaderLibrary fragmentShaderLibrary;
    private final ShaderLibrary geometryShaderLibrary;

    public ShaderLibrariesContainer(JGemsPath shaderPath) {
        this.path = shaderPath.getFullPath();

        ShaderLibrary geometricShader1 = null;
        ShaderLibrary vertexShader1 = null;
        ShaderLibrary fragmentShader1 = null;

        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.FRAGMENT)) {
            fragmentShader1 = new ShaderLibrary(ShaderType.FRAGMENT, shaderPath);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.VERTEX)) {
            vertexShader1 = new ShaderLibrary(ShaderType.VERTEX, shaderPath);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.GEOMETRIC)) {
            geometricShader1 = new ShaderLibrary(ShaderType.GEOMETRIC, shaderPath);
        }

        this.vertexShaderLibrary = vertexShader1;
        this.fragmentShaderLibrary = fragmentShader1;
        this.geometryShaderLibrary = geometricShader1;

        JGemsHelper.getLogger().log("Read shader library: " + this);
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
