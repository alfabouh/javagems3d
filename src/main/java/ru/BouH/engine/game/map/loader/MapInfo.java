package ru.BouH.engine.game.map.loader;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;

public final class MapInfo {
    private final Vector4d fog;
    private final Vector3d playerStartPos;
    private final float sunBrightness;
    private final Vector3f sunColor;
    private final String levelName;
    private final boolean skyCoveredByFog;

    public MapInfo(Vector4d fog, boolean skyCoveredByFog, Vector3d playerStartPos, float sunBrightness, Vector3f sunColor, String levelName) {
        this.playerStartPos = playerStartPos;
        this.fog = fog;
        this.sunBrightness = sunBrightness;
        this.skyCoveredByFog = skyCoveredByFog;
        this.sunColor = sunColor;
        this.levelName = levelName;
    }

    public boolean isSkyCoveredByFog() {
        return this.skyCoveredByFog;
    }

    public Vector4d getFog() {
        return this.fog;
    }

    public Vector3d getPlayerStartPos() {
        return this.playerStartPos;
    }

    public String getLevelName() {
        return this.levelName;
    }

    public Vector3f getSunColor() {
        return this.sunColor;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }
}
