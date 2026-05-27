package javagems3d.system.resources.assets.shaders.manager;

import javagems3d.graphics.rendering.programs.shaders.CShaderProgram;
import javagems3d.graphics.rendering.programs.shaders.GShaderProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformBufferProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.base.ShaderHandler;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import logger.Log;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class ShaderManager implements ICached, ICopyable<ShaderManager> {
    private final Set<UniformBufferObject> uniformBufferObjects;
    private final ShadersContainer shadersContainer;
    private ActiveShader activeShader;
    private ShaderHandler graphicShaderHandler;
    private ShaderHandler computingShaderHandler;
    private final Set<String> usedTexturesCache;

    private boolean warns;

    public ShaderManager(ShadersContainer shadersContainer) {
        this.uniformBufferObjects = new HashSet<>();
        this.shadersContainer = shadersContainer;
        this.activeShader = ActiveShader.NONE;
        this.usedTexturesCache = new HashSet<>();

        this.warns = true;
    }

    public void disableWarns() {
        this.warns = false;
    }

    public void enableWarns() {
        this.warns = true;
    }

    public void clearUsedTextureSlots() {
        this.usedTexturesCache.clear();
    }

    public ShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        this.uniformBufferObjects.addAll(Arrays.asList(uniformBufferObjects));
        return this;
    }

    public void startProgram() {
        CShaderProgram cShaderProgram = this.getShadersContainer().getComputeShader() != null ? new CShaderProgram() : null;
        this.initShaders(this.getShadersContainer(), new GShaderProgram(), cShaderProgram);
    }

    public void destroyProgram() {
        if (this.getComputingShaderGroup() != null) {
            this.getComputingShaderGroup().clear();
        }
        if (this.getGraphicShaderGroup() != null) {
            this.getGraphicShaderGroup().clear();
        }
        this.getShadersContainer().clear();
    }

    public void dispatchComputeShader(int grX, int grY, int grZ, int barrier) {
        if (this.getShadersContainer().getComputeShader() == null) {
            Log.get().error("[" + this + "]" + " doesn't have compute program");
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
                Log.get().error("Couldn't operate with ShaderManager: " + this);
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
                Log.get().error("Couldn't operate with ShaderManager: " + this);
            }
        }
        return false;
    }

    private boolean setUniform(UniformString uniform, UniformProgram.UFunction UFUnction) {
        switch (this.activeShader) {
            case COMPUTE: {
                return this.getComputingShaderGroup().getUniformProgram().setUniform(uniform, UFUnction);
            }
            case GRAPHICAL: {
                return this.getGraphicShaderGroup().getUniformProgram().setUniform(uniform, UFUnction);
            }
            case NONE:
            default: {
                Log.get().error("Couldn't operate with ShaderManager: " + this);
            }
        }
        return false;
    }

    public void performUniformTexture(UniformString uniform, ITextureProgram program) {
        this.performUniformTexture(uniform, program, this.usedTexturesCache.size());
    }

    public void performUniformTexture(UniformString uniform, ITextureProgram program, int textureUnit) {
        if (!this.isUniformExist(uniform)) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Unknown uniform " + uniform);
            }
            return;
        }
        if (textureUnit < 0 || this.getUsedTextureUnits() >= JGemsHelper.render().getMaxTextureUnits()) {
            Log.get().error("[" + this + "] Texture attachments overflow");
            return;
        }
        if (!program.isValid()) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Wrong textureID: " + program.getTextureId() + " - (" + program + ")");
            }
            return;
        }

        GL46.glActiveTexture(GL46.GL_TEXTURE0 + textureUnit);
        if (program.isSamplerValid()) {
            program.bindSampler(textureUnit);
        } else {
            program.unBindSampler(textureUnit);
        }
        program.bindTexture();
        this.usedTexturesCache.add(uniform.toString());
        this.performUniform(uniform, UniformFunctions.INTEGER(textureUnit));
    }

    public void performUniformTexture(UniformString uniform, int textureID, int samplerId, int textureAttachment) {
        this.performUniformTexture(uniform, textureID, samplerId, textureAttachment, this.usedTexturesCache.size());
    }

    public void performUniformTexture(UniformString uniform, int textureID, int samplerId, int textureAttachment, int textureUnit) {
        if (!this.isUniformExist(uniform)) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Unknown uniform " + uniform);
            }
            return;
        }
        if (textureUnit < 0 || this.getUsedTextureUnits() >= JGemsHelper.render().getMaxTextureUnits()) {
            Log.get().error("[" + this + "] Texture attachments overflow");
            return;
        }
        if (textureID < 0) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Wrong textureID: " + textureID);
            }
            return;
        }

        GL46.glActiveTexture(GL46.GL_TEXTURE0 + textureUnit);
        if (samplerId > 0) {
            GL46.glBindSampler(textureUnit, samplerId);
        }
        GL46.glBindTexture(textureAttachment, textureID);
        this.usedTexturesCache.add(uniform.toString());
        this.performUniform(uniform, UniformFunctions.INTEGER(textureUnit));
    }

    public void performUniformTextureBindless(UniformString uniform, ITextureProgram program) {
        if (!this.isUniformExist(uniform)) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Unknown uniform " + uniform);
            }
            return;
        }
        if (!program.isBindless()) {
            Log.get().error("[" + this + "] Texture is not bindless! Uniform: " + uniform);
            return;
        }
        ITextureBindless textureBindless = (ITextureBindless) program;
        this.performUniform(uniform, UniformFunctions.VEC2UI(textureBindless.getBindingHandler()));
    }

    public void performUniformTextureBindless(UniformString uniform, long arbHandler) {
        if (!this.isUniformExist(uniform)) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Unknown uniform " + uniform);
            }
            return;
        }
        this.performUniform(uniform, UniformFunctions.VEC2UI(arbHandler));
    }

    private void initShaders(ShadersContainer shadersContainer, GShaderProgram gShaderProgram, CShaderProgram cShaderProgram) {
        boolean flag = false;
        if (gShaderProgram != null) {
            this.graphicShaderHandler = new ShaderHandler(this.getShadersContainer().getId());
            if (gShaderProgram.createShader(this.getShadersContainer().getFragmentShader(), this.getShadersContainer().getVertexShader(), this.getShadersContainer().getGeometricShader(), this.getShadersContainer().getTesselationControlShader(), this.getShadersContainer().getTesselationEvaluationShader())) {
                if (gShaderProgram.link()) {
                    Log.get().info("G-Shader " + this + " linking... (program id=" + gShaderProgram.getProgramId() + ")");
                } else {
                    throw new JGemsRuntimeException("Found problems in g-shader " + this);
                }
                flag = true;
            }
            this.getGraphicShaderGroup().initShaderGroup(gShaderProgram, shadersContainer.getGUniformsFullSet(), this.uniformBufferObjects);
        }
        if (cShaderProgram != null) {
            this.computingShaderHandler = new ShaderHandler(this.getShadersContainer().getId());
            if (cShaderProgram.createShader(this.getShadersContainer().getComputeShader())) {
                if (cShaderProgram.link()) {
                    Log.get().info("C-Shader " + this + " linking... (program id=" + cShaderProgram.getProgramId() + ")");
                } else {
                    throw new JGemsRuntimeException("Found problems in c-shader " + this);
                }
                flag = true;
            }
            this.getComputingShaderGroup().initShaderGroup(cShaderProgram, shadersContainer.getCUniformsFullSet(), this.uniformBufferObjects);
        }
        if (!flag) {
            throw new JGemsRuntimeException("Wrong ShaderManager passed in system");
        }
    }

    public void performUniform(UniformString uniform, UniformProgram.UFunction UFUnction) {
        if (UFUnction == null) {
            Log.get().error("[" + this + "] NULL uniform " + uniform);
            return;
        }
        if (!this.isUniformExist(uniform)) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Unknown uniform " + uniform);
            }
            return;
        }
        if (!this.setUniform(uniform, UFUnction)) {
            if (this.isWarnsEnabled()) {
                Log.get().warn("[" + this + "] Wrong arguments! U: " + uniform);
            }
        }
    }

    public void performUniformNoWarn(UniformString uniformString, UniformProgram.UFunction o) {
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

    public void performUniformBuffer(UniformBufferObject uniform, long offset, ByteBuffer data) {
        UniformBufferProgram uniformBufferProgram = this.getUniformBufferProgram(uniform);
        if (uniformBufferProgram != null) {
            uniformBufferProgram.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, long offset, IntBuffer data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, long offset, FloatBuffer data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, long offset, float[] data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    public int getUsedTextureUnits() {
        return this.usedTexturesCache.size();
    }

    public ShaderHandler getComputingShaderGroup() {
        return this.computingShaderHandler;
    }

    public ShaderHandler getGraphicShaderGroup() {
        return this.graphicShaderHandler;
    }

    public ShadersContainer getShadersContainer() {
        return this.shadersContainer;
    }

    @Override
    public void onClearingCache(ResourceCache resourceCache) {
        this.destroyProgram();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ShaderManager that = (ShaderManager) o;
        return Objects.equals(this.shadersContainer, that.shadersContainer);
    }

    public boolean isWarnsEnabled() {
        return this.warns;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.shadersContainer);
    }

    public String toString() {
        return this.getShadersContainer().getId();
    }

    private enum ActiveShader {
        COMPUTE,
        GRAPHICAL,
        NONE
    }
}