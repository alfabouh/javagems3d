package ru.alfabouh.engine.audio.sound;

import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.audio.SoundManager;
import ru.alfabouh.engine.audio.sound.loaders.ogg.Ogg;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.exception.GameException;
import ru.alfabouh.engine.system.resources.cache.GameCache;
import ru.alfabouh.engine.system.resources.cache.ICached;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class SoundBuffer implements ICached {
    private final String soundName;
    private int buffer;

    public SoundBuffer(String soundName) {
        this.soundName = soundName;
    }

    public static SoundBuffer createSoundBuffer(GameCache gameCache, String soundName, int soundFormat) {
        if (gameCache.checkObjectInCache(soundName)) {
            return gameCache.getCachedSound(soundName);
        }
        SoundBuffer soundBuffer = new SoundBuffer(soundName);
        if (soundBuffer.loadSound(soundFormat)) {
            gameCache.addObjectInBuffer(soundName, soundBuffer);
        }
        return soundBuffer;
    }

    public boolean loadSound(int soundFormat) {
        this.buffer = AL10.alGenBuffers();
        SoundManager.checkALonErrors();
        try {
            return this.readWave(JGems.loadFileJar("/assets/sounds/" + this.getSoundName()), soundFormat);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new GameException(e);
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
            JGems.get().getLogManager().warn("Failed to read sound: " + this.getSoundName());
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
    public void onCleaningCache(GameCache gameCache) {
        AL10.alDeleteBuffers(this.getBuffer());
        SoundManager.checkALonErrors();
    }
}
