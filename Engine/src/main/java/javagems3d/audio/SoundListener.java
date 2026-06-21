package javagems3d.audio;

import javagems3d.system.settings.JGemsSettings;
import logger.Log;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryUtil;

public class SoundListener {
    public static void updateOrientationAndPosition(Matrix4f cameraMatrix, Vector3f position) {
        if (ALC10.alcGetCurrentContext() == MemoryUtil.NULL || ALC10.alcGetContextsDevice(ALC10.alcGetCurrentContext()) == 0) {
            return;
        }
        if (!position.isFinite() || !cameraMatrix.isFinite()) {
            Log.get().warn("Tried to update sound listener, invalid data!");
            return;
        }
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
        JGemsSoundManager.checkALonErrors();
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        float[] data = new float[]{at.x, at.y, at.z, up.x, up.y, up.z};
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
        JGemsSoundManager.checkALonErrors();
    }

    public static void updateListenerGain(JGemsSettings settings) {
        AL10.alListenerf(AL10.AL_GAIN, settings.soundGain.getValue());
    }
}
