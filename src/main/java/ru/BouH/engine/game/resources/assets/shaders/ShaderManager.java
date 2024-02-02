package ru.BouH.engine.game.resources.assets.shaders;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.materials.textures.ColorSample;
import ru.BouH.engine.game.resources.assets.materials.textures.IImageSample;
import ru.BouH.engine.game.resources.assets.materials.textures.ISample;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.shadow.CascadeShadow;
import ru.BouH.engine.render.environment.shadow.PointLightShadow;
import ru.BouH.engine.render.environment.shadow.ShadowScene;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.fabric.constraints.ModelRenderConstraints;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformBufferProgram;
import ru.BouH.engine.render.scene.programs.UniformProgram;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShaderManager {
    private final Map<UniformBufferObject, UniformBufferProgram> uniformBufferProgramMap;
    private final Set<UniformBufferObject> uniformBufferObjects;
    private final ShaderGroup shaderGroup;
    private final ShaderUtils shaderUtils;
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;

    public ShaderManager(ShaderGroup shaderGroup) {
        this.shaderGroup = shaderGroup;
        this.shaderUtils = new ShaderUtils();
        this.uniformBufferProgramMap = new HashMap<>();
        this.uniformBufferObjects = new HashSet<>();
    }

    public ShaderManager copy() {
        return new ShaderManager(this.getShaderGroup());
    }

    private boolean checkUniformInGroup(String uniform) {
        for (Uniform u : this.getShaderGroup().getUniformsFullSet()) {
            if (u.getId().equals(uniform)) {
                return true;
            }
        }
        return false;
    }

    public ShaderUtils getUtils() {
        return this.shaderUtils;
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

    public ShaderGroup getShaderGroup() {
        return this.shaderGroup;
    }

    public ShaderProgram getShaderProgram() {
        return this.shaderProgram;
    }

    public UniformProgram getUniformProgram() {
        return this.uniformProgram;
    }

    public UniformBufferProgram getUniformBufferProgram(@NotNull UniformBufferObject uniformBufferObject) {
        UniformBufferProgram uniformBufferProgram = this.uniformBufferProgramMap.get(uniformBufferObject);
        if (uniformBufferProgram == null) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Unknown UBO " + uniformBufferObject);
        }
        return uniformBufferProgram;
    }

    public void performUniform(String uniform, String postfix, int arrayPos, Object o) {
        if (!this.checkUniformInGroup(uniform)) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Unknown uniform " + uniform);
            return;
        }
        if (arrayPos >= 0) {
            uniform += "[" + arrayPos + "]" + postfix;
        }
        if (!this.getUniformProgram().setUniform(uniform, o)) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Wrong arguments! U: " + uniform);
        }
    }

    public void performUniform(String uniform, int arrayPos, Object o) {
        this.performUniform(uniform, "", arrayPos, o);
    }

    public void performArrayUniform(String uniform, float[] objects) {
        for (int i = 0; i < objects.length; i++) {
            this.performUniform(uniform, i, objects[i]);
        }
    }

    public void performUniform(String uniform, Object o) {
        this.performUniform(uniform, -1, o);
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

    public Set<UniformBufferObject> getUniformBufferObjects() {
        return this.uniformBufferObjects;
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createShader(this.getShaderGroup());
        if (shaderProgram.link()) {
            Game.getGame().getLogManager().log("Shader " + this.getShaderGroup().getId() + " successfully linked");
        } else {
            Game.getGame().getLogManager().error("Found problems in shader " + this.getShaderGroup().getId());
        }
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
    }

    @SuppressWarnings("all")
    private boolean tryCreateUniform(String value) {
        if (!this.getUniformProgram().createUniform(value)) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Could not find uniform " + value);
            return false;
        }
        return true;
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        if (this.getShaderGroup().getUniformsFullSet().isEmpty()) {
            Game.getGame().getLogManager().warn("Warning! No Uniforms found in: " + this.getShaderGroup().getId());
        }
        for (Uniform uniform : this.getShaderGroup().getUniformsFullSet()) {
            if (uniform.getArraySize() > 1) {
                for (int i = 0; i < uniform.getArraySize(); i++) {
                    String uni = uniform.getId() + "[" + i + "]";
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
                Game.getGame().getLogManager().log("[" + this.getShaderGroup().getId() + "] Linked UBO " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            } else {
                Game.getGame().getLogManager().error("[" + this.getShaderGroup().getId() + "] Couldn't link " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            }
            this.uniformBufferProgramMap.put(uniformBufferObject, uniformBufferProgram);
        }
    }

    public class ShaderUtils {
        public ShaderUtils() {

        }

        public void disableMsaa() {
            GL30.glDisable(GL30.GL_MULTISAMPLE);
        }

        public void enableMsaa() {
            GL30.glEnable(GL30.GL_MULTISAMPLE);
        }

        public void performConstraintsOnShader(ModelRenderConstraints modelRenderConstraints) {
            int lighting_code = 0;
            if (modelRenderConstraints.isLightOpaque()) {
                lighting_code |= 1 << 2;
            }
            ShaderManager.this.performUniform("lighting_code", lighting_code);
        }

        public void performModelMaterialOnShader(Material material, boolean passShadows) {
            Scene scene = Game.getGame().getScreen().getScene();
            if (material == null) {
                return;
            }
            ShaderManager.this.performUniform("show_cascades", scene.getSceneRender().getCurrentDebugMode() == 1 ? 1 : 0);
            ShaderManager.this.performUniform("camera_pos", Game.getGame().getScreen().getScene().getCurrentCamera().getCamPosition());
            ISample diffuse = material.getDiffuse();
            IImageSample emissive = material.getEmissive();
            IImageSample metallic = material.getMetallic();
            IImageSample normals = material.getNormals();
            IImageSample specular = material.getSpecular();
            CubeMapProgram cubeMapProgram = material.getAmbientCubeMap();

            int texturing_code = 0;
            for (int i = 0; i < 12; i++) {
                Scene.activeGlTexture(i);
                GL30.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            }

            if (diffuse != null) {
                if (diffuse instanceof IImageSample) {
                    final int code = 0;
                    IImageSample imageSample = ((IImageSample) diffuse);
                    Scene.activeGlTexture(code);
                    imageSample.bindTexture();
                    ShaderManager.this.performUniform("diffuse_map", code);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof ColorSample) {
                        ShaderManager.this.performUniform("diffuse_color", ((ColorSample) diffuse).getColor());
                    }
                }
            }
            if (emissive != null) {
                final int code = 1;
                Scene.activeGlTexture(code);
                emissive.bindTexture();
                ShaderManager.this.performUniform("emissive_map", code);
                texturing_code |= 1 << 3;
            }
            if (metallic != null) {
                final int code = 2;
                Scene.activeGlTexture(code);
                metallic.bindTexture();
                ShaderManager.this.performUniform("metallic_map", code);
                texturing_code |= 1 << 4;
            }
            if (normals != null) {
                final int code = 3;
                Scene.activeGlTexture(code);
                normals.bindTexture();
                ShaderManager.this.performUniform("normals_map", code);
                texturing_code |= 1 << 5;
            }
            if (specular != null) {
                final int code = 4;
                Scene.activeGlTexture(code);
                specular.bindTexture();
                ShaderManager.this.performUniform("specular_map", code);
                texturing_code |= 1 << 6;
            }
            if (cubeMapProgram != null) {
                final int code = 5;
                Scene.activeGlTexture(code);
                cubeMapProgram.bindCubeMap();
                ShaderManager.this.performUniform("ambient_cubemap", code);
            }
            if (passShadows) {
                for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
                    int startCode = 6;
                    CascadeShadow cascadeShadow = scene.getSceneRender().getShadowScene().getCascadeShadows().get(i);
                    Scene.activeGlTexture(startCode + i);
                    scene.getSceneRender().getShadowScene().getFrameBufferObjectProgram().bindTexture(i);
                    ShaderManager.this.performUniform("shadow_map" + i, startCode + i);
                    ShaderManager.this.performUniform("cascade_shadow", ".split_distance", i, cascadeShadow.getSplitDistance());
                    ShaderManager.this.performUniform("cascade_shadow", ".projection_view", i, cascadeShadow.getLightProjectionViewMatrix());
                }
                for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
                    PointLightShadow pointLightShadow = scene.getSceneRender().getShadowScene().getPointLightShadows().get(i);
                    final int code = 9;
                    Scene.activeGlTexture(code + i);
                    pointLightShadow.getPointLightCubeMap().bindCubeMap();
                    ShaderManager.this.performUniform("far_plane", pointLightShadow.farPlane());
                    ShaderManager.this.performUniform("point_light_cubemap", i, code + i);
                }
            }

            ShaderManager.this.performUniform("texturing_code", texturing_code);
        }

        public void passViewAndModelMatrices(Matrix4d viewMatrix, Model<Format3D> model) {
            if (ShaderManager.this.checkUniformInGroup("model_matrix")) {
                this.performModelMatrix3d(model);
            }
            if (ShaderManager.this.checkUniformInGroup("view_matrix")) {
                this.performViewMatrix3d(viewMatrix);
            }
            if (ShaderManager.this.checkUniformInGroup("model_view_matrix")) {
                this.performModelViewMatrix3d(model, viewMatrix);
            }
        }

        public void passViewAndModelMatrices(Model<Format3D> model) {
            this.passViewAndModelMatrices(RenderManager.instance.getViewMatrix(), model);
        }

        public void performProjectionMatrix() {
            ShaderManager.this.performUniform("projection_matrix", RenderManager.instance.getProjectionMatrix());
        }

        public void performProjectionMatrix2d(Model<Format2D> model) {
            ShaderManager.this.performUniform("projection_model_matrix", RenderManager.instance.getOrthographicScreenModelMatrix(model));
        }

        public void performModelViewMatrix3d(Model<Format3D> model) {
            this.performModelViewMatrix3d(model, RenderManager.instance.getViewMatrix());
        }

        public void performModelViewMatrix3d(Model<Format3D> model, Matrix4d view) {
            ShaderManager.this.performUniform("model_view_matrix", RenderManager.instance.getModelViewMatrix(model, view));
        }

        public void performModelViewMatrix3d(Matrix4d matrix4d) {
            ShaderManager.this.performUniform("model_view_matrix", matrix4d);
        }

        public void performViewMatrix3d(Matrix4d matrix4d) {
            ShaderManager.this.performUniform("view_matrix", matrix4d);
        }

        public void performModelMatrix3d(Model<Format3D> model) {
            this.performModelMatrix3d(model, false);
        }

        public void performModelMatrix3d(Model<Format3D> model, boolean invertedModel) {
            ShaderManager.this.performUniform("model_matrix", RenderManager.instance.getModelMatrix(model, invertedModel));
        }

        public void setCubeMapTexture(CubeMapProgram cubeMapTexture) {
            this.setCubeMapTexture(0, cubeMapTexture);
        }

        public void setCubeMapTexture(int code, CubeMapProgram cubeMapTexture) {
            if (cubeMapTexture == null) {
                Game.getGame().getLogManager().warn("CubeMap is NULL!");
                return;
            }
            ShaderManager.this.performUniform("cube_map_sampler", code);
            GL30.glActiveTexture(GL30.GL_TEXTURE0 + code);
            GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, cubeMapTexture.getTextureId());
        }
    }
}
