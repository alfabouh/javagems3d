package ru.BouH.engine.audio.sound.ogg;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class OggData {
    private final ShortBuffer pcm;
    private int format;
    private final int sampleRate;

    private OggData(String path) {
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            try {
                this.pcm = this.readOGG(path, info);
                this.sampleRate = info.sample_rate();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public static OggData create(String path) {
        return new OggData(path);
    }

    public ShortBuffer getPcm() {
        return this.pcm;
    }

    public int getFormat() {
        return this.format;
    }

    private ShortBuffer readOGG(String path, STBVorbisInfo info) throws IOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            InputStream stream = Game.loadFileJar(path);
            byte[] buffer = new byte[stream.available()];
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(buffer.length);
            byteBuffer.put(buffer);
            byteBuffer.flip();

            IntBuffer error = stack.mallocInt(1);
            long decoder = STBVorbis.stb_vorbis_open_memory(byteBuffer, error, null);
            if (decoder == MemoryUtil.NULL) {
                throw new GameException("Failed to open Ogg sound. Error code: " + error.get(0));
            }

            STBVorbis.stb_vorbis_get_info(decoder, info);
            int channels = info.channels();

            this.format = channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;

            int samples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
            ShortBuffer res = MemoryUtil.memAllocShort(samples * channels);

            res.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, res) * channels);
            STBVorbis.stb_vorbis_close(decoder);

            return res;
        }
    }
}
