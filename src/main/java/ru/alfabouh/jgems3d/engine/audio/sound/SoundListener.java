package ru.alfabouh.jgems3d.engine.audio.sound;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryUtil;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.SoundManager;

public class SoundListener {
    public static void updateOrientationAndPosition(Matrix4f cameraMatrix, Vector3f position) {
        if (ALC10.alcGetCurrentContext() == MemoryUtil.NULL) {
            return;
        }
        AL10.alListener3f(AL10.AL_POSITION, (float) position.x, (float) position.y, (float) position.z);
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        float[] data = new float[]{(float) at.x, (float) at.y, (float) at.z, (float) up.x, (float) up.y, (float) up.z};
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
        SoundManager.checkALonErrors();
    }

    public static void updateListenerGain() {
        AL10.alListenerf(AL10.AL_GAIN, JGems.get().getGameSettings().soundGain.getValue());
    }
}
