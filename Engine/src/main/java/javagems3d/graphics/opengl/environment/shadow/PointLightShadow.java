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

package javagems3d.graphics.opengl.environment.shadow;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import javagems3d.graphics.opengl.environment.light.PointLight;
import javagems3d.graphics.opengl.rendering.programs.fbo.FBOCubeMapProgram;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.graphics.transformation.Transformation;

import java.util.ArrayList;
import java.util.List;

public class PointLightShadow {
    private final FBOCubeMapProgram pointLightCubeMap;
    private final int id;
    private PointLight pointLight;
    private List<Matrix4f> shadowDirections;

    public PointLightShadow(int id) {
        this.id = id;
        this.shadowDirections = new ArrayList<>();
        this.pointLight = null;
        this.pointLightCubeMap = new FBOCubeMapProgram();
    }

    public void createFBO(Vector2i dim) {
        this.pointLightCubeMap.clearFBO();
        this.pointLightCubeMap.createFrameBufferCubeMapColor(new Vector2i(dim), true, GL30.GL_RG32F, GL30.GL_RG, GL30.GL_LINEAR, GL30.GL_CLAMP_TO_EDGE);
        //this.pointLightCubeMap.createFrameBufferCubeMapDepth(news Vector2i(dim), GL30.GL_NEAREST, GL30.GL_CLAMP_TO_EDGE);
    }

    public void configureMatrices() {
        this.shadowDirections = Transformation.getAllDirectionViewSpaces(this.getPointLight().getLightPos(), this.nearPlane(), this.farPlane());
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

    public List<Matrix4f> getShadowDirections() {
        return this.shadowDirections;
    }

    public FBOCubeMapProgram getPointLightCubeMap() {
        return this.pointLightCubeMap;
    }
}
