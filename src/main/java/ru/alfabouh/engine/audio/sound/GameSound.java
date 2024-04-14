package ru.alfabouh.engine.audio.sound;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.audio.SoundManager;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.physics.world.object.WorldItem;

public class GameSound {
    private final SoundType soundType;
    private final SoundBuffer soundBuffer;
    private int source;
    private WorldItem attachedTo;
    private float gain;

    private GameSound(@NotNull SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, WorldItem attachedTo) {
        this.soundBuffer = soundBuffer;
        this.soundType = soundType;
        this.attachedTo = attachedTo;
        this.source = AL10.AL_NONE;
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
        SoundManager.checkALonErrors();
        AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, this.getSoundType().getSoundData().isLocatedInWorld() ? AL10.AL_FALSE : AL10.AL_TRUE);
        AL10.alSourcei(this.source, AL10.AL_LOOPING, this.getSoundType().getSoundData().isLooped() ? AL10.AL_TRUE : AL10.AL_FALSE);
        AL10.alSourcei(this.source, AL10.AL_BUFFER, this.getSoundBuffer().getBuffer());
        SoundManager.checkALonErrors();
        AL10.alSourcef(this.source, AL10.AL_REFERENCE_DISTANCE, Math.max(Math.max(gain, 0.0f) * 2.0f, 1.0f));
        AL10.alSourcef(this.source, AL10.AL_ROLLOFF_FACTOR, Math.max(rollOff, 0.0f));

        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
        } else {
            this.setPosition(new Vector3d(0.0d));
        }

        this.setVelocity(new Vector3d(0.0d, 0.0d, 0.0d));
        this.setPitch(Math.max(pitch, 0.0f));

        AL10.alSourcef(this.source, AL10.AL_GAIN, Math.max(gain, 0f));
        this.setGain(Math.max(gain, 0.0f));

        SoundManager.checkALonErrors();
    }

    public void updateSound() {
        SoundManager.checkALonErrors();
        if (this.isPaused() || this.isStopped()) {
            return;
        }
        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
            if (this.getAttachedTo().isDead()) {
                this.stopSound();
                return;
            }
        }
        AL10.alSourcef(this.source, AL10.AL_GAIN, Math.max(this.getGain() * Game.getGame().getGameSettings().soundGain.getValue(), 0.0f));
        SoundManager.checkALonErrors();
    }

    @Override
    protected void finalize() {
        this.cleanUp();
    }

    public void setPosition(Vector3d vector3d) {
        AL10.alSource3f(this.source, AL10.AL_POSITION, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z);
    }

    public void setVelocity(Vector3d vector3d) {
        AL10.alSource3f(this.source, AL10.AL_VELOCITY, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z);
    }

    public float getGain() {
        return this.gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public float getPitch() {
        return AL10.alGetSourcef(this.source, AL10.AL_PITCH);
    }

    public void setPitch(float pitch) {
        AL10.alSourcef(this.source, AL10.AL_PITCH, Math.max(pitch, 0f));
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
        if (!this.isValid()) {
            Game.getGame().getLogManager().warn("Tried to play invalid sound!");
            return;
        }
        AL10.alSourcePlay(this.source);
    }

    public void pauseSound() {
        if (this.isValid()) {
            AL10.alSourcePause(this.source);
        }
    }

    public void stopSound() {
        if (this.isValid()) {
            AL10.alSourceStop(this.source);
        }
    }

    public void cleanUp() {
        if (this.isValid()) {
            AL10.alDeleteSources(this.source);
            this.source = AL10.AL_NONE;
        }
    }

    public WorldItem getAttachedTo() {
        return this.attachedTo;
    }

    public void setAttachedTo(WorldItem attachedTo) {
        this.attachedTo = attachedTo;
    }

    public SoundBuffer getSoundBuffer() {
        return this.soundBuffer;
    }

    public SoundType getSoundType() {
        return this.soundType;
    }

    public boolean isValid() {
        return this.source != AL10.AL_NONE;
    }
}
