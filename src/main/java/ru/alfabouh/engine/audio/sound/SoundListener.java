package ru.alfabouh.engine.audio.sound;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryUtil;
import ru.alfabouh.engine.audio.SoundManager;
import ru.alfabouh.engine.JGems;

public class SoundListener {
    public static void updateOrientationAndPosition(Matrix4d cameraMatrix, Vector3d position) {
        if (ALC10.alcGetCurrentContext() == MemoryUtil.NULL) {
            return;
        }
        AL10.alListener3f(AL10.AL_POSITION, (float) position.x, (float) position.y, (float) position.z);
        Vector3d at = new Vector3d();
        cameraMatrix.positiveZ(at).negate();
        Vector3d up = new Vector3d();
        cameraMatrix.positiveY(up);
        float[] data = new float[]{(float) at.x, (float) at.y, (float) at.z, (float) up.x, (float) up.y, (float) up.z};
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
        SoundManager.checkALonErrors();
    }

    public static void updateListenerGain() {
        AL10.alListenerf(AL10.AL_GAIN, JGems.get().getGameSettings().soundGain.getValue());
    }
}
