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

package ru.jgems3d.engine.audio.sound;

import org.lwjgl.openal.AL10;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.audio.SoundManager;
import ru.jgems3d.engine.audio.sound.loaders.ogg.Ogg;
import ru.jgems3d.engine.system.resources.cache.ICached;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.engine.system.service.path.JGemsPath;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class SoundBuffer implements ICached {
    private final JGemsPath soundPath;
    private int buffer;

    public SoundBuffer(JGemsPath soundPath) {
        this.soundPath = soundPath;
    }

    public static SoundBuffer createSoundBuffer(ResourceCache resourceCache, JGemsPath soundPath, int soundFormat) {
        if (resourceCache.checkObjectInCache(soundPath)) {
            return (SoundBuffer) resourceCache.getCachedObject(soundPath);
        }
        SoundBuffer soundBuffer = new SoundBuffer(soundPath);
        if (soundBuffer.loadSound(soundFormat)) {
            resourceCache.addObjectInBuffer(soundPath, soundBuffer);
        } else {
            return null;
        }
        return soundBuffer;
    }

    public boolean loadSound(int soundFormat) {
        this.buffer = AL10.alGenBuffers();
        SoundManager.checkALonErrors();
        try {
            try (InputStream inputStream = JGems3D.loadFileFromJar(this.getSoundPath())) {
                return this.readOgg(inputStream, soundFormat);
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    private boolean readOgg(InputStream inputStream, int soundFormat) throws UnsupportedAudioFileException, IOException {
        Ogg ogg = (Ogg) Ogg.create(inputStream);
        //Wave wave = (Wave) Wave.create(inputStream);
        if (ogg != null) {
            AL10.alBufferData(this.buffer, soundFormat, ogg.getPcm(), ogg.getSampleRate());
            ogg.dispose();
            SoundManager.checkALonErrors();
            return true;
        } else {
            JGemsHelper.getLogger().warn("Failed to read sound: " + this.getSoundPath());
        }
        return false;
    }

    public int getBuffer() {
        return this.buffer;
    }

    public JGemsPath getSoundPath() {
        return this.soundPath;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        AL10.alDeleteBuffers(this.getBuffer());
        SoundManager.checkALonErrors();
    }
}
