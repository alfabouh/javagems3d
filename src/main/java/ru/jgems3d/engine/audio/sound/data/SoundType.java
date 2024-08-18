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

package ru.jgems3d.engine.audio.sound.data;

public enum SoundType {
    WORLD_AMBIENT_SOUND(new SoundData(true, true)),
    BACKGROUND_AMBIENT_SOUND(new SoundData(false, true)),
    WORLD_SOUND(new SoundData(true, false)),
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
