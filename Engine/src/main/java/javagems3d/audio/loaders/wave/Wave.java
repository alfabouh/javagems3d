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

package javagems3d.audio.loaders.wave;

import logger.Log;
import org.lwjgl.openal.AL10;
import javagems3d.audio.loaders.ISoundCodec;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class Wave implements ISoundCodec {
    public final ByteBuffer data;
    public final int format;
    public final int samplerate;

    private Wave(AudioInputStream ais) {
        AudioFormat audioFormat = ais.getFormat();

        int channels = this.getChannels(audioFormat);

        ByteBuffer buffer = null;
        try {
            byte[] audioBytes = new byte[ais.available()];
            ais.read(audioBytes);
            buffer = convertAudioBytes(audioBytes, audioFormat.getSampleSizeInBits() == 16, audioFormat.isBigEndian());
        } catch (IOException e) {
            Log.get().warn("Unable to read audio input stream, " + e.getMessage());
        }

        this.data = buffer;
        this.format = channels;
        this.samplerate = (int) audioFormat.getSampleRate();
    }

    public static ISoundCodec create(InputStream is) {
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(is)) {
            return new Wave(inputStream);
        } catch (Exception e) {
            Log.get().warn("Unable to create from inputstream, " + e.getMessage());
            return null;
        }
    }

    public int getSampleRate() {
        return this.samplerate;
    }

    public ByteBuffer getPcm() {
        return this.data;
    }

    public int getPose() {
        return this.format;
    }

    private int getChannels(AudioFormat audioFormat) {
        int channels = 0;
        if (audioFormat.getChannels() == 1) {
            if (audioFormat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_MONO8;
            } else if (audioFormat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_MONO16;
            } else {
                assert false : "Illegal sample size";
            }
        } else if (audioFormat.getChannels() == 2) {
            if (audioFormat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_STEREO8;
            } else if (audioFormat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_STEREO16;
            } else {
                assert false : "Illegal sample size";
            }
        } else {
            assert false : "Only mono or stereo is supported";
        }
        return channels;
    }

    private ByteBuffer convertAudioBytes(byte[] audioBytes, boolean twoBytesData, boolean bigEndian) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(audioBytes.length);
        byteBuffer.order(ByteOrder.nativeOrder());

        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        for (int i = 0; i < audioBytes.length / 2; i++) {
            int b1 = audioBytes[i * 2] & 0xff;
            int b2 = audioBytes[i * 2 + 1] & 0xff;
            short s = (short) (bigEndian ? (b1 << 8) | b2 : (b2 << 8) | b1);
            shortBuffer.put(s);
        }

        byteBuffer.position(0);
        return byteBuffer;
    }

    public void dispose() {
        this.data.clear();
    }
}