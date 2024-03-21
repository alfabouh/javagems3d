package ru.BouH.engine.game.map.loader;

import org.joml.Vector3d;

public final class MapInfo {
    private final Vector3d playerStartPos;
    private final double sunBrightness;
    private final Vector3d sunColor;
    private final String levelName;

    public MapInfo(Vector3d playerStartPos, double sunBrightness, Vector3d sunColor, String levelName) {
        this.playerStartPos = playerStartPos;
        this.sunBrightness = sunBrightness;
        this.sunColor = sunColor;
        this.levelName = levelName;
    }

    public Vector3d getPlayerStartPos() {
        return this.playerStartPos;
    }

    public String getLevelName() {
        return this.levelName;
    }

    public Vector3d getSunColor() {
        return this.sunColor;
    }

    public double getSunBrightness() {
        return this.sunBrightness;
    }
}
