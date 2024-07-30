package ru.jgems3d.engine.system.resources.assets.loaders;

import org.lwjgl.openal.AL10;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.audio.sound.SoundBuffer;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.manager.GameResources;

public class SoundAssetsLoader implements IAssetsLoader {
    public SoundBuffer zippo_o;
    public SoundBuffer zippo_c;
    public SoundBuffer pick;
    public SoundBuffer button;
    public SoundBuffer[] pl_step;
    public SoundBuffer[] pl_slosh;

    @Override
    public void load(GameResources gameResources) {
        this.pl_step = new SoundBuffer[4];
        this.pl_slosh = new SoundBuffer[4];

        this.zippo_o = gameResources.createSoundBuffer(new JGPath(JGems3D.Paths.SOUNDS, "ui/zippo_o.ogg"), AL10.AL_FORMAT_MONO16);
        this.zippo_c = gameResources.createSoundBuffer(new JGPath(JGems3D.Paths.SOUNDS, "ui/zippo_c.ogg"), AL10.AL_FORMAT_MONO16);
        this.pick = gameResources.createSoundBuffer(new JGPath(JGems3D.Paths.SOUNDS, "player/pick.ogg"), AL10.AL_FORMAT_MONO16);
        this.button = gameResources.createSoundBuffer(new JGPath(JGems3D.Paths.SOUNDS, "ui/button.ogg"), AL10.AL_FORMAT_MONO16);

        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = gameResources.createSoundBuffer(new JGPath(JGems3D.Paths.SOUNDS, "player/pl_step" + (i + 1) + ".ogg"), AL10.AL_FORMAT_STEREO16);
            this.pl_slosh[i] = gameResources.createSoundBuffer(new JGPath(JGems3D.Paths.SOUNDS, "player/pl_slosh" + (i + 1) + ".ogg"), AL10.AL_FORMAT_STEREO16);
        }
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.PARALLEL;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}
