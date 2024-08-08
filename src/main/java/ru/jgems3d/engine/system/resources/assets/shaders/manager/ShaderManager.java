package ru.jgems3d.engine.system.resources.assets.shaders.manager;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders.CShaderProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders.GShaderProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders.unifroms.UniformBufferProgram;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.assets.shaders.*;
import ru.jgems3d.engine.system.service.exceptions.JGemsException;
import ru.jgems3d.engine.system.resources.cache.ICached;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public abstract class ShaderManager implements ICached {
    private ACT_SHADER activeShader;
    private final Set<UniformBufferObject> uniformBufferObjects;
    private ShaderGroup graphicShaderGroup;
    private ShaderGroup computingShaderGroup;
    private final ShaderContainer shaderContainer;
    private RenderPass renderPass;

    private int usedTextureUnits;

    public ShaderManager(ShaderContainer shaderContainer) {
        this.uniformBufferObjects = new HashSet<>();
        this.shaderContainer = shaderContainer;
        this.renderPass = RenderPass.FORWARD;
        this.activeShader = ACT_SHADER.NONE;

        this.usedTextureUnits = 0;
    }

    public void updateTextureUnitSlots() {
        this.usedTextureUnits = 0;
    }

    public abstract ShaderManager copy();

    public ShaderManager setShaderRenderPass(RenderPass renderPass) {
        this.renderPass = renderPass;
        return this;
    }

    public ShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        this.uniformBufferObjects.addAll(Arrays.asList(uniformBufferObjects));
        return this;
    }

    public void startProgram() {
        CShaderProgram cShaderProgram = this.getShaderContainer().getComputeShader() != null ? new CShaderProgram() : null;
        this.initShaders(this.getShaderContainer(), new GShaderProgram(), cShaderProgram);
    }

    public void destroyProgram() {
        if (this.getComputingShaderGroup() != null) {
            this.getComputingShaderGroup().clean();
        }
        if (this.getGraphicShaderGroup() != null) {
            this.getGraphicShaderGroup().clean();
        }
        this.getShaderContainer().clean();
    }

    public void dispatchComputeShader(int grX, int grY, int grZ, int barrier) {
        if (this.getShaderContainer().getComputeShader() == null) {
            JGemsHelper.getLogger().warn("[" + this + "]" + " doesn't have compute program!");
            return;
        }
        GL43.glDispatchCompute(grX, grY, grZ);
        if (barrier > 0) {
            GL43.glMemoryBarrier(barrier);
        }
    }

    public void startComputing() {
        this.updateTextureUnitSlots();
        this.getComputingShaderGroup().getShaderProgram().bind();
        this.activeShader = ACT_SHADER.COMPUTE;
    }

    public void endComputing() {
        this.getComputingShaderGroup().getShaderProgram().unbind();
        this.activeShader = ACT_SHADER.NONE;
    }

    public void bind() {
        this.updateTextureUnitSlots();
        this.getGraphicShaderGroup().getShaderProgram().bind();
        this.activeShader = ACT_SHADER.GRAPHICAL;
    }

    public void unBind() {
        this.getGraphicShaderGroup().getShaderProgram().unbind();
        this.activeShader = ACT_SHADER.NONE;
    }

    public UniformBufferProgram getUniformBufferProgram(UniformBufferObject uniform) {
        switch (this.activeShader) {
            case COMPUTE: {
                return this.getComputingShaderGroup().getUniformBufferProgram(uniform);
            }
            case GRAPHICAL: {
                return this.getGraphicShaderGroup().getUniformBufferProgram(uniform);
            }
            case NONE:
            default: {
                JGemsHelper.getLogger().error("Couldn't operate with ShaderManager: " + this);
            }
        }
        return null;
    }

    public boolean isUniformExist(UniformString uniform) {
        switch (this.activeShader) {
            case COMPUTE: {
                return this.getComputingShaderGroup().checkUniformInProgram(uniform);
            }
            case GRAPHICAL: {
                return this.getGraphicShaderGroup().checkUniformInProgram(uniform);
            }
            case NONE:
            default: {
                JGemsHelper.getLogger().error("Couldn't operate with ShaderManager: " + this);
            }
        }
        return false;
    }

    private boolean setUniform(UniformString uniform, Object o) {
        switch (this.activeShader) {
            case COMPUTE: {
                return this.getComputingShaderGroup().getUniformProgram().setUniform(uniform, o);
            }
            case GRAPHICAL: {
                return this.getGraphicShaderGroup().getUniformProgram().setUniform(uniform, o);
            }
            case NONE:
            default: {
                JGemsHelper.getLogger().error("Couldn't operate with ShaderManager: " + this);
            }
        }
        return false;
    }

    public void performUniformTexture(UniformString uniform, int textureID, int textureAttachment) {
        this.performUniformTexture(uniform, textureID, textureAttachment, this.usedTextureUnits++);
    }

    public void performUniformTexture(UniformString uniform, int textureID, int textureAttachment, int textureUnit) {
        if (!this.isUniformExist(uniform)) {
            JGemsHelper.getLogger().warn("[" + this + "] Unknown uniform " + uniform);
            return;
        }
        if (textureUnit < 0 || this.usedTextureUnits >= JGemsSceneUtils.getMaxTextureUnits()) {
            JGemsHelper.getLogger().error("[" + this + "] Texture attachments overflow!");
            return;
        }
        if (textureID < 0) {
            JGemsHelper.getLogger().warn("[" + this + "] Wrong textureID: " + textureID);
            return;
        }

        GL30.glActiveTexture(GL30.GL_TEXTURE0 + textureUnit);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);

        GL30.glBindTexture(textureAttachment, textureID);
        this.performUniform(uniform, textureUnit);
    }

    private void initShaders(ShaderContainer shaderContainer, GShaderProgram gShaderProgram, CShaderProgram cShaderProgram) {
        boolean flag = false;
        if (gShaderProgram != null) {
            this.graphicShaderGroup = new ShaderGroup(this.getShaderContainer().getId());
            if (gShaderProgram.createShader(this.getShaderContainer().getFragmentShader(), this.getShaderContainer().getVertexShader(), this.getShaderContainer().getGeometricShader())) {
                if (gShaderProgram.link()) {
                    JGemsHelper.getLogger().log("G-Shader " + this + " successfully linked");
                } else {
                    throw new JGemsRuntimeException("Found problems in g-shader " + this);
                }
                flag = true;
            }
            this.getGraphicShaderGroup().initShaderGroup(gShaderProgram, shaderContainer.getGUniformsFullSet(), this.uniformBufferObjects);
        }
        if (cShaderProgram != null) {
            this.computingShaderGroup = new ShaderGroup(this.getShaderContainer().getId());
            if (cShaderProgram.createShader(this.getShaderContainer().getComputeShader())) {
                if (cShaderProgram.link()) {
                    JGemsHelper.getLogger().log("C-Shader " + this + " successfully linked");
                } else {
                    throw new JGemsRuntimeException("Found problems in c-shader " + this);
                }
                flag = true;
            }
            this.getComputingShaderGroup().initShaderGroup(cShaderProgram, shaderContainer.getCUniformsFullSet(), this.uniformBufferObjects);
        }
        if (!flag) {
            throw new JGemsRuntimeException("Wrong ShaderManager passed in system!");
        }
    }

    public void performUniform(UniformString uniform, Object o) {
        if (o == null) {
            JGemsHelper.getLogger().error("[" + this + "] NULL uniform " + uniform);
            return;
        }
        if (!this.isUniformExist(uniform)) {
            JGemsHelper.getLogger().warn("[" + this + "] Unknown uniform " + uniform);
            return;
        }
        if (!this.setUniform(uniform, o)) {
            JGemsHelper.getLogger().warn("[" + this + "] Wrong arguments! U: " + uniform);
        }
    }

    public void performUniformNoWarn(UniformString uniformString, Object o) {
        if (this.isUniformExist(uniformString)) {
            this.performUniform(uniformString, o);
        }
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

    public void performUniformBuffer(UniformBufferObject uniform, int offset, ByteBuffer data) {
        UniformBufferProgram uniformBufferProgram = this.getUniformBufferProgram(uniform);
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

    public boolean checkShaderRenderPass(RenderPass renderPass) {
        return this.getShaderRenderPass().equals(renderPass);
    }

    public RenderPass getShaderRenderPass() {
        return this.renderPass;
    }

    public ShaderGroup getComputingShaderGroup() {
        return this.computingShaderGroup;
    }

    public ShaderGroup getGraphicShaderGroup() {
        return this.graphicShaderGroup;
    }

    public ShaderContainer getShaderContainer() {
        return this.shaderContainer;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.destroyProgram();
    }

    public String toString() {
        return this.getShaderContainer().getId();
    }

    private enum ACT_SHADER {
        COMPUTE,
        GRAPHICAL,
        NONE
    };
}