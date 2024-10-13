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

package javagems3d.graphics.opengl.environment.light;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import api.bridge.events.APIEventsLauncher;
import javagems3d.graphics.opengl.environment.Environment;
import javagems3d.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;
import api.app.events.bus.Events;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LightManager implements ILightManager {
    public static final int PL_STRUCT_SIZE = 16;
    public static final int SN_STRUCT_SIZE = 12;
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
        this.pointLightList = SyncManager.createSyncronisedList(new ArrayList<>(JGemsSceneGlobalConstants.MAX_POINT_LIGHTS));
    }

    public void addLight(Light light) {
        if ((light.lightCode() & Light.POINT_LIGHT) != 0) {
            if (this.getPointLightList().stream().filter(PointLight::isEnabled).count() >= JGemsSceneGlobalConstants.MAX_POINT_LIGHTS) {
                throw new JGemsRuntimeException("Reached active point lights limit: " + JGemsSceneGlobalConstants.MAX_POINT_LIGHTS);
            }
            this.getPointLightList().add((PointLight) light);
        }
        APIEventsLauncher.pushEvent(new Events.LightAdded(light));
    }

    public void removeLight(Light light) {
        light.stop();
    }

    public float calcAmbientLight() {
        return this.environment.getSkyBox().getSun().getSunBrightness();
    }

    public List<PointLight> getPointLightList() {
        return this.pointLightList;
    }

    @Override
    public void updateBuffers(MemoryStack stack, SceneWorld sceneWorld, Matrix4f viewMatrix) {
        this.getPointLightList().forEach(e -> e.onUpdate(sceneWorld));
        this.updateSunUbo(stack, viewMatrix);
        this.updatePointLightsUbo(stack, viewMatrix);
    }

    private void updateSunUbo(MemoryStack stack, Matrix4f viewMatrix) {
        Vector3f angle = LightManager.passVectorInViewSpace(this.environment.getSkyBox().getSun().getSunPosition(), viewMatrix, 0.0f);
        FloatBuffer value1Buffer = stack.mallocFloat(LightManager.SN_STRUCT_SIZE * Float.BYTES);

        value1Buffer.put(angle.x);
        value1Buffer.put(angle.y);
        value1Buffer.put(angle.z);
        value1Buffer.put(0.0f);

        value1Buffer.put(this.environment.getSkyBox().getSun().getSunColor().x);
        value1Buffer.put(this.environment.getSkyBox().getSun().getSunColor().y);
        value1Buffer.put(this.environment.getSkyBox().getSun().getSunColor().z);
        value1Buffer.put(0.0f);

        value1Buffer.put(this.calcAmbientLight());
        value1Buffer.put(this.environment.getSkyBox().getSun().getSunBrightness());
        value1Buffer.put(0.0f);
        value1Buffer.put(0.0f);

        value1Buffer.flip();
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.SunLight, value1Buffer);
    }

    private void updatePointLightsUbo(MemoryStack stack, Matrix4f viewMatrix) {
        List<PointLight> pointLights = this.getPointLightList().stream().filter(PointLight::isEnabled).sorted(Comparator.comparingDouble(e -> e.getBrightness() * -1)).collect(Collectors.toList());

        FloatBuffer value1Buffer = stack.mallocFloat(LightManager.PL_STRUCT_SIZE * JGemsSceneGlobalConstants.MAX_POINT_LIGHTS);
        int total = pointLights.size();
        for (int i = 0; i < total; i++) {
            PointLight pointLight = pointLights.get(i);
            value1Buffer.put(pointLight.getLightPos().x);
            value1Buffer.put(pointLight.getLightPos().y);
            value1Buffer.put(pointLight.getLightPos().z);
            value1Buffer.put(0.0f);

            Vector3f lightViewPos = LightManager.passVectorInViewSpace(pointLight.getLightPos(), viewMatrix, 1.0f);
            value1Buffer.put(lightViewPos.x);
            value1Buffer.put(lightViewPos.y);
            value1Buffer.put(lightViewPos.z);
            value1Buffer.put(0.0f);

            value1Buffer.put(pointLight.getLightColor().x);
            value1Buffer.put(pointLight.getLightColor().y);
            value1Buffer.put(pointLight.getLightColor().z);
            value1Buffer.put(0.0f);

            value1Buffer.put(pointLight.getBrightness());
            value1Buffer.put(pointLight.getAttachedShadowSceneId());
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.flip();
            JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.PointLights, i * (LightManager.PL_STRUCT_SIZE * Float.BYTES), value1Buffer);
        }

        IntBuffer intBuffer = stack.mallocInt(1);
        intBuffer.put(total);
        intBuffer.flip();
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.PointLights, JGemsSceneGlobalConstants.MAX_POINT_LIGHTS * LightManager.PL_STRUCT_SIZE * Integer.BYTES, intBuffer);
    }

    public void removeAllLights(MemoryStack stack) {
        boolean flag = JGemsOpenGLRenderer.getGameUboShader().bind();
        FloatBuffer value1Buffer = stack.mallocFloat(LightManager.PL_STRUCT_SIZE * JGemsSceneGlobalConstants.MAX_POINT_LIGHTS);
        for (int i = 0; i < this.getPointLightList().size(); i++) {
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);

            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);

            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);

            value1Buffer.put(-1);
            value1Buffer.put(-1);

            value1Buffer.put(0.0f);
            value1Buffer.put(0.0f);
            value1Buffer.flip();
            JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.PointLights, i * (LightManager.PL_STRUCT_SIZE * Float.BYTES), value1Buffer);
        }

        IntBuffer intBuffer = stack.mallocInt(1);
        intBuffer.put(0);
        intBuffer.flip();
        JGemsOpenGLRenderer.getGameUboShader().performUniformBuffer(JGemsResourceManager.globalShaderAssets.PointLights, JGemsSceneGlobalConstants.MAX_POINT_LIGHTS * LightManager.PL_STRUCT_SIZE * Integer.BYTES, intBuffer);
        this.getPointLightList().clear();
        if (flag) {
            JGemsOpenGLRenderer.getGameUboShader().unBind();
        }
    }
}