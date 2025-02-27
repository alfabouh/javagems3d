package javagems3d.system.resources.assets.shaders.base;

import javagems3d.system.resources.assets.shaders.uniform.Uniform;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.programs.shaders.IShaderProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformBufferProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformProgram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShaderHandler {
    private final Map<UniformBufferObject, UniformBufferProgram> uniformBufferProgramMap;
    private final String id;
    private final Set<UniformString> rawUniforms;
    private UniformProgram uniformProgram;
    private IShaderProgram shaderProgram;

    public ShaderHandler(String id) {
        this.id = id;
        this.uniformBufferProgramMap = new HashMap<>();
        this.rawUniforms = new HashSet<>();
    }

    public void initShaderGroup(IShaderProgram shaderProgram, Set<Uniform> uniforms, Set<UniformBufferObject> uniformBufferObjects) {
        this.uniformProgram = new UniformProgram(shaderProgram.getProgramId());
        this.shaderProgram = shaderProgram;

        this.initUniforms(uniforms);
        this.initUniformBuffers(uniformBufferObjects);
    }

    @SuppressWarnings("all")
    private boolean tryCreateUniform(UniformProgram uniformProgram, UniformString value) {
        if (!uniformProgram.createUniform(value)) {
            Log.get().warn("[" + this + "] Could not find uniform " + value);
            return false;
        }
        this.rawUniforms.add(value);
        return true;
    }

    private void initUniforms(Set<Uniform> uniforms) {
        if (uniforms.isEmpty()) {
            Log.get().warn("Warning! No Uniforms found in: " + this);
        }
        for (Uniform uniform : uniforms) {
            if (uniform.getArraySize() > 1) {
                for (int i = 0; i < uniform.getArraySize(); i++) {
                    if (!uniform.getFields().isEmpty()) {
                        for (String field : uniform.getFields()) {
                            this.tryCreateUniform(this.getUniformProgram(), new UniformString(uniform.getId(), "." + field, i));
                        }
                    } else {
                        this.tryCreateUniform(this.getUniformProgram(), new UniformString(uniform.getId(), i));
                    }
                }
            }
            this.tryCreateUniform(this.getUniformProgram(), new UniformString(uniform.getId()));
        }
    }

    private void initUniformBuffers(Set<UniformBufferObject> uniformBufferObjects) {
        for (UniformBufferObject uniformBufferObject : uniformBufferObjects) {
            UniformBufferProgram uniformBufferProgram = new UniformBufferProgram(shaderProgram.getProgramId(), uniformBufferObject.getId());
            if (uniformBufferProgram.createUniformBuffer(uniformBufferObject.getBinding(), uniformBufferObject.getBufferSize())) {
                Log.get().info("[" + this.id + "] Linked UBO " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            } else {
                Log.get().error("[" + this.id + "] Couldn't link " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            }
            this.uniformBufferProgramMap.put(uniformBufferObject, uniformBufferProgram);
        }
    }

    public UniformBufferProgram getUniformBufferProgram(@NotNull UniformBufferObject uniformBufferObject) {
        UniformBufferProgram uniformBufferProgram = this.getUniformBufferProgramMap().get(uniformBufferObject);
        if (uniformBufferProgram == null) {
            Log.get().warn("[" + this + "] Unknown UBO " + uniformBufferObject);
        }
        return uniformBufferProgram;
    }

    public void clear() {
        this.getShaderProgram().clear();
    }

    public boolean checkUniformInProgram(UniformString uniformString) {
        return this.rawUniforms.stream().anyMatch(e -> e.equals(uniformString));
    }

    public boolean checkIsShaderActive() {
        return GL46.glGetInteger(GL46.GL_CURRENT_PROGRAM) == this.getShaderProgram().getProgramId();
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
