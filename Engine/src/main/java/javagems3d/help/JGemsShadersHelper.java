package javagems3d.help;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.managing.JGemsResourceManager;

public abstract class JGemsShadersHelper {
    public static void performRenderDataOnShader(JGemsShaderManager shaderManager, RenderAttributes objectRenderingConfiguration) {
        if (shaderManager.isUniformExist(new UniformString("lighting_code"))) {
            shaderManager.performUniform(new UniformString("lighting_code"), UniformFunctions.INTEGER(JGemsRenderingHelper.getLightingCodeForShader(objectRenderingConfiguration)));
        }
    }

    public static void performModelMaterialOnShader(IEnvironment environment, JGemsShaderManager shaderManager, Material material) {
        if (material == null) {
            return;
        }

        ISample diffuse = material.getDiffuse();
        ITexture2DProgram emission = material.getEmission();
        ITexture2DProgram metallic = material.getMetallicMap();
        ITexture2DProgram normals = material.getNormalsMap();
        ITexture2DProgram specular = material.getSpecularMap();
        ICubeMapProgram cubeMapProgram = environment.getSkyBox().getTexture();

        int texturing_code = 0;

        shaderManager.disableWarns();
        if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
            shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
        }

        if (diffuse != null) {
            if (diffuse instanceof ITexture2DProgram) {
                shaderManager.performUniformSample(new UniformString("diffuse_map"), diffuse);
                texturing_code |= 1 << 2;
            } else {
                if (diffuse instanceof Color4Texture) {
                    shaderManager.performUniformSample(new UniformString("diffuse_color"), diffuse);
                }
            }
        }

        if (emission != null) {
            shaderManager.performUniformSample(new UniformString("emission_map"), emission);
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
        shaderManager.performUniform(new UniformString("animationData.currAnimationOffset"), UniformFunctions.INTEGER(!animated.hasAnimationData() ? -1 : animated.getAnimationData().getCurrentAnimationFrame().getOffset()));
        shaderManager.performUniform(new UniformString("animationData.currAnimationOffsetPrev"), UniformFunctions.INTEGER(!animated.hasAnimationData() ? -1 : animated.getAnimationData().getPreviousAnimationFrame().getOffset()));
        if (animated.hasAnimationData()) {
            shaderManager.performUniformTexture(new UniformString("animationsMatrix"), JGemsResourceManager.getAnimationsTextureBuffer());
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
                shaderManager.performUniformTexture(new UniformString("sun_shadow_map", i), shadowScene.getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
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
                shaderManager.performUniformTexture(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
            }
        }
        shaderManager.enableWarns();
    }
}
