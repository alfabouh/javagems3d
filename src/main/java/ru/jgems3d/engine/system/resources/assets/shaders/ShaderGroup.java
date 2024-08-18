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

package ru.jgems3d.engine.system.resources.assets.shaders;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders.IShaderProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders.unifroms.UniformBufferProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders.unifroms.UniformProgram;
import ru.jgems3d.engine.JGemsHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShaderGroup {
    private Set<UniformString> rawUniforms;
    private final Map<UniformBufferObject, UniformBufferProgram> uniformBufferProgramMap;
    private UniformProgram uniformProgram;
    private IShaderProgram shaderProgram;
    private final String id;

    public ShaderGroup(String id) {
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
            JGemsHelper.getLogger().warn("[" + this + "] Could not find uniform " + value);
            return false;
        }
        this.rawUniforms.add(value);
        return true;
    }

    private void initUniforms(Set<Uniform> uniforms) {
        if (uniforms.isEmpty()) {
            JGemsHelper.getLogger().warn("Warning! No Uniforms found in: " + this);
        }
        for (Uniform uniform : uniforms) {
            if (uniform.getArraySize() > 1) {
                for (int i = 0; i < uniform.getArraySize(); i++) {
                    if (!uniform.getFields().isEmpty()) {
                        for (String field : uniform.getFields()) {
                            this.tryCreateUniform(this.getUniformProgram(),  new UniformString(uniform.getId(), "." + field, i));
                        }
                    } else {
                        this.tryCreateUniform(this.getUniformProgram(), new UniformString(uniform.getId(), i));
                    }
                }
            } else {
                this.tryCreateUniform(this.getUniformProgram(), new UniformString(uniform.getId()));
            }
        }
    }

    private void initUniformBuffers(Set<UniformBufferObject> uniformBufferObjects) {
        for (UniformBufferObject uniformBufferObject : uniformBufferObjects) {
            UniformBufferProgram uniformBufferProgram = new UniformBufferProgram(shaderProgram.getProgramId(), uniformBufferObject.getId());
            if (uniformBufferProgram.createUniformBuffer(uniformBufferObject.getBinding(), uniformBufferObject.getBufferSize())) {
                JGemsHelper.getLogger().log("[" + this.id + "] Linked UBO " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            } else {
                JGemsHelper.getLogger().error("[" + this.id + "] Couldn't link " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            }
            this.uniformBufferProgramMap.put(uniformBufferObject, uniformBufferProgram);
        }
    }

    public UniformBufferProgram getUniformBufferProgram(@NotNull UniformBufferObject uniformBufferObject) {
        UniformBufferProgram uniformBufferProgram = this.getUniformBufferProgramMap().get(uniformBufferObject);
        if (uniformBufferProgram == null) {
            JGemsHelper.getLogger().warn("[" + this + "] Unknown UBO " + uniformBufferObject);
        }
        return uniformBufferProgram;
    }

    public void clean() {
        this.getShaderProgram().clean();
    }

    public boolean checkUniformInProgram(UniformString uniformString) {
        return this.rawUniforms.stream().anyMatch(e -> e.equals(uniformString));
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
