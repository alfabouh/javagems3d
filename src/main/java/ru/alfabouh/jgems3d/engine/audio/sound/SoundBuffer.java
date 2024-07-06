package ru.alfabouh.jgems3d.engine.audio.sound;

import org.lwjgl.openal.AL10;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.SoundManager;
import ru.alfabouh.jgems3d.engine.audio.sound.loaders.ogg.Ogg;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ICached;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class SoundBuffer implements ICached {
    private final String soundName;
    private int buffer;

    public SoundBuffer(String soundName) {
        this.soundName = soundName;
    }

    public static SoundBuffer createSoundBuffer(ResourceCache resourceCache, String soundName, int soundFormat) {
        if (resourceCache.checkObjectInCache(soundName)) {
            return (SoundBuffer) resourceCache.getCachedObject(soundName);
        }
        SoundBuffer soundBuffer = new SoundBuffer(soundName);
        if (soundBuffer.loadSound(soundFormat)) {
            resourceCache.addObjectInBuffer(soundName, soundBuffer);
        }
        return soundBuffer;
    }

    public boolean loadSound(int soundFormat) {
        this.buffer = AL10.alGenBuffers();
        SoundManager.checkALonErrors();
        try {
            return this.readWave(JGems.loadFileJar("/assets/jgems/sounds/" + this.getSoundName()), soundFormat);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new JGemsException(e);
        }
    }

    private boolean readWave(InputStream inputStream, int soundFormat) throws UnsupportedAudioFileException, IOException {
        Ogg ogg = (Ogg) Ogg.create(inputStream);
        //Wave wave = (Wave) Wave.create(inputStream);
        if (ogg != null) {
            AL10.alBufferData(this.buffer, soundFormat, ogg.getPcm(), ogg.getSampleRate());
            ogg.dispose();
            SoundManager.checkALonErrors();
            return true;
        } else {
            SystemLogging.get().getLogManager().warn("Failed to read sound: " + this.getSoundName());
        }
        return false;
    }

    public int getBuffer() {
        return this.buffer;
    }

    public String getSoundName() {
        return this.soundName;
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        AL10.alDeleteBuffers(this.getBuffer());
        SoundManager.checkALonErrors();
    }
}
