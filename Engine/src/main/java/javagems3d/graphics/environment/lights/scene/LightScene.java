package javagems3d.graphics.environment.lights.scene;

import javagems3d.graphics.environment.lights.*;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import javagems3d.system.service.synchronizing.SyncManager;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

public abstract class LightScene implements ILightScene {
    private final IEnvironment environment;
    private Set<PointLight> pointLights;
    private Set<SpotLight> spotLights;
    protected SunLight sunLight;

    private final ShaderStorageBufferObject sunBuffer;
    private final ShaderStorageBufferObject pointLightsBuffer;
    private final ShaderStorageBufferObject spotLightsBuffer;

    private boolean bloom;
    private float exposure;
    private float gamma;

    private float ssaoRange;
    private float ssaoBias;
    private float ssaoRadius;

    public LightScene(@NotNull ShaderStorageBufferObject sunBuffer, @NotNull ShaderStorageBufferObject pointLightsBuffer, @NotNull ShaderStorageBufferObject spotLightsBuffer, IEnvironment environment) {
        this.environment = environment;
        this.sunBuffer = sunBuffer;
        this.pointLightsBuffer = pointLightsBuffer;
        this.spotLightsBuffer = spotLightsBuffer;
        this.setBloomEnabled(true);
        this.setHdrExposure(JGemsConfig.SYSTEM.HDR_EXPOSURE_DEFAULT);
        this.setHdrGamma(JGemsConfig.SYSTEM.HDR_GAMMA_DEFAULT);
        this.setSsaoRange(JGemsConfig.SYSTEM.SSAO_RANGE);
        this.setSsaoBias(JGemsConfig.SYSTEM.SSAO_BIAS);
        this.setSsaoRadius(JGemsConfig.SYSTEM.SSAO_RADIUS);
        this.init();
        this.initSun();
    }

    public static Vector3f passVectorInViewSpace(Vector3f in, Matrix4f view, float w) {
        Vector4f aux = new Vector4f(in, w);
        aux.mul(view);
        return new Vector3f(aux.x, aux.y, aux.z);
    }

    protected void init() {
        this.pointLights = SyncManager.createSyncronisedSet(new LinkedHashSet<>(this.getMaxPointLights()));
        this.spotLights = SyncManager.createSyncronisedSet(new LinkedHashSet<>(this.getMaxSpotLights()));
    }

    protected void initSun() {
        this.sunLight = new SunLight(new Vector3f(1.0f), new Vector3f(1.0f), 1.0f);
    }

    public void addLight(Light light) {
        switch (light.getLightType()) {
            case POINT -> {
                if (this.getPointLights().size() >= this.getMaxPointLights()) {
                    Log.get().error("Reached point lights limit: " + this.getMaxPointLights());
                } else {
                    this.getPointLights().add((PointLight) light);
                }
            }
            case SPOT -> {
                if (this.getSpotLights().size() >= this.getMaxSpotLights()) {
                    Log.get().error("Reached spot lights limit: " + this.getMaxPointLights());
                } else {
                    this.getSpotLights().add((SpotLight) light);
                }
            }
        }
    }

    public void removeLight(Light light) {
        light.off();
        switch (light.getLightType()) {
            case POINT -> {
                this.getPointLights().remove((PointLight) light);
            }
            case SPOT -> {
                this.getSpotLights().remove((SpotLight) light);
            }
        }
    }

    public float calcAmbientLight() {
        return this.getSunLight().getSunBrightness();
    }

    @Override
    public void updateBuffers(MemoryStack stack, HashMap<PointLight, Integer> pointLightIntegerHashMap, HashMap<SpotLight, Integer> spotLightIntegerHashMap, IWorld world, Matrix4f viewMatrix) {
        this.getPointLights().forEach(e -> e.onUpdate(world));
        this.getSpotLights().forEach(e -> e.onUpdate(world));
        this.updateSunBuffer(this.getSunBuffer(), stack, viewMatrix);
        this.updatePointLightsBuffer(pointLightIntegerHashMap, this.getPointLightsBuffer(), stack, viewMatrix);
        this.updateSpotLightsBuffer(spotLightIntegerHashMap, this.getSpotLightsBuffer(), stack, viewMatrix);
    }

