package ru.jgems3d.engine.graphics.opengl.environment.light;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.synchronizing.SyncManager;
import ru.jgems3d.engine.system.exceptions.JGemsException;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LightManager implements ILightManager {
    public static final int MAX_POINT_LIGHTS = 128;
    private final Environment environment;
    private List<PointLight> pointLightList;

    public LightManager(Environment environment) {
        this.environment = environment;
        this.initCollections();
    }

    public static Vector3f passVectorInViewSpace(Vector3f in, Matrix4f view, float w) {
        Vector4f aux = new Vector4f(in, w);
        aux.mul(view);
        return new Vector3f(aux.x, aux.y, aux.z);
    }

    private void initCollections() {
        this.pointLightList = SyncManager.createSyncronisedList(new ArrayList<>(LightManager.MAX_POINT_LIGHTS));
    }

    public void addLight(Light light) {
        if ((light.lightCode() & Light.POINT_LIGHT) != 0) {
            if (this.getPointLightList().stream().filter(PointLight::isEnabled).count() >= LightManager.MAX_POINT_LIGHTS) {
                throw new JGemsException("Reached active point lights limit: " + LightManager.MAX_POINT_LIGHTS);
            }
            this.getPointLightList().add((PointLight) light);
        }
    }

    public void removeLight(Light light) {
        light.stop();
    }

    public float calcAmbientLight() {
        return this.environment.getSky().getSunBrightness();
    }

    public List<PointLight> getPointLightList() {
        return this.pointLightList;
    }

    @Override
    public void updateBuffers(SceneWorld sceneWorld, Matrix4f viewMatrix) {
        this.getPointLightList().forEach(e -> e.onUpdate(sceneWorld));
        this.updateSunUbo(viewMatrix);
        this.updatePointLightsUbo();
    }

    private void updateSunUbo(Matrix4f viewMatrix) {
        JGemsOpenGLRenderer.getGameUboShader().bind();
        Vector3f angle = LightManager.passVectorInViewSpace(this.environment.getSky().getSunPos(), viewMatrix, 0.0f);
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(8);
        value1Buffer.put(this.calcAmbientLight());
        value1Buffer.put(this.environment.getSky().getSunBrightness());
        value1Buffer.put(angle.x);
        value1Buffer.put(angle.y);
        value1Buffer.put(angle.z);
        value1Buffer.put(this.environment.getSky().getSunColors().x);
        value1Buffer.put(this.environment.getSky().getSunColors().y);
        value1Buffer.put(this.environment.getSky().getSunColors().z);
        value1Buffer.flip();
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.SunLight, value1Buffer);
        MemoryUtil.memFree(value1Buffer);
        JGemsOpenGLRenderer.getGameUboShader().unBind();
    }

    private void updatePointLightsUbo() {
        JGemsOpenGLRenderer.getGameUboShader().bind();
        List<PointLight> pointLights = this.getPointLightList().stream().filter(PointLight::isEnabled).sorted(Comparator.comparingDouble(e -> e.getBrightness() * -1)).collect(Collectors.toList());

        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(8 * LightManager.MAX_POINT_LIGHTS);
        int total = pointLights.size();
        for (int i = 0; i < total; i++) {
            PointLight pointLight = pointLights.get(i);
            value1Buffer.put(pointLight.getLightPos().x);
            value1Buffer.put(pointLight.getLightPos().y);
            value1Buffer.put(pointLight.getLightPos().z);
            value1Buffer.put(pointLight.getLightColor().x);
            value1Buffer.put(pointLight.getLightColor().y);
            value1Buffer.put(pointLight.getLightColor().z);
            value1Buffer.put(pointLight.getBrightness());
            value1Buffer.put(pointLight.getAttachedShadowSceneId());
            value1Buffer.flip();
            JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.PointLights, i * (8 * 4), value1Buffer);
        }
        MemoryUtil.memFree(value1Buffer);

        IntBuffer intBuffer = MemoryUtil.memAllocInt(1);
        intBuffer.put(total);
        intBuffer.flip();
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.PointLights, LightManager.MAX_POINT_LIGHTS * (8 * 4), intBuffer);
        MemoryUtil.memFree(intBuffer);
        JGemsOpenGLRenderer.getGameUboShader().unBind();
    }

    public void removeAllLights() {
        JGemsOpenGLRenderer.getGameUboShader().bind();
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(8 * LightManager.MAX_POINT_LIGHTS);
        for (int i = 0; i < this.getPointLightList().size(); i++) {
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(-1);
            value1Buffer.put(-1);
            value1Buffer.flip();
            JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.PointLights, i * 32, value1Buffer);
        }
        MemoryUtil.memFree(value1Buffer);
        JGemsOpenGLRenderer.getGameUboShader().unBind();
    }
}