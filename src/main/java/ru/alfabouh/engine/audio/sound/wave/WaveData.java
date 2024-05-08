package ru.alfabouh.engine.audio.sound.wave;

import javax.sound.sampled.*;
import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.game.Game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class WaveData {
    public final ByteBuffer data;
    public final int format;
    public final int samplerate;

    private WaveData(ByteBuffer data, int format, int samplerate) {
        this.data = data;
        this.format = format;
        this.samplerate = samplerate;
    }

    public static WaveData create(URL path) {
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(path)) {
            return WaveData.create(inputStream);
        } catch (Exception e) {
            Game.getGame().getLogManager().warn("Unable to create from: " + path + ", " + e.getMessage());
            return null;
        }
    }

    public static WaveData create(String path) {
        return create(Thread.currentThread().getContextClassLoader().getResource(path));
    }

    public static WaveData create(InputStream is) {
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(is)) {
            return WaveData.create(inputStream);
        } catch (Exception e) {
            Game.getGame().getLogManager().warn("Unable to create from inputstream, " + e.getMessage());
            return null;
        }
    }

    public static WaveData create(byte[] buffer) {
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer))) {
            return WaveData.create(inputStream);
        } catch (Exception e) {
            Game.getGame().getLogManager().warn("Unable to create from byte array, " + e.getMessage());
            return null;
        }
    }

    public static WaveData create(ByteBuffer buffer) {
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer.array()))) {
            return WaveData.create(inputStream);
        } catch (Exception e) {
            Game.getGame().getLogManager().warn("Unable to create from ByteBuffer, " + e.getMessage());
            return null;
        }
    }

    public static WaveData create(AudioInputStream ais) {
        AudioFormat audioFormat = ais.getFormat();

        int channels;
        if (audioFormat.getChannels() == 1) {
            if (audioFormat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_MONO8;
            } else if (audioFormat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_MONO16;
            } else {
                assert false : "Illegal sample size";
                return null;
            }
        } else if (audioFormat.getChannels() == 2) {
            if (audioFormat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_STEREO8;
            } else if (audioFormat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_STEREO16;
            } else {
                assert false : "Illegal sample size";
                return null;
            }
        } else {
            assert false : "Only mono or stereo is supported";
            return null;
        }

        ByteBuffer buffer;
        try {
            byte[] audioBytes = new byte[ais.available()];
            ais.read(audioBytes);
            buffer = convertAudioBytes(audioBytes, audioFormat.getSampleSizeInBits() == 16, audioFormat.isBigEndian());
        } catch (IOException e) {
            Game.getGame().getLogManager().warn("Unable to read audio input stream, " + e.getMessage());
            return null;
        }

        WaveData WaveData = new WaveData(buffer, channels, (int) audioFormat.getSampleRate());

        return WaveData;
    }

    private static ByteBuffer convertAudioBytes(byte[] audioBytes, boolean twoBytesData, boolean bigEndian) {
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