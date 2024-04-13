package ru.BouH.engine.audio;

import org.joml.Vector3d;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.audio.sound.GameSound;
import ru.BouH.engine.audio.sound.SoundBuffer;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.world.object.WorldItem;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class SoundManager {
    private final Set<GameSound> sounds;
    private final Set<GameSound> tempSet;
    private boolean isSystemCreated;
    private long device;
    private long context;

    public SoundManager() {
        this.sounds = new HashSet<>();
        this.tempSet = new HashSet<>();
        this.isSystemCreated = false;
    }

    public static void checkALonErrors() {
        int errCode = AL10.alGetError();
        if (errCode != AL10.AL_NO_ERROR) {
            switch (errCode) {
                case AL10.AL_INVALID_NAME: {
                    throw new GameException("AL_INVALID_NAME: a bad name (ID) was passed to an OpenAL function!");
                }
                case AL10.AL_INVALID_ENUM: {
                    throw new GameException("AL_INVALID_ENUM: an invalid enum value was passed to an OpenAL function!");
                }
                case AL10.AL_INVALID_VALUE: {
                    throw new GameException("AL_INVALID_VALUE: an invalid value was passed to an OpenAL function!");
                }
                case AL10.AL_INVALID_OPERATION: {
                    throw new GameException("AL_INVALID_OPERATION: the requested operation is not valid!");
                }
                case AL10.AL_OUT_OF_MEMORY: {
                    throw new GameException("AL_OUT_OF_MEMORY: the requested operation resulted in OpenAL running out of memory!");
                }
                default: {
                    throw new GameException("OpenAL unknown error!");
                }
            }
        }
    }

    public void createSystem() {
        Game.getGame().getLogManager().log("Creating sound OpenAL system!");
        this.device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (this.getDevice() == MemoryUtil.NULL) {
            throw new GameException("Failed to create OpenAL device!");
        }
        ALCCapabilities alcCapabilities = ALC.createCapabilities(this.getDevice());
        this.context = ALC10.alcCreateContext(this.getDevice(), (IntBuffer) null);
        if (this.getContext() == MemoryUtil.NULL) {
            throw new GameException("Failed to create OpenAL context!");
        }
        ALC10.alcMakeContextCurrent(this.getContext());
        AL.createCapabilities(alcCapabilities);
        this.isSystemCreated = true;
        Game.getGame().getLogManager().log("OpenAL system successfully created!");
        AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
        SoundManager.checkALonErrors();
    }

    public void stopAllSounds() {
        for (GameSound gameSound : this.sounds) {
            if (gameSound.getSoundType() != SoundType.SYSTEM) {
                gameSound.stopSound();
                gameSound.cleanUp();
            }
        }
    }

    public void pauseAllSounds() {
        for (GameSound gameSound : this.sounds) {
            if (gameSound.isPlaying()) {
                if (gameSound.getSoundType() != SoundType.SYSTEM) {
                    gameSound.pauseSound();
                    this.tempSet.add(gameSound);
                }
            }
        }
    }

    public void resumeAllSounds() {
        Iterator<GameSound> gameSoundIterator = this.tempSet.iterator();
        while (gameSoundIterator.hasNext()) {
            GameSound sound = gameSoundIterator.next();
            sound.playSound();
            gameSoundIterator.remove();
        }
    }

    public void destroy() {
        this.isSystemCreated = false;
        this.stopAllSounds();
        this.sounds.clear();
        ALC10.alcMakeContextCurrent(MemoryUtil.NULL);
        ALC10.alcCloseDevice(this.getDevice());
        ALC10.alcDestroyContext(this.getContext());
        ALC.destroy();
    }

    public GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, gain, rollOff, null);
        this.sounds.add(gameSound);
        return gameSound;
    }

    public GameSound playLocalSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, gain, 1.0f, null);
        gameSound.playSound();
        this.sounds.add(gameSound);
        return null;
    }

    public GameSound playSoundAt(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, Vector3d position) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, gain, rollOff, null);
        gameSound.setPosition(position);
        gameSound.playSound();
        this.sounds.add(gameSound);
        return null;
    }

    public GameSound playSoundAtEntity(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, WorldItem worldItem) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, gain, rollOff, null);
        if (!worldItem.tryAttachSoundTo(gameSound)) {
            Game.getGame().getLogManager().warn("Couldn't attach sound to: " + worldItem);
        }
        gameSound.setPosition(worldItem.getPosition());
        gameSound.playSound();
        this.sounds.add(gameSound);
        return gameSound;
    }

    public void update() {
        if (!this.isSystemCreated()) {
            return;
        }
        Iterator<GameSound> iterator = this.sounds.iterator();
        while (iterator.hasNext()) {
            GameSound gameSound = iterator.next();
            if (!gameSound.isValid() || gameSound.isStopped()) {
                iterator.remove();
            } else {
                gameSound.updateSound();
            }
        }
        SoundManager.checkALonErrors();
    }

    public long getContext() {
        return this.context;
    }

    public long getDevice() {
        return this.device;
    }

    public boolean isSystemCreated() {
        return this.isSystemCreated;
    }
}
