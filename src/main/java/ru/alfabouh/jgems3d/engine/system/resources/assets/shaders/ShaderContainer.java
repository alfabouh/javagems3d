package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders;

import ru.alfabouh.jgems3d.logger.SystemLogging;

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

    public ShaderContainer(String shader, int types) {
        this.id = shader;
        this.gUniformsFullSet = new HashSet<>();
        this.cUniformsFullSet = new HashSet<>();
        Shader geometricShader1 = null;
        Shader vertexShader1 = null;
        Shader fragmentShader1 = null;
        Shader computeShader1 = null;

        if ((types & Shader.ShaderType.FRAGMENT_BIT) != 0) {
            fragmentShader1 = new Shader(Shader.ShaderType.FRAGMENT, shader);
        }
        if ((types & Shader.ShaderType.VERTEX_BIT) != 0) {
            vertexShader1 = new Shader(Shader.ShaderType.VERTEX, shader);
        }
        if ((types & Shader.ShaderType.GEOMETRIC_BIT) != 0) {
            geometricShader1 = new Shader(Shader.ShaderType.GEOMETRIC, shader);
        }
        if ((types & Shader.ShaderType.COMPUTE_BIT) != 0) {
            computeShader1 = new Shader(Shader.ShaderType.COMPUTE, shader);
        }

        this.vertexShader = vertexShader1;
        this.fragmentShader = fragmentShader1;
        this.geometricShader = geometricShader1;
        this.computeShader = computeShader1;
    }

    public void initAll() {
        if (this.getFragmentShader() != null) {
            SystemLogging.get().getLogManager().log("Initializing " + this.getFragmentShader().getShaderName() + this.getFragmentShader().getShaderType().getFile());
            this.getFragmentShader().init();
            this.putUniformsInGHeap(this.getFragmentShader().getUniforms());
        }
        if (this.getVertexShader() != null) {
            SystemLogging.get().getLogManager().log("Initializing " + this.getVertexShader().getShaderName() + this.getVertexShader().getShaderType().getFile());
            this.getVertexShader().init();
            this.putUniformsInGHeap(this.getVertexShader().getUniforms());
        }
        if (this.getGeometricShader() != null) {
            SystemLogging.get().getLogManager().log("Initializing " + this.getGeometricShader().getShaderName() + this.getGeometricShader().getShaderType().getFile());
            this.getGeometricShader().init();
            this.putUniformsInGHeap(this.getGeometricShader().getUniforms());
        }
        if (this.getComputeShader() != null) {
            SystemLogging.get().getLogManager().log("Initializing " + this.getComputeShader().getShaderName() + this.getComputeShader().getShaderType().getFile());
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