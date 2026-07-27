/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.audio.loaders.ogg;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import javagems3d.audio.loaders.ISoundCodec;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Ogg implements ISoundCodec {
    private final ShortBuffer pcm;
    private final int sampleRate;
    private int format;

    private Ogg(InputStream stream, boolean forceMono) {
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            try {
                this.pcm = this.readOGG(stream, info, forceMono);
                this.sampleRate = info.sample_rate();
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
        }
    }

    public static ISoundCodec create(InputStream is, boolean forceMono) {
        return is == null ? null : new Ogg(is, forceMono);
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public ShortBuffer getPcm() {
        return this.pcm;
    }

    public int getPose() {
        return this.format;
    }

    private ShortBuffer readOGG(InputStream stream, STBVorbisInfo info, boolean forceMono) throws IOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            byte[] buffer = stream.readAllBytes();
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(buffer.length);
            byteBuffer.put(buffer);
            byteBuffer.flip();
            IntBuffer error = stack.mallocInt(1);
            long decoder = STBVorbis.stb_vorbis_open_memory(byteBuffer, error, null);
            if (decoder == MemoryUtil.NULL) {
                throw new JGemsRuntimeException("Failed to open Ogg sound. Error code: " + error.get(0));
            }
            STBVorbis.stb_vorbis_get_info(decoder, info);
            int channels = info.channels();
            int samples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
            ShortBuffer decodedBuffer = MemoryUtil.memAllocShort(samples * channels);
            int actualSamples = STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, decodedBuffer);
            decodedBuffer.limit(actualSamples * channels);
            STBVorbis.stb_vorbis_close(decoder);
            if (forceMono && channels == 2) {
                this.format = AL10.AL_FORMAT_MONO16;
                ShortBuffer monoBuffer = this.stereoToMono(decodedBuffer);
                MemoryUtil.memFree(decodedBuffer);
                return monoBuffer;
            }
            this.format = channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
            return decodedBuffer;
        }
    }

    private ShortBuffer stereoToMono(ShortBuffer stereoBuffer) {
        int monoSamples = stereoBuffer.limit() / 2;
        ShortBuffer monoBuffer = MemoryUtil.memAllocShort(monoSamples);

        for (int i = 0; i < monoSamples; i++) {
            short left = stereoBuffer.get(i * 2);
            short right = stereoBuffer.get(i * 2 + 1);

            int mixed = (left + right) / 2;
            monoBuffer.put(i, (short) mixed);
        }

        monoBuffer.limit(monoSamples);
        return monoBuffer;
    }

    public void dispose() {
        MemoryUtil.memFree(this.pcm);
    }
}