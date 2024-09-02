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

package javagems3d.temp.map_sys.save.objects.map_prop;

import org.joml.Vector3f;

public class FogProp {
    private final Vector3f fogColor;
    private float fogDensity;
    private boolean fogEnabled;
    private boolean skyCoveredByFog;

    public FogProp() {
        this(new Vector3f(1.0f), 0.0f, false, true);
    }

    public FogProp(Vector3f fogColor, float fogDensity, boolean fogEnabled, boolean skyCoveredByFog) {
        this.fogColor = fogColor;
        this.fogDensity = fogDensity;
        this.fogEnabled = fogEnabled;
        this.skyCoveredByFog = skyCoveredByFog;
    }

    public Vector3f getFogColor() {
        return new Vector3f(this.fogColor);
    }

    public void setFogColor(Vector3f fogColor) {
        this.fogColor.set(fogColor);
    }

    public boolean isFogEnabled() {
        return this.fogEnabled;
    }

    public void setFogEnabled(boolean fogEnabled) {
        this.fogEnabled = fogEnabled;
    }

    public float getFogDensity() {
        return this.fogDensity;
    }

    public void setFogDensity(float fogDensity) {
        this.fogDensity = fogDensity;
    }

    public boolean isSkyCoveredByFog() {
        return this.skyCoveredByFog;
    }

    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.skyCoveredByFog = skyCoveredByFog;
    }
}
