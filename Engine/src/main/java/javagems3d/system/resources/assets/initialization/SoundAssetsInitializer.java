package javagems3d.system.resources.assets.initialization;

import org.lwjgl.openal.AL10;
import javagems3d.JGems3D;
import javagems3d.audio.sound.SoundBuffer;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;

public class SoundAssetsInitializer implements IAssetsInitializer {
    public SoundBuffer zippo_o;
    public SoundBuffer zippo_c;
    public SoundBuffer pick;
    public SoundBuffer button;

    public SoundBuffer[] pl_step;

    @Override
    public void load(SystemResources systemResources) {
        this.pl_step = new SoundBuffer[4];

        this.zippo_o = systemResources.createSoundBuffer(JGems3D.GetSource.JAR, new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "ui/zippo_o.ogg"), AL10.AL_FORMAT_MONO16);
        this.zippo_c = systemResources.createSoundBuffer(JGems3D.GetSource.JAR, new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "ui/zippo_c.ogg"), AL10.AL_FORMAT_MONO16);
        this.pick = systemResources.createSoundBuffer(JGems3D.GetSource.JAR, new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "player/pick.ogg"), AL10.AL_FORMAT_MONO16);
        this.button = systemResources.createSoundBuffer(JGems3D.GetSource.JAR, new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "ui/button.ogg"), AL10.AL_FORMAT_MONO16);

        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = systemResources.createSoundBuffer(JGems3D.GetSource.JAR, new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "player/pl_step" + (i + 1) + ".ogg"), AL10.AL_FORMAT_STEREO16);
        }
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.ASYNC;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}
