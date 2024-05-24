package ru.alfabouh.engine.game.resources.assets.shaders;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.game.resources.assets.materials.Material;
import ru.alfabouh.engine.game.resources.assets.materials.textures.ColorSample;
import ru.alfabouh.engine.game.resources.assets.materials.textures.IImageSample;
import ru.alfabouh.engine.game.resources.assets.materials.textures.ISample;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.render.environment.shadow.CascadeShadow;
import ru.alfabouh.engine.render.environment.shadow.PointLightShadow;
import ru.alfabouh.engine.render.environment.shadow.ShadowScene;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.engine.render.scene.programs.CubeMapProgram;
import ru.alfabouh.engine.render.scene.programs.ShaderProgram;
import ru.alfabouh.engine.render.scene.programs.UniformBufferProgram;
import ru.alfabouh.engine.render.scene.programs.UniformProgram;
import ru.alfabouh.engine.render.transformation.TransformationManager;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
    private boolean useForGBuffer;

    public ShaderManager(ShaderGroup shaderGroup) {
        this.shaderGroup = shaderGroup;
        this.shaderUtils = new ShaderUtils();
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

    public ShaderManager copy() {
        return new ShaderManager(this.getShaderGroup());
    }

    public boolean checkUniformInGroup(String uniform) {
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

    public Set<UniformBufferObject> getUniformBufferObjects() {
        return this.uniformBufferObjects;
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createShader(this.getShaderGroup());
        if (shaderProgram.link()) {
            Game.getGame().getLogManager().log("Shader " + this.getShaderGroup().getId() + " successfully linked");
        } else {
            throw new GameException("Found problems in shader " + this.getShaderGroup().getId());
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
                throw new GameException("[" + this.getShaderGroup().getId() + "] Couldn't link " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            }
            this.uniformBufferProgramMap.put(uniformBufferObject, uniformBufferProgram);
        }
    }

    public class ShaderUtils {
        public ShaderUtils() {
        }

        public void performConstraintsOnShader(ModelRenderParams modelRenderParams) {
            if (!ShaderManager.this.checkUniformInGroup("lighting_code")) {
                return;
            }
            int lighting_code = 0;
            if (modelRenderParams.isBright()) {
                lighting_code |= 1 << 2;
            }
            ShaderManager.this.performUniform("lighting_code", lighting_code);
        }

        public void performModelMaterialOnShader(Material material, boolean passShadows) {
            if (material == null) {
                return;
            }

            //ShaderManager.this.performUniformNoWarn("show_cascades", SceneRender.CURRENT_DEBUG_MODE);

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

            this.passCameraInfo();
            this.passCubeMap("ambient_cubemap", cubeMapProgram);
            if (diffuse != null) {
                if (diffuse instanceof IImageSample) {
                    final int code = 0;
                    IImageSample imageSample = ((IImageSample) diffuse);
                    Scene.activeGlTexture(code);
                    imageSample.bindTexture();
                    ShaderManager.this.performUniformNoWarn("diffuse_map", code);
                    texturing_code |= 1 << 2;
                } else {
                    if (diffuse instanceof ColorSample) {
                        ShaderManager.this.performUniformNoWarn("diffuse_color", ((ColorSample) diffuse).getColor());
                    }
                }
            }
            if (emissive != null) {
                final int code = 1;
                Scene.activeGlTexture(code);
                emissive.bindTexture();
                ShaderManager.this.performUniformNoWarn("emissive_map", code);
                texturing_code |= 1 << 3;
            }
            if (metallic != null) {
                final int code = 2;
                Scene.activeGlTexture(code);
                metallic.bindTexture();
                ShaderManager.this.performUniformNoWarn("metallic_map", code);
                texturing_code |= 1 << 4;
            }
            if (normals != null) {
                final int code = 3;
                Scene.activeGlTexture(code);
                normals.bindTexture();
                ShaderManager.this.performUniformNoWarn("normals_map", code);
                texturing_code |= 1 << 5;
            }
            if (specular != null) {
                final int code = 4;
                Scene.activeGlTexture(code);
                specular.bindTexture();
                ShaderManager.this.performUniformNoWarn("specular_map", code);
                texturing_code |= 1 << 6;
            }
            if (passShadows) {
                this.passShadowsInfo();
            }

            ShaderManager.this.performUniformNoWarn("texturing_code", texturing_code);
        }

        public void passCameraInfo() {
            ShaderManager.this.performUniformNoWarn("camera_pos", Game.getGame().getScreen().getScene().getCurrentCamera().getCamPosition());
        }

        public void passCubeMap(String name, CubeMapProgram cubeMapProgram) {
            if (cubeMapProgram != null) {
                final int code = 5;
                Scene.activeGlTexture(code);
                cubeMapProgram.bindCubeMap();
                ShaderManager.this.performUniformNoWarn(name, code);
            }
        }

        public void passShadowsInfo() {
            Scene scene = Game.getGame().getScreen().getScene();
            for (int i = 0; i < ShadowScene.CASCADE_SPLITS; i++) {
                int startCode = 6;
                CascadeShadow cascadeShadow = scene.getSceneRender().getShadowScene().getCascadeShadows().get(i);
                Scene.activeGlTexture(startCode + i);
                scene.getSceneRender().getShadowScene().getFrameBufferObjectProgram().bindTexture(i);
                ShaderManager.this.performUniformNoWarn("shadow_map" + i, startCode + i);
                ShaderManager.this.performUniformNoWarn("cascade_shadow", ".split_distance", i, cascadeShadow.getSplitDistance());
                ShaderManager.this.performUniformNoWarn("cascade_shadow", ".projection_view", i, cascadeShadow.getLightProjectionViewMatrix());
            }
            for (int i = 0; i < ShadowScene.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = scene.getSceneRender().getShadowScene().getPointLightShadows().get(i);
                final int code = 9 + i;
                Scene.activeGlTexture(code);
                pointLightShadow.getPointLightCubeMap().bindCubeMap();
                ShaderManager.this.performUniformNoWarn("far_plane", pointLightShadow.farPlane());
                ShaderManager.this.performUniformNoWarn("point_light_cubemap", i, code);
            }
        }

        public void passViewAndModelMatrices(Matrix4d viewMatrix, Model<Format3D> model) {
            if (ShaderManager.this.checkUniformInGroup("model_matrix")) {
                if (model.getFormat().isOrientedToViewMatrix()) {
                    this.performToViewOrientedModelMatrix3d(model, viewMatrix, false);
                } else {
                    this.performModelMatrix3d(model);
                }
            }
            if (ShaderManager.this.checkUniformInGroup("view_matrix")) {
                this.performViewMatrix3d(viewMatrix);
            }
            if (ShaderManager.this.checkUniformInGroup("model_view_matrix")) {
                this.performModelViewMatrix3d(model, viewMatrix);
            }
        }

        public void passViewAndModelMatrices(Model<Format3D> model) {
            this.passViewAndModelMatrices(TransformationManager.instance.getMainCameraViewMatrix(), model);
        }

        public void performProjectionMatrix() {
            ShaderManager.this.performUniform("projection_matrix", TransformationManager.instance.getProjectionMatrix());
        }

        public void performProjectionMatrix2d(Model<Format2D> model) {
            ShaderManager.this.performUniform("projection_model_matrix", TransformationManager.instance.getOrthographicScreenModelMatrix(model));
        }

        public void performModelViewMatrix3d(Model<Format3D> model) {
            this.performModelViewMatrix3d(model, TransformationManager.instance.getMainCameraViewMatrix());
        }

        public void performModelViewMatrix3d(Model<Format3D> model, Matrix4d view) {
            ShaderManager.this.performUniform("model_view_matrix", TransformationManager.instance.getModelViewMatrix(model, view));
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
            ShaderManager.this.performUniform("model_matrix", TransformationManager.instance.getModelMatrix(model, invertedModel));
        }

        public void performModelMatrix3d(Matrix4d model) {
            ShaderManager.this.performUniform("model_matrix", model);
        }

        public void performToViewOrientedModelMatrix3d(Model<Format3D> model, Matrix4d view, boolean invertedModel) {
            Matrix4d matrix4d = TransformationManager.instance.getModelMatrix(model, invertedModel);
            view.transpose3x3(matrix4d).scale(model.getFormat().getScale());
            ShaderManager.this.performUniform("model_matrix", matrix4d);
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
