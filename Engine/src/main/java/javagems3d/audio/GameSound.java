package javagems3d.audio;

import javagems3d.system.global.JGemsConfig;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import javagems3d.audio.data.SoundType;
import javagems3d.physics.world.basic.WorldItem;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class GameSound {
    public static final Object LOCK = new Object();
    private final SoundType soundType;
    private final SoundBuffer soundBuffer;
    private volatile int source;
    private WorldItem attachedTo;
    private volatile float volume;
    private volatile float pitch;
    private volatile float rollOff;
    private volatile float distance;
    private final Vector3f position;
    private final Vector3f velocity;

    private volatile boolean isPlaying;
    private volatile boolean isPaused;
    private volatile boolean isStopped;

    private GameSound(@NotNull SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, float distance, WorldItem attachedTo) {
        this.soundBuffer = soundBuffer;
        this.soundType = soundType;
        this.attachedTo = attachedTo;
        this.source = AL10.AL_NONE;

        this.position = new Vector3f();
        this.velocity = new Vector3f();

        this.setVolume(volume);
        this.setPitch(pitch);
        this.setDistance(distance);
        this.setRollOff(rollOff);

        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
        } else {
            this.setPosition(new Vector3f(0.0f));
        }

        this.setVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, float distance, WorldItem attachedTo) {
        if (soundBuffer == null) {
            return null;
        }
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, distance, attachedTo);
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, float distance) {
        if (soundBuffer == null) {
            return null;
        }
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, distance, null);
    }

    void setupSound() {
        if (ALC10.alcGetCurrentContext() == MemoryUtil.NULL || ALC10.alcGetContextsDevice(ALC10.alcGetCurrentContext()) == 0) {
            return;
        }
        this.source = AL10.alGenSources();

        JGemsSoundManager.checkALonErrors();
        AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, this.getSoundType().getSoundData().isLocatedInWorld() ? AL10.AL_FALSE : AL10.AL_TRUE);
        AL10.alSourcei(this.source, AL10.AL_LOOPING, this.getSoundType().getSoundData().isLooped() ? AL10.AL_TRUE : AL10.AL_FALSE);
        AL10.alSourcei(this.source, AL10.AL_BUFFER, this.getSoundBuffer().getBuffer());
        JGemsSoundManager.checkALonErrors();
        this.updateParams();
        JGemsSoundManager.register(this);
    }

    private void updateParams() {
        AL10.alSource3f(this.source, AL10.AL_POSITION, this.position.x, this.position.y, this.position.z);
        AL10.alSource3f(this.source, AL10.AL_VELOCITY, this.velocity.x, this.velocity.y, this.velocity.z);
        AL10.alSourcef(this.source, AL10.AL_REFERENCE_DISTANCE, this.getDistance());
        AL10.alSourcef(this.source, AL10.AL_ROLLOFF_FACTOR, this.getRollOff());
        AL10.alSourcef(this.source, AL10.AL_GAIN, this.getVolume());
        AL10.alSourcef(this.source, AL10.AL_PITCH, this.getPitch());
        JGemsSoundManager.checkALonErrors();
    }

    void updateSound() {
        //synchronized (GameSound.LOCK) {
        if (this.isValid()) {
            JGemsSoundManager.checkALonErrors();
            this.updateParams();
            this.isPlaying = AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
            this.isPaused = AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
            this.isStopped = AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
            if (this.isPaused || this.isStopped) {
                return;
            }
            if (this.getAttachedTo() != null) {
                this.setPosition(this.getAttachedTo().getPosition());
                if (this.getAttachedTo().isDead()) {
                    this.stopSound();
                    return;
                }
            }
            JGemsSoundManager.checkALonErrors();
        }
        //}
    }

   // @Override
   // protected void finalize() {
   //     this.clear();
   // }

    public void setPosition(Vector3f vector3f) {
        synchronized (GameSound.LOCK) {
            this.position.set(vector3f);
        }
    }

    public void setVelocity(Vector3f vector3f) {
        synchronized (GameSound.LOCK) {
            this.velocity.set(vector3f);
        }
    }

    public float getDistance() {
        //synchronized (GameSound.LOCK) {
            return Math.max(this.distance, 0.0f);
        //}
    }

    public void setDistance(float distance) {
      //  synchronized (GameSound.LOCK) {
            this.distance = distance;
      //  }
    }

    public float getRollOff() {
      //  synchronized (GameSound.LOCK) {
            return Math.max(this.rollOff, 0.0f);
       // }
    }

    public void setRollOff(float rollOff) {
      //  synchronized (GameSound.LOCK) {
            this.rollOff = rollOff;
      //  }
    }

    public float getVolume() {
      //  synchronized (GameSound.LOCK) {
            return Math.max(this.volume, 0.0f);
      //  }
    }

    public void setVolume(float gain) {
       // synchronized (GameSound.LOCK) {
            this.volume = gain;
       // }
    }

    public float getPitch() {
        //synchronized (GameSound.LOCK) {
            return Math.max(this.pitch, 0.0f);
        //}
    }

    public void setPitch(float pitch) {
        //synchronized (GameSound.LOCK) {
            this.pitch = pitch;
        //}
    }

    public boolean isPaused() {
        if (!this.isValid()) {
            return false;
        }
        return this.isPaused;
    }

    public boolean isStopped() {
        //synchronized (GameSound.LOCK) {
            if (!this.isValid()) {
                return false;
            }
            return this.isStopped;
        //}
    }

    public boolean isPlaying() {
        //synchronized (GameSound.LOCK) {
            if (!this.isValid()) {
                return false;
            }
            return this.isPlaying;
       // }
    }

    void playSound() {
        if (JGemsConfig.SYSTEM.DISABLE_SOUNDS) {
            return;
        }
        //synchronized (GameSound.LOCK) {
            if (!this.isValid()) {
                this.setupSound();
                JGemsSoundManager.checkALonErrors();
            }
            AL10.alSourcePlay(this.source);
        //}
    }

    void pauseSound() {
        //synchronized (GameSound.LOCK) {
            if (this.isValid()) {
                AL10.alSourcePause(this.source);
            }
        //}
    }

    void stopSound() {
        this.clear();
    }

    void clear() {
        synchronized (GameSound.LOCK) {
            if (this.source == AL10.AL_NONE) {
                return;
            }
            int src = this.source;
            this.source = AL10.AL_NONE;
            AL10.alSourceStop(src);
            AL10.alSourcei(src, AL10.AL_BUFFER, AL10.AL_NONE);
            int queued = AL10.alGetSourcei(src, AL10.AL_BUFFERS_QUEUED);
            while (queued-- > 0) {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer buffer = stack.mallocInt(1);
                    AL10.alSourceUnqueueBuffers(src, buffer);
                }
            }
            AL10.alDeleteSources(src);
            JGemsSoundManager.checkALonErrors();
        }
    }

    public WorldItem getAttachedTo() {
        synchronized (GameSound.LOCK) {
            return this.attachedTo;
        }
    }

    public void setAttachedTo(WorldItem attachedTo) {
        synchronized (GameSound.LOCK) {
            this.attachedTo = attachedTo;
        }
        this.setPosition(this.getAttachedTo().getPosition());
    }

    public SoundBuffer getSoundBuffer() {
        //synchronized (GameSound.LOCK) {
            return this.soundBuffer;
        //}
    }

    public SoundType getSoundType() {
        //synchronized (GameSound.LOCK) {
            return this.soundType;
        //}
    }

    public boolean isValid() {
        //synchronized (GameSound.LOCK) {
            return this.source != AL10.AL_NONE;
        //}
    }
}
