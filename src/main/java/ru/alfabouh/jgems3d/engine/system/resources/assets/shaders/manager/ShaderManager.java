package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.ShaderProgram;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.UniformBufferProgram;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.UniformProgram;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.ShaderGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.Uniform;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ShaderManager {
    private final Map<UniformBufferObject, UniformBufferProgram> uniformBufferProgramMap;
    private final Set<UniformBufferObject> uniformBufferObjects;
    private final ShaderGroup shaderGroup;
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;
    private boolean useForGBuffer;

    public ShaderManager(ShaderGroup shaderGroup) {
        this.shaderGroup = shaderGroup;
        this.uniformBufferProgramMap = new HashMap<>();
        this.uniformBufferObjects = new HashSet<>();

        this.useForGBuffer = false;
    }

    public ShaderManager setUseForGBuffer(boolean useForGBuffer) {
        this.useForGBuffer = useForGBuffer;
        return this;
    }

    public boolean isUseForGBuffer() {
        return this.useForGBuffer;
    }

    public abstract ShaderManager copy();

    public boolean checkUniformInGroup(String uniform) {
        for (Uniform u : this.getShaderGroup().getUniformsFullSet()) {
            if (u.getId().equals(uniform)) {
                return true;
            }
        }
        return false;
    }

    public ShaderManager addUBO(UniformBufferObject uniformBufferObject) {
        this.getUniformBufferObjects().add(uniformBufferObject);
        return this;
    }

    public void startProgram() {
        this.initShaders(new ShaderProgram());
    }

    public void destroyProgram() {
        if (this.shaderProgram != null) {
            this.shaderProgram.clean();
        }
    }

    public void bind() {
        this.getShaderProgram().bind();
    }

    public void unBind() {
        this.getShaderProgram().unbind();
    }

    public UniformBufferProgram getUniformBufferProgram(@NotNull UniformBufferObject uniformBufferObject) {
        UniformBufferProgram uniformBufferProgram = this.uniformBufferProgramMap.get(uniformBufferObject);
        if (uniformBufferProgram == null) {
            SystemLogging.get().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Unknown UBO " + uniformBufferObject);
        }
        return uniformBufferProgram;
    }

    public void performUniform(String uniform, String postfix, int arrayPos, Object o) {
        if (o == null) {
            SystemLogging.get().getLogManager().error("[" + this.getShaderGroup().getId() + "] NULL uniform " + uniform);
            return;
        }
        if (!this.checkUniformInGroup(uniform)) {
            SystemLogging.get().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Unknown uniform " + uniform);
            return;
        }
        if (arrayPos >= 0) {
            uniform += "[" + arrayPos + "]" + postfix;
        }
        if (!this.getUniformProgram().setUniform(uniform, o)) {
            SystemLogging.get().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Wrong arguments! U: " + uniform);
        }
    }

    public void performUniformNoWarn(String uniform, String postfix, int arrayPos, Object o) {
        if (this.checkUniformInGroup(uniform)) {
            this.performUniform(uniform, postfix, arrayPos, o);
        }
    }

    public void performUniform(String uniform, int arrayPos, Object o) {
        this.performUniform(uniform, "", arrayPos, o);
    }

    public void performUniformNoWarn(String uniform, int arrayPos, Object o) {
        if (this.checkUniformInGroup(uniform)) {
            this.performUniform(uniform, "", arrayPos, o);
        }
    }

    public void performUniformNoWarn(String uniform, Object o) {
        if (this.checkUniformInGroup(uniform)) {
            this.performUniform(uniform, -1, o);
        }
    }

    public void performArrayUniform(String uniform, float[] objects) {
        for (int i = 0; i < objects.length; i++) {
            this.performUniform(uniform, i, objects[i]);
        }
    }

    public void performUniform(String uniform, Object o) {
        this.performUniform(uniform, -1, o);
    }

    public void performUniformBuffer(UniformBufferObject uniform, IntBuffer data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(UniformBufferObject uniform, ByteBuffer data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(UniformBufferObject uniform, FloatBuffer data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(UniformBufferObject uniform, float[] data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(UniformBufferObject uniformBufferObject, int offset, ByteBuffer data) {
        UniformBufferProgram uniformBufferProgram = this.getUniformBufferProgram(uniformBufferObject);
        if (uniformBufferProgram != null) {
            uniformBufferProgram.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, int offset, IntBuffer data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, int offset, FloatBuffer data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, int offset, float[] data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createShader(this.getShaderGroup());
        if (shaderProgram.link()) {
            SystemLogging.get().getLogManager().log("Shader " + this.getShaderGroup().getId() + " successfully linked");
        } else {
            throw new JGemsException("Found problems in shader " + this.getShaderGroup().getId());
        }
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
    }

    @SuppressWarnings("all")
    private boolean tryCreateUniform(String value) {
        if (!this.getUniformProgram().createUniform(value)) {
            SystemLogging.get().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Could not find uniform " + value);
            return false;
        }
        return true;
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        if (this.getShaderGroup().getUniformsFullSet().isEmpty()) {
            SystemLogging.get().getLogManager().warn("Warning! No Uniforms found in: " + this.getShaderGroup().getId());
        }
        for (Uniform uniform : this.getShaderGroup().getUniformsFullSet()) {
            if (uniform.getArraySize() > 1) {
                for (int i = 0; i < uniform.getArraySize(); i++) {
                    if (!uniform.getFields().isEmpty()) {
                        for (String field : uniform.getFields()) {
                            this.tryCreateUniform(uniform.getId() + "[" + i + "]." + field);
                        }
                    } else {
                        this.tryCreateUniform(uniform.getId() + "[" + i + "]");
                    }
                }
            } else {
                this.tryCreateUniform(uniform.getId());
            }
        }
        this.initUniformBuffers();
    }

    private void initUniformBuffers() {
        for (UniformBufferObject uniformBufferObject : this.getUniformBufferObjects()) {
            UniformBufferProgram uniformBufferProgram = new UniformBufferProgram(this.shaderProgram.getProgramId(), uniformBufferObject.getId());
            if (uniformBufferProgram.createUniformBuffer(uniformBufferObject.getBinding(), uniformBufferObject.getBufferSize())) {
                SystemLogging.get().getLogManager().log("[" + this.getShaderGroup().getId() + "] Linked UBO " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            } else {
                throw new JGemsException("[" + this.getShaderGroup().getId() + "] Couldn't link " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            }
            this.uniformBufferProgramMap.put(uniformBufferObject, uniformBufferProgram);
        }
    }

    public Set<UniformBufferObject> getUniformBufferObjects() {
        return this.uniformBufferObjects;
    }

    public ShaderGroup getShaderGroup() {
        return this.shaderGroup;
    }

    public ShaderProgram getShaderProgram() {
        return this.shaderProgram;
    }

    public UniformProgram getUniformProgram() {
        return this.uniformProgram;
    }
}
