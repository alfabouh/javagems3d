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

package ru.jgems3d.engine.system.resources.assets.loaders;

import org.lwjgl.openal.AL10;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.audio.sound.SoundBuffer;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.service.path.JGemsPath;

public class SoundAssetsLoader implements IAssetsLoader {
    public SoundBuffer zippo_o;
    public SoundBuffer zippo_c;
    public SoundBuffer pick;
    public SoundBuffer button;

    public SoundBuffer[] pl_step;

    @Override
    public void load(GameResources gameResources) {
        this.pl_step = new SoundBuffer[4];

        this.zippo_o = gameResources.createSoundBuffer(new JGemsPath(JGems3D.Paths.SOUNDS, "ui/zippo_o.ogg"), AL10.AL_FORMAT_MONO16);
        this.zippo_c = gameResources.createSoundBuffer(new JGemsPath(JGems3D.Paths.SOUNDS, "ui/zippo_c.ogg"), AL10.AL_FORMAT_MONO16);
        this.pick = gameResources.createSoundBuffer(new JGemsPath(JGems3D.Paths.SOUNDS, "player/pick.ogg"), AL10.AL_FORMAT_MONO16);
        this.button = gameResources.createSoundBuffer(new JGemsPath(JGems3D.Paths.SOUNDS, "ui/button.ogg"), AL10.AL_FORMAT_MONO16);

        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = gameResources.createSoundBuffer(new JGemsPath(JGems3D.Paths.SOUNDS, "player/pl_step" + (i + 1) + ".ogg"), AL10.AL_FORMAT_STEREO16);
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
