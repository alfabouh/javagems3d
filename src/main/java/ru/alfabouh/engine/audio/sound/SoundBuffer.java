package ru.alfabouh.engine.audio.sound;

import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.audio.SoundManager;
import ru.alfabouh.engine.audio.sound.wave.OldJGLWaveData;
import ru.alfabouh.engine.audio.sound.wave.WaveData;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.game.resources.cache.GameCache;
import ru.alfabouh.engine.game.resources.cache.ICached;

import java.net.URL;

public class SoundBuffer implements ICached {
    private final String soundName;
    private int buffer;

    public SoundBuffer(String soundName, int soundFormat) {
        this.soundName = soundName;
        this.loadSound(soundFormat);
    }

    public static SoundBuffer createSoundBuffer(GameCache gameCache, String soundName, int soundFormat) {
        if (gameCache.checkObjectInCache(soundName)) {
            return gameCache.getCachedSound(soundName);
        }
        SoundBuffer soundBuffer = new SoundBuffer(soundName, soundFormat);
        gameCache.addObjectInBuffer(soundName, soundBuffer);
        return soundBuffer;
    }

    private void loadSound(int soundFormat) {
        this.buffer = AL10.alGenBuffers();
        SoundManager.checkALonErrors();

        URL url = this.getClass().getResource("/assets/sounds/" + this.getSoundName());
        if (url == null) {
            throw new GameException("Sound " + this.getSoundName() + " doesn't exist!");
        }

        this.readWave(url, soundFormat);
    }

    private void readWave(URL url, int soundFormat) {
        OldJGLWaveData WaveData = OldJGLWaveData.create(url);
        if (WaveData == null) {
            throw new GameException("Couldn't create wavefront: " + this.getSoundName());
        }
        AL10.alBufferData(this.buffer, soundFormat, WaveData.data, WaveData.samplerate);
        WaveData.dispose();
        SoundManager.checkALonErrors();
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
