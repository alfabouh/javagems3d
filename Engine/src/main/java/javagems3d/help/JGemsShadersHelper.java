package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.JGemsScene;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;

public abstract class JGemsShadersHelper {
    public static void performRenderDataOnShader(JGemsShaderManager shaderManager, RenderAttributes objectRenderingConfiguration) {
        if (!shaderManager.isUniformExist(new UniformString("lighting_code"))) {
            return;
        }
        int lighting_code = 0;
        if (objectRenderingConfiguration.isDefaultBrightLighted()) {
            lighting_code |= 1 << 2;
        }
        shaderManager.performUniform(new UniformString("lighting_code"), UniformFunctions.INTEGER(lighting_code));
    }

    public static void performModelMaterialOnShader(JGemsShaderManager shaderManager, Material material) {
        if (material == null) {
            return;
        }

        ISample diffuse = material.getDiffuse();
        ImageBasedTexture emission = material.getEmissionMap();
        ImageBasedTexture metallic = material.getMetallicMap();
        ImageBasedTexture normals = material.getNormalsMap();
        ImageBasedTexture specular = material.getSpecularMap();
        ITextureProgram cubeMapProgram = JGemsEnvironmentHelper.getWorldEnvironment().getSkyBox().getTexture();

        int texturing_code = 0;

        shaderManager.disableWarns();
        if (shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
            shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
        }

        if (diffuse != null) {
            if (diffuse instanceof ImageBasedTexture) {
                shaderManager.performUniformSample(new UniformString("diffuse_map"), diffuse);
                texturing_code |= 1 << 2;
            } else {
                if (diffuse instanceof RGBAColor) {
                    shaderManager.performUniformSample(new UniformString("diffuse_color"), diffuse);
                }
            }
        }

        if (emission != null) {
            shaderManager.performUniformSample(new UniformString("emissive_map"), emission);
            texturing_code |= 1 << 3;
        }

        if (metallic != null) {
            shaderManager.performUniformSample(new UniformString("metallic_map"), metallic);
            texturing_code |= 1 << 4;
        }

        if (normals != null) {
            shaderManager.performUniformSample(new UniformString("normals_map"), normals);
            texturing_code |= 1 << 5;
        }

        if (specular != null) {
            shaderManager.performUniformSample(new UniformString("specular_map"), specular);
            texturing_code |= 1 << 6;
        }

        shaderManager.performUniform(new UniformString("texturing_code"), UniformFunctions.INTEGER(texturing_code));
        shaderManager.enableWarns();
    }

    public static boolean performAnimationsInfo(JGemsShaderManager shaderManager, IAnimated animated) {
        shaderManager.disableWarns();
        if (animated.hasAnimationData()) {
            shaderManager.performUniformTexture(new UniformString("animationsMatrix"), JGemsResourceManager.getAnimationsTextureBuffer());
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffset"), UniformFunctions.INTEGER(animated.getAnimationData().getCurrentAnimationFrame().getOffset()));
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffsetPrev"), UniformFunctions.INTEGER(animated.getAnimationData().getPreviousAnimationFrame().getOffset()));
            shaderManager.performUniform(new UniformString("animationData.deltaFrame"), UniformFunctions.FLOAT(animated.getAnimationData().getAnimationFrameDelta()));
        }
        shaderManager.enableWarns();
        return false;
    }

    public static void performShadowsInfo(JGemsShaderManager shaderManager) {
        shaderManager.disableWarns();
        JGemsScene scene = JGems3D.get().getScreen().getScene();
        SceneWorld sceneWorld = (SceneWorld) scene.getSceneRenderer().getWorld();
        for (int i = 0; i < JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES; i++) {
            SunLightShadow.Cascade cascade = sceneWorld.getEnvironment().getShadowScene().getSunLightShadow().getCascades().get(i);
            if (shaderManager.isUniformExist(new UniformString("sun_shadow_map", i))) {
                shaderManager.performUniformTexture(new UniformString("sun_shadow_map", i), sceneWorld.getEnvironment().getShadowScene().getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
                shaderManager.performUniform(new UniformString("cascade_shadow", ".split_distance", i), UniformFunctions.FLOAT(cascade.getSplitDistance()));
                shaderManager.performUniform(new UniformString("cascade_shadow", ".projection_view", i), UniformFunctions.MAT4F(cascade.getLightProjectionViewMatrix()));
                shaderManager.performUniform(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
                shaderManager.performUniform(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_NEGATIVE_EXPONENT));
            }
        }
        for (int i = 0; i < JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = sceneWorld.getEnvironment().getShadowScene().getPointLightShadows().get(i);
            shaderManager.performUniform(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
            if (shaderManager.isUniformExist(new UniformString("point_light_cubemap", i))) {
                shaderManager.performUniformTexture(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
            }
        }
        shaderManager.enableWarns();
    }
}
