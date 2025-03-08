package javagems3d.graphics.environment.lights.scene;

import api.events.EventBus;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.LightType;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import api.events.EventLauncher;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LightScene implements ILightScene {
    private final IEnvironment environment;
    private List<PointLight> pointLightList;

    private final ShaderStorageBufferObject sunBuffer;
    private final ShaderStorageBufferObject pointLightsBuffer;

    public LightScene(@NotNull ShaderStorageBufferObject sunBuffer, @NotNull ShaderStorageBufferObject pointLightsBuffer, IEnvironment environment) {
        this.environment = environment;
        this.sunBuffer = sunBuffer;
        this.pointLightsBuffer = pointLightsBuffer;
        this.initCollections();
    }

    public static Vector3f passVectorInViewSpace(Vector3f in, Matrix4f view, float w) {
        Vector4f aux = new Vector4f(in, w);
        aux.mul(view);
        return new Vector3f(aux.x, aux.y, aux.z);
    }

    private void initCollections() {
        this.pointLightList = SyncManager.createSyncronisedList(new ArrayList<>(this.getMaxPointLights()));
    }

    public void addLight(Light light) {
        if (light.getLightType().equals(LightType.POINT)) {
            if (this.getPointLightList().stream().filter(PointLight::isActive).count() >= this.getMaxPointLights()) {
                throw new JGemsRuntimeException("Reached active point lights limit: " + this.getMaxPointLights());
            }
            this.getPointLightList().add((PointLight) light);
        }
        EventLauncher.pushEvent(new EventBus.LightAdded(light));
    }

    public void removeLight(Light light) {
        light.off();
    }

    public float calcAmbientLight() {
        return this.getEnvironment().getSkyBox().getSun().getSunBrightness();
    }

    public List<PointLight> getPointLightList() {
        return this.pointLightList;
    }

    @Override
    public void updateBuffers(MemoryStack stack, IWorld world, Matrix4f viewMatrix) {
        this.getPointLightList().forEach(e -> e.onUpdate(world));
        this.updateSunBuffer(this.getSunBuffer(), stack, viewMatrix);
        this.updatePointLightsBuffer(this.getPointLightsBuffer(), stack, viewMatrix);
    }

    public void updateSunBuffer(ShaderStorageBufferObject sunBuffer, MemoryStack stack, Matrix4f viewMatrix) {
        Vector3f angle = LightScene.passVectorInViewSpace(this.getEnvironment().getSkyBox().getSun().getSunPosition(), viewMatrix, 0.0f);
        FloatBuffer buffer = stack.mallocFloat(JGemsConfig.SYSTEM.SUN_LIGHT_BUFFER_PACK_SIZE);
        buffer.put(angle.x);
        buffer.put(angle.y);
        buffer.put(angle.z);
        buffer.put(0f); //_padding0
        buffer.put(this.getEnvironment().getSkyBox().getSun().getSunColor().x);
        buffer.put(this.getEnvironment().getSkyBox().getSun().getSunColor().y);
        buffer.put(this.getEnvironment().getSkyBox().getSun().getSunColor().z);
        buffer.put(this.calcAmbientLight());
        buffer.put(this.getEnvironment().getSkyBox().getSun().getSunBrightness());
        buffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(sunBuffer, 0L, buffer);
    }

    public void updatePointLightsBuffer(ShaderStorageBufferObject pointLightsBuffer, MemoryStack stack, Matrix4f viewMatrix) {
        List<PointLight> pointLights = this.getPointLightList().stream().filter(PointLight::isActive).sorted(Comparator.comparingDouble(e -> e.getBrightness() * -1.0f)).collect(Collectors.toList());
        final int sizeMainBuffer = JGemsConfig.SYSTEM.POINT_LIGHT_BUFFER_PACK_SIZE * (4);
        ByteBuffer buffer = stack.malloc(sizeMainBuffer);
        ByteBuffer buffer2 = stack.malloc(Integer.BYTES);

        int total = pointLights.size();
        for (PointLight pointLight : pointLights) {
            Vector3f lightViewPos = LightScene.passVectorInViewSpace(pointLight.getLightPos(), viewMatrix, 1.0f);
            buffer.putFloat(pointLight.getLightPos().x);
            buffer.putFloat(pointLight.getLightPos().y);
            buffer.putFloat(pointLight.getLightPos().z);
            buffer.putFloat(0.0f); //_padding0

            buffer.putFloat(lightViewPos.x);
            buffer.putFloat(lightViewPos.y);
            buffer.putFloat(lightViewPos.z);
            buffer.putFloat(0.0f); //_padding00;

            buffer.putFloat(pointLight.getLightColor().x);
            buffer.putFloat(pointLight.getLightColor().y);
            buffer.putFloat(pointLight.getLightColor().z);
            buffer.putFloat(pointLight.getBrightness());

            buffer.putInt(pointLight.getAttachedShadowSceneId());
            buffer.putInt(0); //_padding000;
            buffer.putInt(0); //_padding0000;
            buffer.putInt(0); //_padding0000;
        }
        buffer.flip();

        buffer2.putInt(total);
        buffer2.flip();

        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, 0L, buffer);
        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, sizeMainBuffer, buffer2);
    }

    public void clearPointLightsBuffer(MemoryStack stack) {
        final int sizeMainBuffer = JGemsConfig.SYSTEM.POINT_LIGHT_BUFFER_PACK_SIZE * (4);
        ByteBuffer buffer = stack.malloc(sizeMainBuffer);
        ByteBuffer buffer2 = stack.malloc(Integer.BYTES);

        for (int i = 0; i < this.getPointLightList().size(); i++) {
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f); //_padding0

            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f); //_padding00;

            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);

            buffer.putInt(-1);
            buffer.putInt(0); //_padding000;
            buffer.putInt(0); //_padding0000;
            buffer.putInt(0); //_padding0000;
        }
        buffer.flip();

        buffer2.putInt(0);
        buffer2.flip();

        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, 0L, buffer);
        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, sizeMainBuffer, buffer2);
    }

    public abstract int getMaxPointLights();

    public IEnvironment getEnvironment() {
        return this.environment;
    }

    public ShaderStorageBufferObject getSunBuffer() {
        return this.sunBuffer;
    }

    public ShaderStorageBufferObject getPointLightsBuffer() {
        return this.pointLightsBuffer;
    }
}