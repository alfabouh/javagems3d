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

package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.JGemsEnvironment;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.rendering.programs.fbo.FBOCubeMapProgram;
import javagems3d.graphics.transformation.TransformUtils;

import java.util.ArrayList;
import java.util.List;

public class PointLightShadow extends Shadow {
    private final FBOCubeMapProgram pointLightCubeMap;
    private final int id;
    private PointLight pointLight;
    private List<Matrix4f> shadowDirections;

    public PointLightShadow(IEnvironment environment, Vector2i shadowMapResolution, int id) {
        super(environment, shadowMapResolution);
        this.id = id;
        this.shadowDirections = new ArrayList<>(6);
        this.pointLight = null;
        this.pointLightCubeMap = new FBOCubeMapProgram();
    }

    public void configureMatrices() {
        this.shadowDirections = TransformUtils.getAllDirectionViewSpaces(this.getPointLight().getLightPos(), this.nearPlane(), this.farPlane());
    }

    public void setPointLight(PointLight pointLight) {
        if (pointLight == null) {
            if (this.getPointLight() != null) {
                this.getPointLight().setAttachedShadowSceneId(-1);
            }
            this.pointLight = null;
        } else {
            this.pointLight = pointLight;
            this.pointLight.setAttachedShadowSceneId(this.getId());
        }
    }

    @Override
    public void createResources() {
        this.getPointLightCubeMap().createFrameBufferCubeMapColor(this.getShadowMapResolution(), true, GL46.GL_RG32F, GL46.GL_RG, GL46.GL_LINEAR, GL46.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void destroyResources() {
        this.getPointLightCubeMap().clearFBO();
    }

    public int getId() {
        return this.id;
    }

    public float farPlane() {
        return 25.0f;
    }

    public float nearPlane() {
        return 0.1f;
    }

    public boolean isAttachedToLight() {
        return this.getPointLight() != null;
    }

    public PointLight getPointLight() {
        return this.pointLight;
    }

    public List<Matrix4f> getShadowDirections() {
        return this.shadowDirections;
    }

    public FBOCubeMapProgram getPointLightCubeMap() {
        return this.pointLightCubeMap;
    }
}
