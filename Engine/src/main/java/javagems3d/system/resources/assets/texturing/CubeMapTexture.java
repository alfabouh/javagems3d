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

package javagems3d.system.resources.assets.texturing;

import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.packs.CubeMapTexturingDataPack;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

public class CubeMapTexture implements ISample, ICached {
    private int textureId;

    public CubeMapTexture() {
    }

    public static CubeMapTexture createCubeMap(ResourceCache resourceCache, CubeMapTexturingDataPack cubeMapTexturingDataPack) {
        CubeMapTexture cubeMap = new CubeMapTexture();
        cubeMap.registerCubeMap(cubeMapTexturingDataPack);
        if (resourceCache != null) {
            if (resourceCache.checkObjectInCache(cubeMapTexturingDataPack.getName())) {
                return (CubeMapTexture) resourceCache.getCachedObject(cubeMapTexturingDataPack.getName());
            }
            if (cubeMap.isValid()) {
                resourceCache.addObjectInBuffer(cubeMapTexturingDataPack.getName(), cubeMap);
            } else {
                throw new JGemsRuntimeException("Couldn't add invalid texture in cache!");
            }
        }
        return cubeMap;
    }

    public void registerCubeMap(CubeMapTexturingDataPack cubeMapTexturingDataPack) {
        this.textureId = GL46.glGenTextures();

        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_R, GL46.GL_CLAMP_TO_EDGE);

        for (int i = 0; i < 6; i++) {
            CubeMapTexturingDataPack.Data data = cubeMapTexturingDataPack.getTextureArray()[i];
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_RGB16, data.getSize().x, data.getSize().y, 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, data.getBuffer());
        }
        cubeMapTexturingDataPack.freeBuffers();

        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
    }

    public boolean isValid() {
        return this.textureId > 0;
    }

    public void unBindCubeMap() {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void bindCubeMap() {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, this.getTextureId());
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void clearCubeMap() {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
        GL46.glDeleteTextures(this.getTextureId());
        this.textureId = 0;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clearCubeMap();
    }
}
