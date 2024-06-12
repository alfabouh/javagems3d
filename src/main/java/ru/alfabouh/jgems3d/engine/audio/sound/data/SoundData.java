package ru.alfabouh.jgems3d.engine.audio.sound.data;

public final class SoundData {
    private final boolean isLocatedInWorld;
    private final boolean isLooped;

    public SoundData(boolean isLocatedInWorld, boolean isLooped) {
        this.isLocatedInWorld = isLocatedInWorld;
        this.isLooped = isLooped;
    }

    public boolean isLocatedInWorld() {
        return this.isLocatedInWorld;
    }

    public boolean isLooped() {
        return this.isLooped;
    }
}
