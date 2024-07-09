package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.shaders.IShaderProgram;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.shaders.unifroms.UniformBufferProgram;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.programs.shaders.unifroms.UniformProgram;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShaderGroup {
    private Set<Uniform> rawUniforms;
    private final Map<UniformBufferObject, UniformBufferProgram> uniformBufferProgramMap;
    private UniformProgram uniformProgram;
    private IShaderProgram shaderProgram;
    private final String id;

    public ShaderGroup(String id) {
        this.id = id;
        this.uniformBufferProgramMap = new HashMap<>();
    }

    public void initShaderGroup(IShaderProgram shaderProgram, Set<Uniform> uniforms, Set<UniformBufferObject> uniformBufferObjects) {
        this.uniformProgram = new UniformProgram(shaderProgram.getProgramId());
        this.shaderProgram = shaderProgram;

        this.initUniforms(uniforms);
        this.initUniformBuffers(uniformBufferObjects);
    }

    @SuppressWarnings("all")
    private boolean tryCreateUniform(UniformProgram uniformProgram, String value) {
        if (!uniformProgram.createUniform(value)) {
            SystemLogging.get().getLogManager().warn("[" + this + "] Could not find uniform " + value);
            return false;
        }
        return true;
    }

    private void initUniforms(Set<Uniform> uniforms) {
        this.rawUniforms = uniforms;
        if (uniforms.isEmpty()) {
            SystemLogging.get().getLogManager().warn("Warning! No Uniforms found in: " + this);
        }
        for (Uniform uniform : uniforms) {
            if (uniform.getArraySize() > 1) {
                for (int i = 0; i < uniform.getArraySize(); i++) {
                    if (!uniform.getFields().isEmpty()) {
                        for (String field : uniform.getFields()) {
                            this.tryCreateUniform(this.getUniformProgram(), uniform.getId() + "[" + i + "]." + field);
                        }
                    } else {
                        this.tryCreateUniform(this.getUniformProgram(), uniform.getId() + "[" + i + "]");
                    }
                }
            } else {
                this.tryCreateUniform(this.getUniformProgram(), uniform.getId());
            }
        }
    }

    private void initUniformBuffers(Set<UniformBufferObject> uniformBufferObjects) {
        for (UniformBufferObject uniformBufferObject : uniformBufferObjects) {
            UniformBufferProgram uniformBufferProgram = new UniformBufferProgram(shaderProgram.getProgramId(), uniformBufferObject.getId());
            if (uniformBufferProgram.createUniformBuffer(uniformBufferObject.getBinding(), uniformBufferObject.getBufferSize())) {
                SystemLogging.get().getLogManager().log("[" + this.id + "] Linked UBO " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            } else {
                SystemLogging.get().getLogManager().error("[" + this.id + "] Couldn't link" + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            }
            this.uniformBufferProgramMap.put(uniformBufferObject, uniformBufferProgram);
        }
    }

    public UniformBufferProgram getUniformBufferProgram(@NotNull UniformBufferObject uniformBufferObject) {
        UniformBufferProgram uniformBufferProgram = this.getUniformBufferProgramMap().get(uniformBufferObject);
        if (uniformBufferProgram == null) {
            SystemLogging.get().getLogManager().warn("[" + this + "] Unknown UBO " + uniformBufferObject);
        }
        return uniformBufferProgram;
    }

    public void clean() {
        this.getShaderProgram().clean();
    }

    public boolean checkUniformInProgram(String uniform) {
        return this.rawUniforms.stream().anyMatch(e -> e.getId().equals(uniform));
    }

    public boolean checkIsShaderActive() {
        return GL30.glGetInteger(GL30.GL_CURRENT_PROGRAM) == this.getShaderProgram().getProgramId();
    }

    public Map<UniformBufferObject, UniformBufferProgram> getUniformBufferProgramMap() {
        return this.uniformBufferProgramMap;
    }

    public IShaderProgram getShaderProgram() {
        return this.shaderProgram;
    }

    public UniformProgram getUniformProgram() {
        return this.uniformProgram;
    }

    public String toString() {
        return this.id;
    }
}
