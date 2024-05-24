package ru.alfabouh.engine.render.environment.shadow;

import org.joml.Matrix4d;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.alfabouh.engine.render.environment.light.PointLight;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.programs.FBOCubeMapProgram;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.transformation.TransformationManager;

import java.util.ArrayList;
import java.util.List;

public class PointLightShadow {
    private final FBOCubeMapProgram pointLightCubeMap;
    private final int id;
    private final SceneWorld sceneWorld;
    private PointLight pointLight;
    private List<Matrix4d> shadowDirections;

    public PointLightShadow(int id, SceneWorld sceneWorld) {
        this.id = id;
        this.sceneWorld = sceneWorld;
        this.shadowDirections = new ArrayList<>();
        this.pointLight = null;
        this.pointLightCubeMap = new FBOCubeMapProgram();
    }

    public void createFBO(Vector2i dim) {
        this.pointLightCubeMap.clearFBO();
        //this.pointLightCubeMap.createFrameBufferCubeMapColor(new Vector2i(dim), true, GL30.GL_RG32F, GL30.GL_RG, GL30.GL_LINEAR, GL30.GL_CLAMP_TO_EDGE);
        this.pointLightCubeMap.createFrameBufferCubeMapDepth(new Vector2i(dim), GL30.GL_NEAREST, GL30.GL_CLAMP_TO_EDGE);
    }

    public void configureMatrices() {
        this.shadowDirections = TransformationManager.instance.getAllDirectionViewSpaces(this.getPointLight().getLightPos(), this.nearPlane(), this.farPlane());
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

    public List<Matrix4d> getShadowDirections() {
        return this.shadowDirections;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public FBOCubeMapProgram getPointLightCubeMap() {
        return this.pointLightCubeMap;
    }
}