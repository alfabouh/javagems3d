package javagems3d.graphics.environment.lights.scene;

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
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

public abstract class LightScene implements ILightScene {
    private final IEnvironment environment;
    private Set<PointLight> pointLights;

    private final ShaderStorageBufferObject sunBuffer;
    private final ShaderStorageBufferObject pointLightsBuffer;

    private boolean bloom;
    private float exposure;
    private float gamma;

    private float ssaoRange;
    private float ssaoBias;
    private float ssaoRadius;

    public LightScene(@NotNull ShaderStorageBufferObject sunBuffer, @NotNull ShaderStorageBufferObject pointLightsBuffer, IEnvironment environment) {
        this.environment = environment;
        this.sunBuffer = sunBuffer;
        this.pointLightsBuffer = pointLightsBuffer;
        this.setBloomEnabled(true);
        this.setHdrExposure(JGemsConfig.SYSTEM.HDR_EXPOSURE_DEFAULT);
        this.setHdrGamma(JGemsConfig.SYSTEM.HDR_GAMMA_DEFAULT);
        this.setSsaoRange(JGemsConfig.SYSTEM.SSAO_RANGE);
        this.setSsaoBias(JGemsConfig.SYSTEM.SSAO_BIAS);
        this.setSsaoRadius(JGemsConfig.SYSTEM.SSAO_RADIUS);
        this.initCollections();
    }

    public static Vector3f passVectorInViewSpace(Vector3f in, Matrix4f view, float w) {
        Vector4f aux = new Vector4f(in, w);
        aux.mul(view);
        return new Vector3f(aux.x, aux.y, aux.z);
    }

    private void initCollections() {
        this.pointLights = SyncManager.createSyncronisedSet(new LinkedHashSet<>(this.getMaxPointLights()));
    }

    public void addLight(Light light) {
        if (light.getLightType().equals(LightType.POINT)) {
            if (this.getPointLights().size() >= this.getMaxPointLights()) {
                throw new JGemsRuntimeException("Reached active point lights limit: " + this.getMaxPointLights());
            }
            this.getPointLights().add((PointLight) light);
        }
    }

    public void removeLight(Light light) {
        light.off();
        if (light.getLightType().equals(LightType.POINT)) {
            this.getPointLights().remove((PointLight) light);
        }
    }

    public float calcAmbientLight() {
        return this.getEnvironment().getSkyBox().getSun().getSunBrightness();
    }

    @Override
    public void updateBuffers(MemoryStack stack, HashMap<PointLight, Integer> lightIntegerHashMap, IWorld world, Matrix4f viewMatrix) {
        this.getPointLights().forEach(e -> e.onUpdateWithEvent(world));
        this.updateSunBuffer(this.getSunBuffer(), stack, viewMatrix);
        this.updatePointLightsBuffer(lightIntegerHashMap, this.getPointLightsBuffer(), stack, viewMatrix);
    }

    public void updateSunBuffer(ShaderStorageBufferObject sunBuffer, MemoryStack stack, Matrix4f viewMatrix) {
        Vector3f angle = LightScene.passVectorInViewSpace(this.getEnvironment().getSkyBox().getSun().getLightPosition(), viewMatrix, 0.0f);
        FloatBuffer buffer = stack.mallocFloat(JGemsConfig.SYSTEM.SUN_LIGHT_BUFFER_PACK_SIZE);
        buffer.put(angle.x);
        buffer.put(angle.y);
        buffer.put(angle.z);
        buffer.put(0f); //_padding0
        buffer.put(this.getEnvironment().getSkyBox().getSun().getLightColor().x);
        buffer.put(this.getEnvironment().getSkyBox().getSun().getLightColor().y);
        buffer.put(this.getEnvironment().getSkyBox().getSun().getLightColor().z);
        buffer.put((JGemsConfig.DEBUG.FULL_BRIGHT || JGemsConfig.DEBUG.WIREFRAME_RENDERING) ? 1.0f : this.calcAmbientLight());
        buffer.put((JGemsConfig.DEBUG.FULL_BRIGHT || JGemsConfig.DEBUG.WIREFRAME_RENDERING) ? 1.0f : this.getEnvironment().getSkyBox().getSun().getSunBrightness());
        buffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(sunBuffer, 0L, buffer);
    }

    public void updatePointLightsBuffer(HashMap<PointLight, Integer> pointLightIdsHashMap, ShaderStorageBufferObject pointLightsBuffer, MemoryStack stack, Matrix4f viewMatrix) {
        if (JGemsConfig.DEBUG.DISABLE_POINT_LIGHTS) {
            this.clearPointLightsBuffer(stack);
            return;
        }
        final List<PointLight> pointLights = this.getPointLights().stream().filter(PointLight::isActive).sorted(Comparator.comparing(e -> e.getBrightness() * -1.0f)).toList();
        final int sizeMainBuffer = JGemsConfig.SYSTEM.POINT_LIGHT_BUFFER_PACK_SIZE * (4);
        ByteBuffer buffer = stack.malloc(sizeMainBuffer);
        ByteBuffer buffer2 = stack.malloc(Integer.BYTES);

        int total = pointLights.size();
        for (PointLight pointLight : pointLights) {
            Vector3f lightViewPos = LightScene.passVectorInViewSpace(pointLight.getLightPosition(), viewMatrix, 1.0f);
            buffer.putFloat(pointLight.getLightPosition().x);
            buffer.putFloat(pointLight.getLightPosition().y);
            buffer.putFloat(pointLight.getLightPosition().z);
            buffer.putFloat(0.0f); //_padding0

            buffer.putFloat(lightViewPos.x);
            buffer.putFloat(lightViewPos.y);
            buffer.putFloat(lightViewPos.z);
            buffer.putFloat(0.0f); //_padding00;

            buffer.putFloat(pointLight.getLightColor().x);
            buffer.putFloat(pointLight.getLightColor().y);
            buffer.putFloat(pointLight.getLightColor().z);
            buffer.putFloat(pointLight.getBrightness());

            buffer.putInt(pointLightIdsHashMap.getOrDefault(pointLight, -1));
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

        for (int i = 0; i < this.getPointLights().size(); i++) {
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

    public float getSsaoRange() {
        return this.ssaoRange;
    }

    public void setSsaoRange(float ssaoRange) {
        this.ssaoRange = ssaoRange;
    }

    public float getSsaoBias() {
        return this.ssaoBias;
    }

    public void setSsaoBias(float ssaoBias) {
        this.ssaoBias = ssaoBias;
    }

    public float getSsaoRadius() {
        return this.ssaoRadius;
    }

    public void setSsaoRadius(float ssaoRadius) {
        this.ssaoRadius = ssaoRadius;
    }

    public boolean isBloomEnabled() {
        return this.bloom;
    }

    public void setBloomEnabled(boolean bloom) {
        this.bloom = bloom;
    }

    public float getHdrExposure() {
        return this.exposure;
    }

    public void setHdrExposure(float exposure) {
        this.exposure = exposure;
    }

    public float getHdrGamma() {
        return this.gamma;
    }

    public void setHdrGamma(float gamma) {
        this.gamma = gamma;
    }

    public boolean containsPointLight(PointLight pointLight) {
        return this.getPointLights().contains(pointLight);
    }

    public abstract int getMaxPointLights();

    public Set<PointLight> getPointLights() {
        return this.pointLights;
    }

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