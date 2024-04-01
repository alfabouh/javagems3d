package ru.BouH.engine.audio.sound.data;

public enum SoundType {
    WORLD_AMBIENT_SOUND(new SoundData(true, true)),
    BACKGROUND_AMBIENT_SOUND(new SoundData(false, true)),
    WORLD_SOUND(new SoundData(true, false)),
    BACKGROUND_SOUND(new SoundData(false, false));

    private final SoundData soundData;

    SoundType(SoundData soundData) {
        this.soundData = soundData;
    }

    public SoundData getSoundData() {
        return this.soundData;
    }
}
