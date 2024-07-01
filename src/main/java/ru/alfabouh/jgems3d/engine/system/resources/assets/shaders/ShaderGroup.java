package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders;

import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.HashSet;
import java.util.Set;

public final class ShaderGroup {
    private final Set<Uniform> uniformsFullSet;
    private final Shader vertexShader;
    private final Shader fragmentShader;
    private final Shader geometricShader;
    private final String id;

    public ShaderGroup(String shader, int types) {
        this.id = shader;
        this.uniformsFullSet = new HashSet<>();
        Shader geometricShader1 = null;
        Shader vertexShader1 = null;
        Shader fragmentShader1 = null;
        if ((types & Shader.ShaderType.FRAGMENT_BIT) != 0) {
            fragmentShader1 = new Shader(Shader.ShaderType.FRAGMENT, shader);
        }
        if ((types & Shader.ShaderType.VERTEX_BIT) != 0) {
            vertexShader1 = new Shader(Shader.ShaderType.VERTEX, shader);
        }
        if ((types & Shader.ShaderType.GEOMETRIC_BIT) != 0) {
            geometricShader1 = new Shader(Shader.ShaderType.GEOMETRIC, shader);
        }
        this.vertexShader = vertexShader1;
        this.fragmentShader = fragmentShader1;
        this.geometricShader = geometricShader1;
    }

    public void initAll() {
        if (this.getFragmentShader() != null) {
            SystemLogging.get().getLogManager().log("Initializing " + this.getFragmentShader().getShaderName() + this.getFragmentShader().getShaderType().getFile());
            this.getFragmentShader().init();
            this.getUniformsFullSet().addAll(this.getFragmentShader().getUniforms());
        }
        if (this.getVertexShader() != null) {
            SystemLogging.get().getLogManager().log("Initializing " + this.getVertexShader().getShaderName() + this.getVertexShader().getShaderType().getFile());
            this.getVertexShader().init();
            this.getUniformsFullSet().addAll(this.getVertexShader().getUniforms());
        }
        if (this.getGeometricShader() != null) {
            SystemLogging.get().getLogManager().log("Initializing " + this.getGeometricShader().getShaderName() + this.getGeometricShader().getShaderType().getFile());
            this.getGeometricShader().init();
            this.getUniformsFullSet().addAll(this.getGeometricShader().getUniforms());
        }
    }

    public Set<Uniform> getUniformsFullSet() {
        return this.uniformsFullSet;
    }

    public String getId() {
        return this.id;
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