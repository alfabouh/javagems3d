package ru.jgems3d.engine.audio.sound;

import org.lwjgl.openal.AL10;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.audio.SoundManager;
import ru.jgems3d.engine.audio.sound.loaders.ogg.Ogg;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.cache.ICached;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class SoundBuffer implements ICached {
    private final JGPath soundPath;
    private int buffer;

    public SoundBuffer(JGPath soundPath) {
        this.soundPath = soundPath;
    }

    public static SoundBuffer createSoundBuffer(ResourceCache resourceCache, JGPath soundPath, int soundFormat) {
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
            return this.readOgg(JGems3D.loadFileJar(this.getSoundPath()), soundFormat);
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

    public JGPath getSoundPath() {
        return this.soundPath;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        AL10.alDeleteBuffers(this.getBuffer());
        SoundManager.checkALonErrors();
    }
}
