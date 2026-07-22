package javagems3d.audio;

import javagems3d.system.global.JGemsConfig;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;
import javagems3d.audio.data.SoundType;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.synchronizing.SyncManager;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public final class JGemsSoundManager {
    private static final Queue<SoundRequest> req_queue = new ConcurrentLinkedQueue<>();
    private static final Set<GameSound> sounds = SyncManager.createSyncronisedSet();
    private final Set<GameSound> tempSetPaused;
    private boolean isSystemCreated;
    private long device;
    private long context;

    public JGemsSoundManager() {
        this.tempSetPaused = SyncManager.createSyncronisedSet();
        this.isSystemCreated = false;
    }

    public static void checkALonErrors() {
        int errCode = AL10.alGetError();
        if (errCode != AL10.AL_NO_ERROR) {
            switch (errCode) {
                case AL10.AL_INVALID_NAME: {
                    throw new JGemsRuntimeException("AL_INVALID_NAME: a bad name (ID) was passed to an OpenAL function");
                }
                case AL10.AL_INVALID_ENUM: {
                    throw new JGemsRuntimeException("AL_INVALID_ENUM: an invalid enum value was passed to an OpenAL function");
                }
                case AL10.AL_INVALID_VALUE: {
                    throw new JGemsRuntimeException("AL_INVALID_VALUE: an invalid value was passed to an OpenAL function");
                }
                case AL10.AL_INVALID_OPERATION: {
                    throw new JGemsRuntimeException("AL_INVALID_OPERATION: the requested operation is not valid");
                }
                case AL10.AL_OUT_OF_MEMORY: {
                    throw new JGemsRuntimeException("AL_OUT_OF_MEMORY: the requested operation resulted in OpenAL running out of memory");
                }
                default: {
                    throw new JGemsRuntimeException("OpenAL unknown error");
                }
            }
        }
    }

    public void createSystem() {
        this.device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (this.getDevice() == MemoryUtil.NULL) {
            throw new JGemsRuntimeException("Failed to create OpenAL device");
        }
        ALCCapabilities alcCapabilities = ALC.createCapabilities(this.getDevice());
        this.context = ALC10.alcCreateContext(this.getDevice(), (IntBuffer) null);
        if (this.getContext() == MemoryUtil.NULL) {
            throw new JGemsRuntimeException("Failed to create OpenAL context");
        }
        ALC10.alcMakeContextCurrent(this.getContext());
        AL.createCapabilities(alcCapabilities);
        this.isSystemCreated = true;
        Log.get().info("OpenAL system successfully created");
        AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
        JGemsSoundManager.checkALonErrors();
    }

    public void stopAllSounds() {
        Iterator<GameSound> gameSoundIterator = JGemsSoundManager.sounds.iterator();
        while (gameSoundIterator.hasNext()) {
            GameSound gameSound = gameSoundIterator.next();
            JGemsSoundManager.stop(gameSound);
            gameSoundIterator.remove();
        }
        this.tempSetPaused.clear();
    }

    public void pauseAllSounds() {
        for (GameSound gameSound : JGemsSoundManager.sounds) {
            if (gameSound.isPlaying()) {
                if (gameSound.getSoundType() != SoundType.SYSTEM) {
                    JGemsSoundManager.pause(gameSound);
                    this.tempSetPaused.add(gameSound);
                }
            }
        }
    }

    public void resumeAllSounds() {
        Iterator<GameSound> gameSoundIterator = this.tempSetPaused.iterator();
        while (gameSoundIterator.hasNext()) {
            GameSound sound = gameSoundIterator.next();
            JGemsSoundManager.play(sound, null);
            gameSoundIterator.remove();
        }
    }

    public void clearCachedSounds() {
        JGemsSoundManager.req_queue.forEach(e -> e.buffer.stopSound());
        JGemsSoundManager.sounds.forEach(GameSound::stopSound);
    }

    public void destroy() {
        this.isSystemCreated = false;
        {
            JGemsSoundManager.sounds.clear();
            JGemsSoundManager.req_queue.clear();
        }
        ALC10.alcMakeContextCurrent(MemoryUtil.NULL);
        ALC10.alcCloseDevice(this.getDevice());
        ALC10.alcDestroyContext(this.getContext());
        ALC.destroy();
    }

    static void register(GameSound gameSound) {
        JGemsSoundManager.sounds.add(gameSound);
    }

    private static void checkSet() {
        if (JGemsSoundManager.sounds.size() >= JGemsConfig.SYSTEM.MAX_SOUND_BUFFERS) {
            Optional<GameSound> random = JGemsSoundManager.sounds.stream().filter(e -> !e.getSoundType().getSoundData().isLooped() && e.isValid()).findAny();
            if (random.isEmpty()) {
                random = JGemsSoundManager.sounds.stream().findAny();
            }
            if (random.isPresent()) {
                random.get().stopSound();
                Log.get().warn("Got max sound buffers! Killed random.");
            }
        }
    }

    public GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, float distance) {
        if (!this.isSystemCreated()) {
            return null;
        }
        return GameSound.createSound(soundBuffer, soundType, pitch, volume, rollOff, distance, null);
    }

    public GameSound playLocalSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, volume, 1.0f, 1.0f, null);
        JGemsSoundManager.play(gameSound, soundType);
        return gameSound;
    }

    public GameSound playSoundAt(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, float distance, Supplier<@NotNull Vector3f> position) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, volume, rollOff, distance, null);
        gameSound.setPosition(position.get());
        JGemsSoundManager.play(gameSound, soundType);
        return gameSound;
    }

    public GameSound playSoundAt(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, float distance, @NotNull Vector3f position) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, volume, rollOff, distance, null);
        gameSound.setPosition(position);
        JGemsSoundManager.play(gameSound, soundType);
        return gameSound;
    }

    public GameSound playSoundAtEntity(SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, float distance, @NotNull WorldItem worldItem) {
        if (!this.isSystemCreated()) {
            return null;
        }
        GameSound gameSound = GameSound.createSound(soundBuffer, soundType, pitch, volume, rollOff, distance, worldItem);
        JGemsSoundManager.play(gameSound, soundType);
        return gameSound;
    }

    public void update() {
        if (!this.isSystemCreated()) {
            return;
        }
       // JGems3D.get().getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssets.button, SoundType.BACKGROUND_SOUND, 1.0f, 0.2f);

        {
            JGemsSoundManager.checkSet();
        }
        {
            this.processRequests();
        }
        Iterator<GameSound> gameSoundIterator = JGemsSoundManager.sounds.iterator();
        while (gameSoundIterator.hasNext()) {
            GameSound gameSound = gameSoundIterator.next();
          //if (i++ >= JGemsConfig.SYSTEM.MAX_SOUND_BUFFERS) {
          //    Log.get().warn("Got max sound buffers! Rejected.");
          //    gameSound.clear();
          //    gameSoundIterator.remove();
          //    continue;
          //}
            if (gameSound.isValid()) {
                if (!gameSound.isStopped()) {
                    gameSound.updateSound();
                } else {
                    gameSound.clear();
                    gameSoundIterator.remove();
                }
            } else {
                gameSoundIterator.remove();
            }
        }
        JGemsSoundManager.checkALonErrors();
    }

    private void processRequests() {
        SoundRequest request;
        while ((request = JGemsSoundManager.req_queue.poll()) != null) {
            this.handleRequest(request);
        }
    }

    private void handleRequest(SoundRequest request) {
        switch (request.op()) {
            case START -> {
                GameSound sound = request.buffer();
                if (sound != null) {
                    sound.playSound();
                //    JGemsSoundManager.sounds.add(sound);
                }
            }
            case STOP -> {
                GameSound sound = request.buffer();
                sound.stopSound();
            }
            case PAUSE -> {
                GameSound sound = request.buffer();
                sound.pauseSound();
            }
        }
    }

    private static void submit(SoundRequest request) {
        if (request == null) {
            return;
        }
        JGemsSoundManager.req_queue.offer(request);
    }

    public static void play(GameSound buffer, SoundType type) {
        JGemsSoundManager.submit(new SoundRequest(buffer, SoundRequest.Operation.START, type));
    }

    public static void stop(GameSound buffer) {
        JGemsSoundManager.submit(new SoundRequest(buffer, SoundRequest.Operation.STOP));
    }

    public static void pause(GameSound buffer) {
        JGemsSoundManager.submit(new SoundRequest(buffer, SoundRequest.Operation.PAUSE));
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

    record SoundRequest(GameSound buffer, Operation op, Object... meta) {
            public enum Operation {
                STOP,
                START,
                PAUSE
            }
    }
}
