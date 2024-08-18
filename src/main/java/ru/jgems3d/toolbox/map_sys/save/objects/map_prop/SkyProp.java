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

package ru.jgems3d.toolbox.map_sys.save.objects.map_prop;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import ru.jgems3d.engine.system.service.path.JGemsPath;

public class SkyProp {
    private final Vector3f sunPos;
    private final Vector3f sunColor;
    private float sunBrightness;
    private String skyBoxPath;

    public SkyProp() {
        this(TextureAssetsLoader.defaultSkyCubeMapPath.toString(), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f), 1.0f);
    }

    public SkyProp(String skyBoxPath, Vector3f sunPos, Vector3f sunColor, float sunBrightness) {
        this.skyBoxPath = skyBoxPath;
        this.sunPos = sunPos;
        this.sunColor = sunColor;
        this.sunBrightness = sunBrightness;
    }

    public String getSkyBoxPath() {
        return this.skyBoxPath;
    }

    public void setSkyBoxPath(String skyBoxPath) {
        this.skyBoxPath = skyBoxPath;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    public Vector3f getSunColor() {
        return new Vector3f(this.sunColor);
    }

    public void setSunColor(Vector3f sunColor) {
        this.sunColor.set(sunColor);
    }

    public Vector3f getSunPos() {
        return new Vector3f(this.sunPos);
    }

    public void setSunPos(Vector3f sunPos) {
        this.sunPos.set(sunPos);
    }
}
