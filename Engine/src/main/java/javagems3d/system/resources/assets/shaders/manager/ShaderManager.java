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

package javagems3d.system.resources.assets.shaders.manager;

import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.DefaultUniformActions;
import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.UniformProgram;
import javagems3d.system.resources.assets.shaders.RenderPass;
import javagems3d.system.resources.assets.shaders.base.*;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.programs.shaders.CShaderProgram;
import javagems3d.graphics.opengl.rendering.programs.shaders.GShaderProgram;
import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.UniformBufferProgram;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

/**
 * ShaderManager objects are shader packages that have functions for managing the state of the shader, its uniforms and uni-buffers
 */
public abstract class ShaderManager implements ICached {

    private final Set<UniformBufferObject> uniformBufferObjects;
    private final ShadersContainer shadersContainer;
    private ActiveShader activeShader;
    private ShaderHandler graphicShaderHandler;
    private ShaderHandler computingShaderHandler;
    private RenderPass renderPass;
    private int usedTextureUnits;

    public ShaderManager(ShadersContainer shadersContainer) {
        this.uniformBufferObjects = new HashSet<>();
        this.shadersContainer = shadersContainer;
        this.renderPass = RenderPass.FORWARD;
        this.activeShader = ActiveShader.NONE;
        this.usedTextureUnits = 0;
    }

    public void clearUsedTextureSlots() {
        this.usedTextureUnits = 0;
    }

    public abstract ShaderManager copy();

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
        GL46.glDispatchCompute(grX, grY, grZ);
        if (barrier > 0) {
            GL46.glMemoryBarrier(barrier);
        }
    }

    public boolean beginComputing() {
        if (this.activeShader != ActiveShader.NONE) {
            return false;
        }
        this.clearUsedTextureSlots();
        this.getComputingShaderGroup().getShaderProgram().bind();
        this.activeShader = ActiveShader.COMPUTE;
        return true;
    }

    public void endComputing() {
        this.getComputingShaderGroup().getShaderProgram().unbind();
        this.activeShader = ActiveShader.NONE;
    }

    public boolean beginShading() {
        if (this.activeShader != ActiveShader.NONE) {
            return false;
        }
        this.clearUsedTextureSlots();
        this.getGraphicShaderGroup().getShaderProgram().bind();
        this.activeShader = ActiveShader.GRAPHICAL;
        return true;
    }

    public void endShading() {
        this.getGraphicShaderGroup().getShaderProgram().unbind();
        this.activeShader = ActiveShader.NONE;
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

    private boolean setUniform(UniformString uniform, UniformProgram.UniformAction uniformAction) {
        switch (this.activeShader) {
            case COMPUTE: {
                return this.getComputingShaderGroup().getUniformProgram().setUniform(uniform, uniformAction);
            }
            case GRAPHICAL: {
                return this.getGraphicShaderGroup().getUniformProgram().setUniform(uniform, uniformAction);
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

        GL46.glActiveTexture(GL46.GL_TEXTURE0 + textureUnit);

        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);

        GL46.glBindTexture(textureAttachment, textureID);
        this.performUniform(uniform, DefaultUniformActions.INTEGER(textureUnit));
    }

    private void initShaders(ShadersContainer shadersContainer, GShaderProgram gShaderProgram, CShaderProgram cShaderProgram) {
        boolean flag = false;
        if (gShaderProgram != null) {
            this.graphicShaderHandler = new ShaderHandler(this.getShaderContainer().getId());
            if (gShaderProgram.createShader(this.getShaderContainer().getFragmentShader(), this.getShaderContainer().getVertexShader(), this.getShaderContainer().getGeometricShader(), this.getShaderContainer().getTesselationControlShader(), this.getShaderContainer().getTesselationEvaluationShader())) {
                if (gShaderProgram.link()) {
                    JGemsHelper.getLogger().log("G-Shader " + this + " successfully linked (program id=" + gShaderProgram.getProgramId() + ")");
                } else {
                    throw new JGemsRuntimeException("Found problems in g-shader " + this);
                }
                flag = true;
            }
            this.getGraphicShaderGroup().initShaderGroup(gShaderProgram, shadersContainer.getGUniformsFullSet(), this.uniformBufferObjects);
        }
        if (cShaderProgram != null) {
            this.computingShaderHandler = new ShaderHandler(this.getShaderContainer().getId());
            if (cShaderProgram.createShader(this.getShaderContainer().getComputeShader())) {
                if (cShaderProgram.link()) {
                    JGemsHelper.getLogger().log("C-Shader " + this + " successfully linked (program id=" + cShaderProgram.getProgramId() + ")");
                } else {
                    throw new JGemsRuntimeException("Found problems in c-shader " + this);
                }
                flag = true;
            }
            this.getComputingShaderGroup().initShaderGroup(cShaderProgram, shadersContainer.getCUniformsFullSet(), this.uniformBufferObjects);
        }
        if (!flag) {
            throw new JGemsRuntimeException("Wrong ShaderManager passed in system!");
        }
    }

    public void performUniform(UniformString uniform, UniformProgram.UniformAction uniformAction) {
        if (uniformAction == null) {
            JGemsHelper.getLogger().error("[" + this + "] NULL uniform " + uniform);
            return;
        }
        if (!this.isUniformExist(uniform)) {
            JGemsHelper.getLogger().warn("[" + this + "] Unknown uniform " + uniform);
            return;
        }
        if (!this.setUniform(uniform, uniformAction)) {
            JGemsHelper.getLogger().warn("[" + this + "] Wrong arguments! U: " + uniform);
        }
    }

    public void performUniformNoWarn(UniformString uniformString, UniformProgram.UniformAction o) {
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

    public ShaderManager setShaderRenderPass(RenderPass renderPass) {
        this.renderPass = renderPass;
        return this;
    }

    public ShaderHandler getComputingShaderGroup() {
        return this.computingShaderHandler;
    }

    public ShaderHandler getGraphicShaderGroup() {
        return this.graphicShaderHandler;
    }

    public ShadersContainer getShaderContainer() {
        return this.shadersContainer;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.destroyProgram();
    }

    public String toString() {
        return this.getShaderContainer().getId();
    }

    private enum ActiveShader {
        COMPUTE,
        GRAPHICAL,
        NONE
    }

}