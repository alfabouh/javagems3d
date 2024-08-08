package ru.jgems3d.engine.system.resources.assets.shaders;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.misc.JGPath;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ShaderContainer {
    private final Set<Uniform> gUniformsFullSet;
    private final Set<Uniform> cUniformsFullSet;
    private final Shader vertexShader;
    private final Shader fragmentShader;
    private final Shader geometricShader;
    private final Shader computeShader;
    private final String id;

    public ShaderContainer(JGPath shaderPath) {
        this.id = shaderPath.getSPath();
        this.gUniformsFullSet = new HashSet<>();
        this.cUniformsFullSet = new HashSet<>();
        Shader geometricShader1 = null;
        Shader vertexShader1 = null;
        Shader fragmentShader1 = null;
        Shader computeShader1 = null;

        if (Shader.checkIfShaderExistsInJar(shaderPath, Shader.ShaderType.FRAGMENT)) {
            fragmentShader1 = new Shader(Shader.ShaderType.FRAGMENT, shaderPath);
        }
        if (Shader.checkIfShaderExistsInJar(shaderPath, Shader.ShaderType.VERTEX)) {
            vertexShader1 = new Shader(Shader.ShaderType.VERTEX, shaderPath);
        }
        if (Shader.checkIfShaderExistsInJar(shaderPath, Shader.ShaderType.GEOMETRIC)) {
            geometricShader1 = new Shader(Shader.ShaderType.GEOMETRIC, shaderPath);
        }
        if (Shader.checkIfShaderExistsInJar(shaderPath, Shader.ShaderType.COMPUTE)) {
            computeShader1 = new Shader(Shader.ShaderType.COMPUTE, shaderPath);
        }

        this.vertexShader = vertexShader1;
        this.fragmentShader = fragmentShader1;
        this.geometricShader = geometricShader1;
        this.computeShader = computeShader1;
    }

    public void initAll() {
        if (this.getFragmentShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getFragmentShader().getShaderPath() + this.getFragmentShader().getShaderType().getFile());
            this.getFragmentShader().init();
            this.putUniformsInGHeap(this.getFragmentShader().getUniforms());
        }
        if (this.getVertexShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getVertexShader().getShaderPath() + this.getVertexShader().getShaderType().getFile());
            this.getVertexShader().init();
            this.putUniformsInGHeap(this.getVertexShader().getUniforms());
        }
        if (this.getGeometricShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getGeometricShader().getShaderPath() + this.getGeometricShader().getShaderType().getFile());
            this.getGeometricShader().init();
            this.putUniformsInGHeap(this.getGeometricShader().getUniforms());
        }
        if (this.getComputeShader() != null) {
            JGemsHelper.getLogger().log("Initializing " + this.getComputeShader().getShaderPath() + this.getComputeShader().getShaderType().getFile());
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