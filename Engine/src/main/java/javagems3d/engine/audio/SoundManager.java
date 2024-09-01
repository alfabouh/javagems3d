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

package javagems3d.engine.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.audio.sound.GameSound;
import javagems3d.engine.audio.sound.SoundBuffer;
import javagems3d.engine.audio.sound.data.SoundType;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.system.service.exceptions.JGemsRuntimeException;
import javagems3d.engine.system.service.synchronizing.SyncManager;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.Set;

public final class SoundManager {
    public static final Set<GameSound> sounds = SyncManager.createSyncronisedSet();
    private final Set<GameSound> tempSet;
    private boolean isSystemCreated;
    private long device;
    private long context;

    public SoundManager() {
        this.tempSet = SyncManager.createSyncronisedSet();
        this.isSystemCreated = false;
    }

    public static void checkALonErrors() {
        int errCode = AL10.alGetError();
        if (errCode != AL10.AL_NO_ERROR) {
            switch (errCode) {
                case AL10.AL_INVALID_NAME: {
                    throw new JGemsRuntimeException("AL_INVALID_NAME: a bad name (ID) was passed to an OpenAL function!");
                }
                case AL10.AL_INVALID_ENUM: {
                    throw new JGemsRuntimeException("AL_INVALID_ENUM: an invalid enum value was passed to an OpenAL function!");
                }
                case AL10.AL_INVALID_VALUE: {
                    throw new JGemsRuntimeException("AL_INVALID_VALUE: an invalid value was passed to an OpenAL function!");
                }
                case AL10.AL_INVALID_OPERATION: {
                    throw new JGemsRuntimeException("AL_INVALID_OPERATION: the requested operation is not valid!");
                }
                case AL10.AL_OUT_OF_MEMORY: {
                    throw new JGemsRuntimeException("AL_OUT_OF_MEMORY: the requested operation resulted in OpenAL running out of memory!");
                }
                default: {
                    throw new JGemsRuntimeException("OpenAL unknown error!");
                }
            }
        }
    }

    public void createSystem() {
        JGemsHelper.getLogger().log("Creating sound OpenAL system!");
        this.device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (this.getDevice() == MemoryUtil.NULL) {
            throw new JGemsRuntimeException("Failed to create OpenAL device!");
        }
        ALCCapabilities alcCapabilities = ALC.createCapabilities(this.getDevice());
        this.context = ALC10.alcCreateContext(this.getDevice(), (IntBuffer) null);
        if (this.getContext() == MemoryUtil.NULL) {
            throw new JGemsRuntimeException("Failed to create OpenAL context!");
        }
        ALC10.alcMakeContextCurrent(this.getContext());
        AL.createCapabilities(alcCapabilities);
        this.isSystemCreated = true;
        JGemsHelper.getLogger().log("OpenAL system successfully created!");
        AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
        SoundManager.checkALonErrors();
    }

    public void stopAllSounds() {
        Iterator<GameSound> gameSoundIterator = SoundManager.sounds.iterator();
        while (gameSoundIterator.hasNext()) {
            GameSound gameSound = gameSoundIterator.next();
            gameSound.stopSound();
            gameSound.cleanUp();
            gameSoundIterator.remove();
        }
        this.tempSet.clear();
    }

    public void pauseAllSounds() {
        for (GameSound gameSound : SoundManager.sounds) {
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
        SoundManager.sounds.clear();
        ALC10.alcMakeContextCurrent(MemoryUtil.NULL);
        ALC10.alcCloseDevice(this.getDevice());
        ALC10.alcDestroyContext(this.getContext());
        ALC.destroy();
    }

    public GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff) {
        if (!this.isSystemCreated()) {
            return null;
        }
        return GameSound.createSound(soundBuffer, soundType, pitch, volume, rollOff, null);
    }

    public GameSound playLocalSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, volume, 1.0f, null);
        gameSound.playSound();
        return gameSound;
    }

    public GameSound playSoundAt(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, Vector3f position) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, volume, rollOff, null);
        gameSound.setPosition(position);
        gameSound.playSound();
        return gameSound;
    }

    public GameSound playSoundAtEntity(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, WorldItem worldItem) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, volume, rollOff, worldItem);
        gameSound.playSound();
        return gameSound;
    }

    public void update() {
        if (!this.isSystemCreated()) {
            return;
        }
        Iterator<GameSound> gameSoundIterator = SoundManager.sounds.iterator();
        while (gameSoundIterator.hasNext()) {
            GameSound gameSound = gameSoundIterator.next();
            if (gameSound.isValid()) {
                if (!gameSound.isStopped()) {
                    gameSound.updateSound();
                } else {
                    gameSound.cleanUp();
                    gameSoundIterator.remove();
                }
            } else {
                gameSoundIterator.remove();
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
