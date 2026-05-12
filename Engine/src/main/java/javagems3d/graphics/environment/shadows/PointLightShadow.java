package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.system.global.JGemsConfig;
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
   // private float farPlane;

    public PointLightShadow(IEnvironment environment, Vector2i shadowMapResolution, int id) {
        super(environment, shadowMapResolution);
        this.id = id;
        this.shadowDirections = new ArrayList<>(6);
        this.pointLight = null;
        this.pointLightCubeMap = new FBOCubeMapProgram();
    }

    public void configureMatrices() {
       // this.farPlane = Math.min(this.getPointLight().getClipRadius() / 2.0f, 64.0f);
        this.shadowDirections = TransformUtils.getAllDirectionViewSpaces(this.getPointLight().getLightPosition(), this.nearPlane(), this.farPlane());
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
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
        return 64.0f;
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
