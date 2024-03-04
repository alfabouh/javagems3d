package ru.BouH.engine.audio.sound;

import org.joml.Vector3d;
import org.lwjgl.openal.AL10;
import ru.BouH.engine.audio.SoundManager;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.physics.world.object.WorldItem;

public class GameSound {
    private final SoundType soundType;
    private final SoundBuffer soundBuffer;
    private int source;
    private boolean hasStarted;
    private boolean wantsToBeCleared;
    private WorldItem attachedTo;

    private GameSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, WorldItem attachedTo) {
        this.hasStarted = false;
        this.soundBuffer = soundBuffer;
        this.soundType = soundType;
        this.attachedTo = attachedTo;
        this.wantsToBeCleared = false;
        this.setupSoundOptions(pitch, gain, rollOff);
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, WorldItem attachedTo) {
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, attachedTo);
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff) {
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, null);
    }

    private void setupSoundOptions(float pitch, float gain, float rollOff) {
        this.source = AL10.alGenSources();
        AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, this.getSoundType().getSoundData().isLocatedInWorld() ? AL10.AL_FALSE : AL10.AL_TRUE);
        AL10.alSourcei(this.source, AL10.AL_LOOPING, this.getSoundType().getSoundData().isLooped() ? AL10.AL_TRUE : AL10.AL_FALSE);
        AL10.alSourcei(this.source, AL10.AL_BUFFER, this.getSoundBuffer().getBuffer());
        AL10.alSourcef(this.source, AL10.AL_REFERENCE_DISTANCE, Math.max(gain * 2.0f, 1.0f));
        AL10.alSourcef(this.source, AL10.AL_ROLLOFF_FACTOR, rollOff);
        SoundManager.checkALonErrors();
        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
        } else {
            this.setPosition(new Vector3d(0.0d));
        }
        this.setVelocity(new Vector3d(0.0d, 0.0d, 0.0d));
        this.setPitch(pitch);
        this.setGain(gain);
        SoundManager.checkALonErrors();
    }

    public void updateSound() {
        SoundManager.checkALonErrors();
        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
            if (this.getAttachedTo().isDead()) {
                this.stopSound();
                return;
            }
        }
        if (this.hasStarted) {
            if (this.isStopped()) {
                this.cleanUp();
            }
        }
        SoundManager.checkALonErrors();
    }

    public void setPosition(Vector3d vector3d) {
        AL10.alSource3f(this.source, AL10.AL_POSITION, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z);
    }

    public void setVelocity(Vector3d vector3d) {
        AL10.alSource3f(this.source, AL10.AL_VELOCITY, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z);
    }

    public void setGain(float gain) {
        AL10.alSourcef(this.source, AL10.AL_GAIN, gain);
    }

    public void setPitch(float pitch) {
        AL10.alSourcef(this.source, AL10.AL_PITCH, pitch);
    }

    public float getGain() {
        return AL10.alGetSourcef(this.source, AL10.AL_GAIN);
    }

    public float getPitch() {
        return AL10.alGetSourcef(this.source, AL10.AL_PITCH);
    }

    public boolean isPaused() {
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
    }

    public boolean isStopped() {
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void playSound() {
        AL10.alSourcePlay(this.source);
        this.hasStarted = true;
    }

    public void pauseSound() {
        AL10.alSourcePause(this.source);
    }

    public void stopSound() {
        AL10.alSourceStop(this.source);
        this.cleanUp();
    }

    public void cleanUp() {
        AL10.alDeleteSources(this.source);
        this.wantsToBeCleared = true;
    }

    public boolean isWantsToBeCleared() {
        return this.wantsToBeCleared;
    }

    public void setAttachedTo(WorldItem attachedTo) {
        this.attachedTo = attachedTo;
    }

    public WorldItem getAttachedTo() {
        return this.attachedTo;
    }

    public SoundBuffer getSoundBuffer() {
        return this.soundBuffer;
    }

    public SoundType getSoundType() {
        return this.soundType;
    }
}
