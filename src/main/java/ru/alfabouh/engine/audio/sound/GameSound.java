package ru.alfabouh.engine.audio.sound;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.audio.SoundManager;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.physics.world.object.WorldItem;

import java.nio.IntBuffer;

public class GameSound {
    private final SoundType soundType;
    private final SoundBuffer soundBuffer;
    private int source;
    private WorldItem attachedTo;
    private float gain;
    private float pitch;
    private float rollOff;

    private GameSound(@NotNull SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, WorldItem attachedTo) {
        this.soundBuffer = soundBuffer;
        this.soundType = soundType;
        this.attachedTo = attachedTo;
        this.source = AL10.AL_NONE;

        this.setGain(gain);
        this.setPitch(pitch);
        this.setRollOff(rollOff);
        this.setupSound();
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, WorldItem attachedTo) {
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, attachedTo);
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff) {
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, null);
    }

    private void setupSound() {
        this.source = AL10.alGenSources();

        SoundManager.checkALonErrors();
        AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, this.getSoundType().getSoundData().isLocatedInWorld() ? AL10.AL_FALSE : AL10.AL_TRUE);
        AL10.alSourcei(this.source, AL10.AL_LOOPING, this.getSoundType().getSoundData().isLooped() ? AL10.AL_TRUE : AL10.AL_FALSE);
        AL10.alSourcei(this.source, AL10.AL_BUFFER, this.getSoundBuffer().getBuffer());
        AL10.alSourcef(this.source, AL10.AL_REFERENCE_DISTANCE, Math.max(Math.max(this.getGain(), 0.0f) * 2.0f, 1.0f));
        SoundManager.checkALonErrors();

        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
        } else {
            this.setPosition(new Vector3d(0.0d));
        }

        this.setVelocity(new Vector3d(0.0d, 0.0d, 0.0d));

        SoundManager.checkALonErrors();
        this.updateParams();
        SoundManager.checkALonErrors();
        SoundManager.sounds.add(this);
    }

    private void updateParams() {
        AL10.alSourcef(this.source, AL10.AL_ROLLOFF_FACTOR, this.getRollOff());
        AL10.alSourcef(this.source, AL10.AL_GAIN, this.getGain());
        AL10.alSourcef(this.source, AL10.AL_PITCH, this.getPitch());
        SoundManager.checkALonErrors();
    }

    public void updateSound() {
        SoundManager.checkALonErrors();
        this.updateParams();
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

    public float getRollOff() {
        return Math.max(this.rollOff, 0.0f);
    }

    public float getGain() {
        return Math.max(this.gain, 0.0f);
    }

    public float getPitch() {
        return Math.max(this.pitch, 0.0f);
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setRollOff(float rollOff) {
        this.rollOff = rollOff;
    }

    public boolean isPaused() {
        if (!this.isValid()) {
            return false;
        }
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
    }

    public boolean isStopped() {
        if (!this.isValid()) {
            return false;
        }
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
    }

    public boolean isPlaying() {
        if (!this.isValid()) {
            return false;
        }
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void playSound() {
        if (!this.isValid()) {
            this.setupSound();
            SoundManager.checkALonErrors();
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
            int bufferProcessed = AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_PROCESSED);
            while (bufferProcessed-- > 0) {
                IntBuffer buffer = IntBuffer.allocate(1);
                AL10.alSourceUnqueueBuffers(this.source, buffer);
            }
            AL10.alSourcei(this.source, AL10.AL_BUFFER, AL10.AL_NONE);
            AL10.alDeleteSources(this.source);
            this.source = AL10.AL_NONE;
            SoundManager.checkALonErrors();
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
