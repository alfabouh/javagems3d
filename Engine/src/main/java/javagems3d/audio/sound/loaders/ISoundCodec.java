package javagems3d.audio.sound.loaders;

import java.nio.Buffer;

public interface ISoundCodec {
    int getSampleRate();

    Buffer getPcm();

    int getPose();
}
