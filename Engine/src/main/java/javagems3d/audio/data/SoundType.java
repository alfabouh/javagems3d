/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
