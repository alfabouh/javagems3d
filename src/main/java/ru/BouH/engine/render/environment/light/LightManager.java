package ru.BouH.engine.render.environment.light;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LightManager implements ILightManager {
    public static final int MAX_POINT_LIGHTS = 128;
    private final Environment environment;
    private List<PointLight> pointLightList;

    public LightManager(Environment environment) {
        this.environment = environment;
        this.initCollections();
    }

    public static Vector3d passVectorInViewSpace(Vector3d in, Matrix4d view, double w) {
        Vector4d aux = new Vector4d(in, w);
        aux.mul(view);
        return new Vector3d(aux.x, aux.y, aux.z);
    }

    public static Vector3f passVectorInViewSpace(Vector3f in, Matrix4d view, float w) {
        Vector4d aux = new Vector4d(in, w);
        aux.mul(view);
        return new Vector3f((float) aux.x, (float) aux.y, (float) aux.z);
    }

    private void initCollections() {
        this.pointLightList = new ArrayList<>(LightManager.MAX_POINT_LIGHTS);
    }

    public void addLight(Light light) {
        if ((light.lightCode() & Light.POINT_LIGHT) != 0) {
            if (this.getPointLightList().size() >= LightManager.MAX_POINT_LIGHTS) {
                throw new GameException("Reached point lights limit: " + LightManager.MAX_POINT_LIGHTS);
            }
            this.getPointLightList().add((PointLight) light);
        }
    }

    public void removeLight(Light light) {
        light.stop();
    }

    public float calcAmbientLight() {
        return Math.max(0.5f * this.environment.getSky().getSunBrightness(), 1.5e-2f);
    }

    public List<PointLight> getPointLightList() {
        return this.pointLightList;
    }

    @Override
    public void updateBuffers(SceneWorld sceneWorld, Matrix4d viewMatrix) {
        this.getPointLightList().forEach(e -> e.onUpdate(sceneWorld));
        this.updateSunUbo(viewMatrix);
        this.updatePointLightsUbo();
    }

    private void updateSunUbo(Matrix4d viewMatrix) {
        Vector3f angle = LightManager.passVectorInViewSpace(this.environment.getSky().getSunAngle(), viewMatrix, 0.0f);
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
        Scene.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.SunLight, value1Buffer);
        MemoryUtil.memFree(value1Buffer);
    }

    private void updatePointLightsUbo() {
        this.getPointLightList().sort(Comparator.comparingDouble(e -> e.getBrightness() * -1));
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(8 * LightManager.MAX_POINT_LIGHTS);
        for (int i = 0; i < this.getPointLightList().size(); i++) {
            PointLight pointLight = this.getPointLightList().get(i);
            value1Buffer.put((float) pointLight.getLightPos().x);
            value1Buffer.put((float) pointLight.getLightPos().y);
            value1Buffer.put((float) pointLight.getLightPos().z);
            value1Buffer.put((float) pointLight.getLightColor().x);
            value1Buffer.put((float) pointLight.getLightColor().y);
            value1Buffer.put((float) pointLight.getLightColor().z);
            value1Buffer.put(pointLight.getBrightness());
            value1Buffer.put(pointLight.getAttachedShadowSceneId());
            value1Buffer.flip();
            Scene.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.PointLights, i * 32, value1Buffer);
        }
        MemoryUtil.memFree(value1Buffer);
    }
}