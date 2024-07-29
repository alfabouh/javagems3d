package ru.jgems3d.engine.audio.sound.loaders;

import java.nio.Buffer;

public interface ISoundLoader {
    int getSampleRate();

    Buffer getPcm();

    int getFormat();
}
