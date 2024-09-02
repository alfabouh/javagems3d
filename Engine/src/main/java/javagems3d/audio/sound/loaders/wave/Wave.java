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

package javagems3d.audio.sound.loaders.wave;

import org.lwjgl.openal.AL10;
import javagems3d.JGemsHelper;
import javagems3d.audio.sound.loaders.ISoundLoader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class Wave implements ISoundLoader {
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
            JGemsHelper.getLogger().warn("Unable to read audio input stream, " + e.getMessage());
        }

        this.data = buffer;
        this.format = channels;
        this.samplerate = (int) audioFormat.getSampleRate();
    }

    public static ISoundLoader create(InputStream is) {
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(is)) {
            return new Wave(inputStream);
        } catch (Exception e) {
            JGemsHelper.getLogger().warn("Unable to create from inputstream, " + e.getMessage());
            return null;
        }
    }

    public int getSampleRate() {
        return this.samplerate;
    }

    public ByteBuffer getPcm() {
        return this.data;
    }

    public int getFormat() {
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