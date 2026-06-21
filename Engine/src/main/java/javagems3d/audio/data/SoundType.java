package javagems3d.audio.data;

public enum SoundType {
    WORLD_AMBIENT_SOUND(new SoundData(true, true)),
    WORLD_SOUND(new SoundData(true, false)),
    BACKGROUND_LOOP_SOUND(new SoundData(false, true)),
    BACKGROUND_SOUND(new SoundData(false, false)),
    SYSTEM(new SoundData(false, false));

    private final SoundData soundData;

    SoundType(SoundData soundData) {
        this.soundData = soundData;
    }

    public SoundData getSoundData() {
        return this.soundData;
    }
}
