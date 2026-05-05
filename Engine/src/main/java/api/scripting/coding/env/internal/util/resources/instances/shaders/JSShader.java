package api.scripting.coding.env.internal.util.resources.instances.shaders;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.math.JSVector4f;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTextureI;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformProgram;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.lwjgl.opengl.GL46;

@JSCodingClass(binding = "JSShader", description = "Shader wrapper for scripts. Set uniforms, textures, and manage shader state.")
public class JSShader {
    @JSCodingField(description = "Real java object")
    private final JGemsShaderManager shaderManager;

    @JSCodingConstructor(description = "Wrap existing shader manager", paramNames = {"mainSceneShaderManager"})
    public JSShader(JGemsShaderManager shaderManager) {
        this.shaderManager = shaderManager;
    }

    @JSCodingConstructor(description = "Copy shader", paramNames = {"shader"})
    public JSShader(JSShader shader) {
        this.shaderManager = shader.shaderManager.copy();
    }

    @JSCodingFunctionOrMethod(description = "Copy shader")
    public JSShader copy() {
        return new JSShader(this);
    }

    @JSCodingFunctionOrMethod(description = "Check if uniform exists", paramNames = {"uniform"})
    public boolean isUniformExist(JSShaderVar uniform) {
        return this.shaderManager.isUniformExist(uniform.getJavaUniformString());
    }

    @JSCodingFunctionOrMethod(description = "Perform uniform with integer", paramNames = {"uniform", "value"})
    public void uniformInt(JSShaderVar uniform, int value) {
        this.shaderManager.performUniform(uniform.getJavaUniformString(), UniformFunctions.INTEGER(value));
    }

    @JSCodingFunctionOrMethod(description = "Perform uniform with float", paramNames = {"uniform", "value"})
    public void uniformFloat(JSShaderVar uniform, float value) {
        this.shaderManager.performUniform(uniform.getJavaUniformString(), UniformFunctions.FLOAT(value));
    }

    @JSCodingFunctionOrMethod(description = "Perform uniform with Vector2f", paramNames = {"uniform", "value"})
    public void uniformVec2(JSShaderVar uniform, JSVector2f value) {
        this.shaderManager.performUniform(uniform.getJavaUniformString(), UniformFunctions.VEC2F(value.getJavaVector2f()));
    }

    @JSCodingFunctionOrMethod(description = "Perform uniform with Vector3f", paramNames = {"uniform", "value"})
    public void uniformVec3(JSShaderVar uniform, JSVector3f value) {
        this.shaderManager.performUniform(uniform.getJavaUniformString(), UniformFunctions.VEC3F(value.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Perform uniform with Vector4f", paramNames = {"uniform", "value"})
    public void uniformVec4(JSShaderVar uniform, JSVector4f value) {
        this.shaderManager.performUniform(uniform.getJavaUniformString(), UniformFunctions.VEC4F(value.getJavaVector4f()));
    }

    @JSCodingFunctionOrMethod(description = "Perform uniform with Matrix4f", paramNames = {"uniform", "matrix"})
    public void uniformMat4(JSShaderVar uniform, JSMatrix4f matrix) {
        this.shaderManager.performUniform(uniform.getJavaUniformString(), UniformFunctions.MAT4F(matrix.getJavaMatrix4f()));
    }

    @JSCodingFunctionOrMethod(description = "Perform uniform with custom function", paramNames = {"uniform", "func"})
    public void uniformCustom(JSShaderVar uniform, UniformProgram.UFunction func) {
        this.shaderManager.performUniform(uniform.getJavaUniformString(), func);
    }

    @JSCodingFunctionOrMethod(description = "Bind texture to uniform", paramNames = {"uniform", "textureID", "samplerID"})
    public void uniformTexture(JSShaderVar uniform, int textureID, int samplerID) {
        this.shaderManager.performUniformTexture(uniform.getJavaUniformString(), textureID, samplerID, GL46.GL_TEXTURE_2D);
    }

    @JSCodingFunctionOrMethod(description = "Bind bindless texture to uniform", paramNames = {"uniform", "handle"})
    public void uniformTextureBindless(JSShaderVar uniform, long handle) {
        this.shaderManager.performUniformTextureBindless(uniform.getJavaUniformString(), handle);
    }

    @JSCodingFunctionOrMethod(description = "Bind texture to uniform", paramNames = {"uniform", "textureID", "samplerID"})
    public void uniformTexture(JSShaderVar uniform, JSTextureI textureI) {
        this.shaderManager.performUniformTexture(uniform.getJavaUniformString(), textureI.getJavaTextureI());
    }

    @JSCodingFunctionOrMethod(description = "Bind bindless texture to uniform", paramNames = {"uniform", "handle"})
    public void uniformTextureBindless(JSShaderVar uniform, JSTextureI textureI) {
        this.shaderManager.performUniformTextureBindless(uniform.getJavaUniformString(), textureI.getJavaTextureI());
    }

    @JSCodingFunctionOrMethod(description = "Bind shader for rendering")
    public void begin() {
        this.shaderManager.beginShading();
    }

    @JSCodingFunctionOrMethod(description = "Unbind shader")
    public void end() {
        this.shaderManager.endShading();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying shader manager")
    public JGemsShaderManager getJavaShaderManager() {
        return this.shaderManager;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "JSShader[" + this.shaderManager.toString() + "]";
    }
}