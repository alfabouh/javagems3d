# Audio System

JGems3D uses **OpenAL** (via LWJGL) for runtime audio playback and currently supports **OGG** as the main sound asset format.

Audio resources are loaded through the standard asset loading pipeline using `systemResources`, the same way as textures, models, and other engine resources.

---

## Supported Audio Formats

The engine supports:

- Mono sound
- Stereo sound

This allows usage for:

- positional 3D world sounds
- ambient environmental loops
- UI/system sounds
- background music
- non-positional gameplay sounds

---

## Sound Playback Types

Several playback categories are built into the engine:

```java
WORLD_AMBIENT_SOUND(new SoundData(true, true)),
WORLD_SOUND(new SoundData(true, false)),
BACKGROUND_LOOP_SOUND(new SoundData(false, true)),
BACKGROUND_SOUND(new SoundData(false, false)),
SYSTEM(new SoundData(false, false));
```

### Type Overview

### `WORLD_AMBIENT_SOUND`

3D positioned looping sound.

Used for:

* ambient environment zones
* machinery hum
* wind sources
* continuous world effects

---

### `WORLD_SOUND`

3D positioned one-shot sound.

Used for:

* explosions
* gunshots
* footsteps
* object interactions

---

### `BACKGROUND_LOOP_SOUND`

Non-positional looping sound.

Used for:

* background music
* menu music
* global ambience

---

### `BACKGROUND_SOUND`

Non-positional one-shot sound.

Used for:

* stingers
* notifications
* temporary music events

---

### `SYSTEM`

Non-positional system-level sounds.

Usually used for:

* UI clicks
* debug notifications
* editor/system feedback

System sounds are not paused automatically when global pause is triggered.

---

# Sound Manager

Main entry point:

```java
JGemsHelper.getSoundManager()
```

Core implementation:

```java
public final class JGemsSoundManager
```

This class manages:

* OpenAL device/context creation
* sound playback
* positional updates
* pausing/resuming
* cleanup
* listener updates
* active sound lifecycle

---

# Creating OpenAL System

The engine initializes OpenAL automatically:

```java
soundManager.createSystem();
```

Internally this creates:

* audio device
* OpenAL context
* capabilities
* distance attenuation model

```java
AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
```

This enables proper distance-based volume attenuation for world sounds.

---

# Playing Sounds

## Simple local playback

```java
GameSound sound = JGemsHelper.getSoundManager()
    .playLocalSound(
        soundBuffer,
        SoundType.BACKGROUND_SOUND,
        1.0f,
        1.0f
    );
```

Used for:

* UI sounds
* simple music
* notifications

No world position is used.

---

## Positional 3D sound

```java
GameSound sound = JGemsHelper.getSoundManager()
    .playSoundAt(
        soundBuffer,
        SoundType.WORLD_SOUND,
        1.0f,
        1.0f,
        1.0f,
        16.0f,
        new Vector3f(10, 0, 5)
    );
```

Parameters:

* pitch
* volume
* rollOff
* max distance
* world position

Used for:

* weapon sounds
* environmental effects
* gameplay feedback

---

## Sound attached to entity

```java
GameSound sound = JGemsHelper.getSoundManager()
    .playSoundAtEntity(
        soundBuffer,
        SoundType.WORLD_AMBIENT_SOUND,
        1.0f,
        1.0f,
        1.0f,
        20.0f,
        worldItem
    );
```

The sound automatically follows the entity position.

Used for:

* vehicles
* NPCs
* machines
* moving objects

---

# Pause / Resume System

Useful for:

* pause menu
* alt-tab
* editor mode switching

Pause:

```java
JGemsHelper.getSoundManager().pauseAllSounds();
```

Resume:

```java
JGemsHelper.getSoundManager().resumeAllSounds();
```

Important:

`SYSTEM` sounds are excluded from automatic pause.

This is intentional.

---

# Manual Sound Creation

If needed, sound objects can be created manually:

```java
GameSound sound = JGemsHelper.getSoundManager()
    .createSound(
        soundBuffer,
        SoundType.WORLD_SOUND,
        1.0f,
        1.0f,
        1.0f,
        15.0f
    );
```

This allows delayed playback or custom runtime control.

---

# Runtime Updates

Every active sound is updated automatically during engine runtime:

```java
soundManager.update();
```

This handles:

* attached entity tracking
* source cleanup
* stopped sound removal
* validation checks

Usually developers do not need to call this manually.

---

# Error Handling

The engine validates OpenAL state using:

```java
JGemsSoundManager.checkALonErrors();
```

It checks:

* invalid source IDs
* invalid enum usage
* invalid values
* invalid operations
* out-of-memory situations

This helps catch audio bugs early during development.

---

# Resource Cleanup

On shutdown:

```java
soundManager.stopAllSounds();
soundManager.destroy();
```

This properly releases:

* sources
* buffers
* OpenAL context
* OpenAL device

Never skip cleanup in production builds.

---