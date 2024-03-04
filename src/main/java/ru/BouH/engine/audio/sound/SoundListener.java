package ru.BouH.engine.audio.sound;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.openal.AL10;
import ru.BouH.engine.audio.SoundManager;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;

public class SoundListener {
    private final SoundManager soundManager;

    public SoundListener(SoundManager soundManager) {
        Game.getGame().getLogManager().log("Initializing sound listener!");
        this.soundManager = soundManager;
        this.setPosition(new Vector3d(0.0d));
        this.setVelocity(new Vector3d(0.0d));
        float[] data = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
    }

    public void updateOrientationAndPosition(Matrix4d cameraMatrix, Vector3d position) {
        if (!this.getSoundManager().isSystemCreated()) {
            throw new GameException("Sound system is not valid!");
        }
        this.setPosition(position);
        Vector3d at = new Vector3d();
        cameraMatrix.positiveZ(at).negate();
        Vector3d up = new Vector3d();
        cameraMatrix.positiveY(up);
        float[] data = new float[] {(float) at.x, (float) at.y, (float) at.z, (float) up.x, (float) up.y, (float) up.z};
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
        SoundManager.checkALonErrors();
    }

    public void setPosition(Vector3d position) {
        AL10.alListener3f(AL10.AL_POSITION, (float) position.x, (float) position.y, (float) position.z);
    }

    public void setVelocity(Vector3d velocity) {
        AL10.alListener3f(AL10.AL_VELOCITY, (float) velocity.x, (float) velocity.y, (float) velocity.z);
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }
}
