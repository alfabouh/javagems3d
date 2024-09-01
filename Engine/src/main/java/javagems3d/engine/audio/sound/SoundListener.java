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

package javagems3d.engine.audio.sound;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryUtil;
import javagems3d.engine.JGems3D;
import javagems3d.engine.audio.SoundManager;

public class SoundListener {
    public static void updateOrientationAndPosition(Matrix4f cameraMatrix, Vector3f position) {
        if (ALC10.alcGetCurrentContext() == MemoryUtil.NULL) {
            return;
        }
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
        SoundManager.checkALonErrors();
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        float[] data = new float[]{at.x, at.y, at.z, up.x, up.y, up.z};
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
        SoundManager.checkALonErrors();
    }

    public static void updateListenerGain() {
        AL10.alListenerf(AL10.AL_GAIN, JGems3D.get().getGameSettings().soundGain.getValue());
    }
}
