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

package javagems3d.audio;

import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL10;
import javagems3d.JGems3D;
import javagems3d.audio.loaders.ogg.Ogg;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class SoundBuffer implements ICached {
    private final JGemsPathSource soundPath;
    private int buffer;

    public SoundBuffer(JGemsPathSource soundPath) {
        this.soundPath = soundPath;
    }

    public static SoundBuffer createSoundBuffer(@NotNull JGemsPathSource soundPath, ResourceCache resourceCache, int soundFormat) {
        if (resourceCache.checkObjectInCache(soundPath, SoundBuffer.class)) {
            return (SoundBuffer) resourceCache.getCachedObject(soundPath);
        }
        SoundBuffer soundBuffer = new SoundBuffer(soundPath);
        if (soundBuffer.loadSound(soundFormat)) {
            resourceCache.registerInCache(soundPath, soundBuffer);
        } else {
            return null;
        }
        return soundBuffer;
    }

    public boolean loadSound(int soundFormat) {
        this.buffer = AL10.alGenBuffers();
        JGemsSoundManager.checkALonErrors();
        try {
            try (InputStream inputStream = JGems3D.getInputStream(this.getSoundPath())) {
                return this.readOgg(inputStream, soundFormat);
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.get().exception(e);
            return false;
        }
    }

    private boolean readOgg(InputStream inputStream, int soundFormat) throws UnsupportedAudioFileException, IOException {
        Ogg ogg = (Ogg) Ogg.create(inputStream, soundFormat == AL10.AL_FORMAT_MONO16);
        //Wave wave = (Wave) Wave.create(inputStream);
        if (ogg != null) {
            AL10.alBufferData(this.buffer, soundFormat, ogg.getPcm(), ogg.getSampleRate());
            ogg.dispose();
            JGemsSoundManager.checkALonErrors();
            return true;
        } else {
            Log.get().warn("Failed to read sound: " + this.getSoundPath());
        }
        return false;
    }

    public int getBuffer() {
        return this.buffer;
    }

    public JGemsPathSource getSoundPath() {
        return this.soundPath;
    }

    @Override
    public void onClearingCache(ResourceCache resourceCache) {
        AL10.alDeleteBuffers(this.getBuffer());
        JGemsSoundManager.checkALonErrors();
    }
}
