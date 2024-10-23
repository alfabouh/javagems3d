/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.assets.shaders.base;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.shaders.library.ShaderLibrariesManager;
import javagems3d.system.service.path.JGemsPath;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ShadersContainer {
    private final Set<Uniform> gUniformsFullSet;
    private final Set<Uniform> cUniformsFullSet;
    private final Shader vertexShader;
    private final Shader fragmentShader;
    private final Shader geometricShader;
    private final Shader computeShader;
    private final String id;

    public ShadersContainer(JGemsPath shaderPath) {
        this(null, shaderPath);
    }

    public ShadersContainer(ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath) {
        this.id = shaderPath.getFullPath();
        this.gUniformsFullSet = new HashSet<>();
        this.cUniformsFullSet = new HashSet<>();
        Shader geometricShader1 = null;
        Shader vertexShader1 = null;
        Shader fragmentShader1 = null;
        Shader computeShader1 = null;

        if (Shader.checkIfShaderExistsInJar(shaderPath, ShaderType.FRAGMENT)) {
            fragmentShader1 = new Shader(shaderLibrary, ShaderType.FRAGMENT, shaderPath);
        }
        if (Shader.checkIfShaderExistsInJar(shaderPath, ShaderType.VERTEX)) {
            vertexShader1 = new Shader(shaderLibrary, ShaderType.VERTEX, shaderPath);
        }
        if (Shader.checkIfShaderExistsInJar(shaderPath, ShaderType.GEOMETRIC)) {
            geometricShader1 = new Shader(shaderLibrary, ShaderType.GEOMETRIC, shaderPath);
        }
        if (Shader.checkIfShaderExistsInJar(shaderPath, ShaderType.COMPUTE)) {
            computeShader1 = new Shader(shaderLibrary, ShaderType.COMPUTE, shaderPath);
        }

        this.vertexShader = vertexShader1;
        this.fragmentShader = fragmentShader1;
        this.geometricShader = geometricShader1;
        this.computeShader = computeShader1;
    }

    public void initAll() {
        if (this.getFragmentShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getFragmentShader().getShaderPath() + "/" + this.getFragmentShader().getShaderType().getFile());
            this.getFragmentShader().init();
            this.putUniformsInGHeap(this.getFragmentShader().getUniforms());
        }
        if (this.getVertexShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getVertexShader().getShaderPath() + "/" + this.getVertexShader().getShaderType().getFile());
            this.getVertexShader().init();
            this.putUniformsInGHeap(this.getVertexShader().getUniforms());
        }
        if (this.getGeometricShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getGeometricShader().getShaderPath() + "/" + this.getGeometricShader().getShaderType().getFile());
            this.getGeometricShader().init();
            this.putUniformsInGHeap(this.getGeometricShader().getUniforms());
        }
        if (this.getComputeShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getComputeShader().getShaderPath() + "/" + this.getComputeShader().getShaderType().getFile());
            this.getComputeShader().init();
            this.putUniformsInCHeap(this.getComputeShader().getUniforms());
        }
    }

    private void putUniformsInCHeap(List<Uniform> uniformList) {
        this.getCUniformsFullSet().addAll(uniformList);
    }

    private void putUniformsInGHeap(List<Uniform> uniformList) {
        this.getGUniformsFullSet().addAll(uniformList);
    }

    public void clean() {
        this.getGUniformsFullSet().clear();
        this.getCUniformsFullSet().clear();
        if (this.getFragmentShader() != null) {
            this.getFragmentShader().clean();
        }
        if (this.getVertexShader() != null) {
            this.getVertexShader().clean();
        }
        if (this.getGeometricShader() != null) {
            this.getGeometricShader().clean();
        }
        if (this.getComputeShader() != null) {
            this.getComputeShader().clean();
        }
    }

    public Set<Uniform> getCUniformsFullSet() {
        return this.cUniformsFullSet;
    }

    public Set<Uniform> getGUniformsFullSet() {
        return this.gUniformsFullSet;
    }

    public String getId() {
        return this.id;
    }

    public Shader getComputeShader() {
        return this.computeShader;
    }

    public Shader getFragmentShader() {
        return this.fragmentShader;
    }

    public Shader getGeometricShader() {
        return this.geometricShader;
    }

    public Shader getVertexShader() {
        return this.vertexShader;
    }
}