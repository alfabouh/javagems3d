package ru.alfabouh.engine.audio.sound.wave;

import com.sun.media.sound.WaveFileReader;
import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class OldJGLWaveData {
	public final ByteBuffer data;
	public final int format;
	public final int samplerate;

	private OldJGLWaveData(ByteBuffer data, int format, int samplerate) {
		this.data = data;
		this.format = format;
		this.samplerate = samplerate;
	}

	public void dispose() {
		this.data.clear();
	}

	public static OldJGLWaveData create(URL path) {
		try {
			WaveFileReader wfr = new WaveFileReader();
			return OldJGLWaveData.create(wfr.getAudioInputStream(new BufferedInputStream(path.openStream())));
		} catch (Exception e) {
			Game.getGame().getLogManager().warn("Unable to create from: " + path + ", " + e.getMessage());
			return null;
		}		
	}

	public static OldJGLWaveData create(String path) {
		return create(Thread.currentThread().getContextClassLoader().getResource(path));
	}

	public static OldJGLWaveData create(InputStream is) {
		try {
			return OldJGLWaveData.create(AudioSystem.getAudioInputStream(is));
		} catch (Exception e) {
			Game.getGame().getLogManager().warn("Unable to create from inputstream, " + e.getMessage());
			return null;
		}		
	}	

	public static OldJGLWaveData create(byte[] buffer) {
		try {
			return OldJGLWaveData.create(AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer))));
		} catch (Exception e) {
			Game.getGame().getLogManager().warn("Unable to create from byte array, " + e.getMessage());
			return null;
		}
	}

	public static OldJGLWaveData create(ByteBuffer buffer) {
		try {
			byte[] bytes = null;
			
			if(buffer.hasArray()) {
				bytes = buffer.array();
			} else {
				bytes = new byte[buffer.capacity()];
				buffer.get(bytes);
			}
			return create(bytes);
		} catch (Exception e) {
			Game.getGame().getLogManager().warn("Unable to create from ByteBuffer, " + e.getMessage());
			return null;
		}
	}	

	public static OldJGLWaveData create(AudioInputStream ais) {
		AudioFormat audioformat = ais.getFormat();

		int channels = 0;
		if (audioformat.getChannels() == 1) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_MONO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_MONO16;
			} else {
				throw new GameException("Illegal sample size");
			}
		} else if (audioformat.getChannels() == 2) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_STEREO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_STEREO16;
			} else {
				throw new GameException("Illegal sample size");
			}
		} else {
			throw new GameException("Only mono or stereo is supported");
		}

		ByteBuffer buffer;
		try {
			byte[] buf = new byte[ais.available()];
			int read = 0, total = 0;
			while ((read = ais.read(buf, total, buf.length - total)) != -1
				&& total < buf.length) {
				total += read;
			}
			buffer = convertAudioBytes(buf, audioformat.getSampleSizeInBits() == 16, audioformat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		} catch (IOException ioe) {
			return null;
		}

		OldJGLWaveData wavedata = new OldJGLWaveData(buffer, channels, (int) audioformat.getSampleRate());

		try {
			ais.close();
		} catch (IOException ioe) {
		}

		return wavedata;
	}

	private static ByteBuffer convertAudioBytes(byte[] audio_bytes, boolean two_bytes_data, ByteOrder order) {
		ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
		dest.order(ByteOrder.nativeOrder());
		ByteBuffer src = ByteBuffer.wrap(audio_bytes);
		src.order(order);
		if (two_bytes_data) {
			ShortBuffer dest_short = dest.asShortBuffer();
			ShortBuffer src_short = src.asShortBuffer();
			while (src_short.hasRemaining()) {
				dest_short.put(src_short.get());
			}
		} else {
			while (src.hasRemaining()) {
				dest.put(src.get());
			}
		}
		dest.rewind();
		return dest;
	}
}