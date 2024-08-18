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