    public void updateSunBuffer(ShaderStorageBufferObject sunBuffer, MemoryStack stack, Matrix4f viewMatrix) {
        Vector3f angle = LightScene.passVectorInViewSpace(this.getSunLight().getLightPosition(), viewMatrix, 0.0f);
        FloatBuffer buffer = stack.mallocFloat(JGemsConfig.SYSTEM.SUN_LIGHT_BUFFER_PACK_SIZE);
        buffer.put(angle.x);
        buffer.put(angle.y);
        buffer.put(angle.z);
        buffer.put((JGemsConfig.DEBUG.FULL_BRIGHT || JGemsConfig.DEBUG.WIREFRAME_RENDERING) ? 1.0f : this.getSunLight().getSunBrightness());
        buffer.put(this.getSunLight().getLightColor().x);
        buffer.put(this.getSunLight().getLightColor().y);
        buffer.put(this.getSunLight().getLightColor().z);
        buffer.put((JGemsConfig.DEBUG.FULL_BRIGHT || JGemsConfig.DEBUG.WIREFRAME_RENDERING) ? 1.0f : this.calcAmbientLight());
        buffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(sunBuffer, 0L, buffer);
    }

    public void updatePointLightsBuffer(HashMap<PointLight, Integer> pointLightIdsHashMap, ShaderStorageBufferObject pointLightsBuffer, MemoryStack stack, Matrix4f viewMatrix) {
        if (JGemsConfig.DEBUG.DISABLE_POINT_LIGHTS) {
            this.clearPointLightsBuffer(stack, pointLightsBuffer);
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
            buffer.putFloat(pointLight.getBrightness());

            buffer.putFloat(lightViewPos.x);
            buffer.putFloat(lightViewPos.y);
            buffer.putFloat(lightViewPos.z);
            buffer.putInt(pointLightIdsHashMap.getOrDefault(pointLight, -1));

            buffer.putFloat(pointLight.getLightColor().x);
            buffer.putFloat(pointLight.getLightColor().y);
            buffer.putFloat(pointLight.getLightColor().z);

            buffer.putFloat(pointLight.getClipRadius());
        }
        buffer.flip();

        buffer2.putInt(total);
        buffer2.flip();

        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, 0L, buffer);
        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, sizeMainBuffer, buffer2);
    }

    public void clearPointLightsBuffer(MemoryStack stack, ShaderStorageBufferObject pointLightsBuffer) {
        final int sizeMainBuffer = JGemsConfig.SYSTEM.POINT_LIGHT_BUFFER_PACK_SIZE * (4);
        ByteBuffer buffer = stack.malloc(sizeMainBuffer);
        ByteBuffer buffer2 = stack.malloc(Integer.BYTES);

        for (int i = 0; i < JGemsConfig.SYSTEM.MAX_POINT_LIGHTS; i++) {
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);

            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);

            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
        }
        buffer.flip();

        buffer2.putInt(0);
        buffer2.flip();

        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, 0L, buffer);
        ShaderStorageBufferProgram.updateSubDataSSBO(pointLightsBuffer, sizeMainBuffer, buffer2);
    }

    public void updateSpotLightsBuffer(HashMap<SpotLight, Integer> spotLightIdsHashMap, ShaderStorageBufferObject spotLightsBuffer, MemoryStack stack, Matrix4f viewMatrix) {
        if (JGemsConfig.DEBUG.DISABLE_SPOT_LIGHTS) {
            this.clearSpotLightsBuffer(stack, spotLightsBuffer);
            return;
        }
        final List<SpotLight> spotLights = this.getSpotLights().stream().filter(SpotLight::isActive).sorted(Comparator.comparing(e -> e.getBrightness() * -1.0f)).toList();
        final int sizeMainBuffer = JGemsConfig.SYSTEM.SPOT_LIGHT_BUFFER_PACK_SIZE * (4);
        ByteBuffer buffer = stack.malloc(sizeMainBuffer);
        ByteBuffer buffer2 = stack.malloc(Integer.BYTES);

        int total = spotLights.size();
        for (SpotLight spotLight : spotLights) {
            Vector3f lightViewPos = LightScene.passVectorInViewSpace(spotLight.getLightPosition(), viewMatrix, 1.0f);
            Vector3f lightViewRot = LightScene.passVectorInViewSpace(spotLight.getLightDirection(), viewMatrix, 0.0f);
            buffer.putFloat(spotLight.getLightPosition().x);
            buffer.putFloat(spotLight.getLightPosition().y);
            buffer.putFloat(spotLight.getLightPosition().z);
            buffer.putFloat(spotLight.getBrightness());

            buffer.putFloat(lightViewRot.x);
            buffer.putFloat(lightViewRot.y);
            buffer.putFloat(lightViewRot.z);
            buffer.putInt(spotLightIdsHashMap.getOrDefault(spotLight, -1));

            buffer.putFloat(lightViewPos.x);
            buffer.putFloat(lightViewPos.y);
            buffer.putFloat(lightViewPos.z);
            buffer.putFloat(spotLight.getAttenuationFactor());

            buffer.putFloat(spotLight.getLightColor().x);
            buffer.putFloat(spotLight.getLightColor().y);
            buffer.putFloat(spotLight.getLightColor().z);

            buffer.putFloat(spotLight.getCutOff());
        }
        buffer.flip();

        buffer2.putInt(total);
        buffer2.flip();

        ShaderStorageBufferProgram.updateSubDataSSBO(spotLightsBuffer, 0L, buffer);
        ShaderStorageBufferProgram.updateSubDataSSBO(spotLightsBuffer, sizeMainBuffer, buffer2);
    }

    public void clearSpotLightsBuffer(MemoryStack stack, ShaderStorageBufferObject spotLightsBuffer) {
        final int sizeMainBuffer = JGemsConfig.SYSTEM.SPOT_LIGHT_BUFFER_PACK_SIZE * (4);
        ByteBuffer buffer = stack.malloc(sizeMainBuffer);
        ByteBuffer buffer2 = stack.malloc(Integer.BYTES);

        for (int i = 0; i < JGemsConfig.SYSTEM.MAX_SPOT_LIGHTS; i++) {
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);

            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putInt(0);

            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);

            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
            buffer.putFloat(0.0f);
        }
        buffer.flip();

        buffer2.putInt(0);
        buffer2.flip();

        ShaderStorageBufferProgram.updateSubDataSSBO(spotLightsBuffer, 0L, buffer);
        ShaderStorageBufferProgram.updateSubDataSSBO(spotLightsBuffer, sizeMainBuffer, buffer2);
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

    public boolean containsSpotLight(SpotLight spotLight) {
        return this.getSpotLights().contains(spotLight);
    }

    public boolean containsPointLight(PointLight pointLight) {
        return this.getPointLights().contains(pointLight);
    }

    public abstract int getMaxPointLights();
    public abstract int getMaxSpotLights();

    @Override
    public SunLight getSunLight() {
        return this.sunLight;
    }

    @Override
    public Set<SpotLight> getSpotLights() {
        return this.spotLights;
    }

    public Set<PointLight> getPointLights() {
        return this.pointLights;
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }

    public ShaderStorageBufferObject getSpotLightsBuffer() {
        return this.spotLightsBuffer;
    }

    public ShaderStorageBufferObject getSunBuffer() {
        return this.sunBuffer;
    }

    public ShaderStorageBufferObject getPointLightsBuffer() {
        return this.pointLightsBuffer;
    }
}