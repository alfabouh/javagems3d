package ru.jgems3d.engine.system.resources.assets.material.samples;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ISample;
import ru.jgems3d.engine.system.resources.assets.material.samples.packs.CubeMapTexturePack;
import ru.jgems3d.engine.system.resources.cache.ICached;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;

public class CubeMapSample implements ISample, ICached {
    private int textureId;

    public CubeMapSample() {
    }

    public static CubeMapSample createCubeMap(ResourceCache resourceCache, CubeMapTexturePack cubeMapTexturePack) {
        CubeMapSample cubeMap = new CubeMapSample();
        cubeMap.registerCubeMap(cubeMapTexturePack);
        if (resourceCache != null) {
            if (resourceCache.checkObjectInCache(cubeMapTexturePack.getId())) {
                return (CubeMapSample) resourceCache.getCachedObject(cubeMapTexturePack.getId());
            }
            if (cubeMap.isValid()) {
                resourceCache.addObjectInBuffer(cubeMapTexturePack.getId(), cubeMap);
            } else {
                throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
            }
        }
        return cubeMap;
    }

    public void registerCubeMap(CubeMapTexturePack cubeMapTexturePack) {
        this.textureId = GL30.glGenTextures();

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE);

        for (int i = 0; i < 6; i++) {
            CubeMapTexturePack.Data data = cubeMapTexturePack.getTextureArray()[i];
            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_RGB16, data.size.x, data.size.y, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, data.buffer);
        }
        cubeMapTexturePack.freeBuffers();

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public boolean isValid() {
        return this.textureId > 0;
    }

    public void unBindCubeMap() {
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void bindCubeMap() {
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.getTextureId());
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void cleanCubeMap() {
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
        GL30.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.cleanCubeMap();
    }
}
