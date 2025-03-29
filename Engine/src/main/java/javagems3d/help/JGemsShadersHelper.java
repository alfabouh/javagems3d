package javagems3d.help;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.ISample;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor3;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import javagems3d.system.resources.managing.JGemsResourceManager;

public abstract class JGemsShadersHelper {
    public static void performModelMaterialOnShader(IEnvironment environment, JGemsShaderManager shaderManager, Material material) {
        if (material == null) {
            return;
        }

        ITexture2DProgram diffuseMap = material.getDiffuseMap();
        ISampleColor4 diffuseColor = material.getDiffuseColor();

        ITexture2DProgram emissionMap = material.getEmissionMap();
        ISampleColor3 emissionColor = material.getEmissionColor();

        ITexture2DProgram metallicRoughnessMap = material.getEmissionMap();
        ITexture2DProgram normalsMap = material.getEmissionMap();

        float metallicFactor = material.getMetallicFactor();
        float roughnessFactor = material.getRoughnessFactor();

        ICubeMapProgram cubeMapProgram = environment.getSkyBox().getTexture();

        shaderManager.disableWarns();
        if (cubeMapProgram != null) {
            if (shaderManager.isUniformExist(new UniformString("ambient_cubemap"))) {
                shaderManager.performUniformTextureBindless(new UniformString("ambient_cubemap"), cubeMapProgram);
            }
            if (shaderManager.isUniformExist(new UniformString("useCubeMap"))) {
                shaderManager.performUniform(new UniformString("useCubeMap"), UniformFunctions.BOOLEAN(true));
            }
        } else {
            if (shaderManager.isUniformExist(new UniformString("useCubeMap"))) {
                shaderManager.performUniform(new UniformString("useCubeMap"), UniformFunctions.BOOLEAN(false));
            }
        }

        shaderManager.performUniformSample(new UniformString("diffuse_color"), diffuseColor);
        shaderManager.performUniformSample(new UniformString("emission_color"), emissionColor);
        shaderManager.performUniform(new UniformString("metallic_factor"), UniformFunctions.FLOAT(metallicFactor));
        shaderManager.performUniform(new UniformString("roughness_factor"), UniformFunctions.FLOAT(roughnessFactor));

        if (diffuseMap != null) {
            shaderManager.performUniformSample(new UniformString("diffuse_map"), diffuseMap);
        }

        if (emissionMap != null) {
            shaderManager.performUniformSample(new UniformString("emissionMap"), emissionMap);
        }

        if (normalsMap != null) {
            shaderManager.performUniformSample(new UniformString("normalsMap"), normalsMap);
        }

        if (metallicRoughnessMap != null) {
            shaderManager.performUniformSample(new UniformString("metallicRoughnessMap"), metallicRoughnessMap);
        }

        shaderManager.performUniform(new UniformString("texturing_code"), UniformFunctions.INTEGER(JGemsRenderingHelper.getTexturingCodeForShader(material)));
        shaderManager.enableWarns();
    }

    public static boolean performAnimationsInfo(JGemsShaderManager shaderManager, IAnimated animated) {
        shaderManager.disableWarns();
        shaderManager.performUniform(new UniformString("animationData.currAnimationOffset"), UniformFunctions.INTEGER(!animated.hasAnimationData() ? -1 : animated.getAnimationData().getCurrentAnimationFrame().getOffset()));
        shaderManager.performUniform(new UniformString("animationData.currAnimationOffsetPrev"), UniformFunctions.INTEGER(!animated.hasAnimationData() ? -1 : animated.getAnimationData().getPreviousAnimationFrame().getOffset()));
        if (animated.hasAnimationData()) {
            shaderManager.performUniformTexture(new UniformString("animations_matrix"), JGemsResourceManager.getAnimationsTextureBuffer());
            shaderManager.performUniform(new UniformString("animationData.deltaFrame"), UniformFunctions.FLOAT(animated.getAnimationData().getAnimationFrameDelta()));
        }
        shaderManager.enableWarns();
        return false;
    }

    public static void performShadowsInfo(IEnvironment environment, JGemsShaderManager shaderManager) {
        shaderManager.disableWarns();
        ShadowScene shadowScene = (ShadowScene) environment.getShadowScene();
        for (int i = 0; i < JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES; i++) {
            SunLightShadow.Cascade cascade = shadowScene.getSunLightShadow().getCascades().get(i);
            if (shaderManager.isUniformExist(new UniformString("sun_shadow_map", i))) {
                shaderManager.performUniformTextureBindless(new UniformString("sun_shadow_map", i), shadowScene.getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
                shaderManager.performUniform(new UniformString("cascade_shadow", ".split_distance", i), UniformFunctions.FLOAT(cascade.getSplitDistance()));
                shaderManager.performUniform(new UniformString("cascade_shadow", ".projection_view", i), UniformFunctions.MAT4F(cascade.getLightProjectionViewMatrix()));
                shaderManager.performUniform(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
                shaderManager.performUniform(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_NEGATIVE_EXPONENT));
            }
        }
        for (int i = 0; i < JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = shadowScene.getPointLightShadows().get(i);
            shaderManager.performUniform(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
            if (shaderManager.isUniformExist(new UniformString("point_light_cubemap", i))) {
                shaderManager.performUniformTextureBindless(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
            }
        }
        shaderManager.enableWarns();
    }
}
