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

package jgems_api.horror.resources;

import org.lwjgl.openal.AL10;
import ru.jgems3d.engine.audio.sound.SoundBuffer;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.service.path.JGemsPath;


public class HorrorSoundsLoader implements IAssetsLoader {
    public SoundBuffer[] slosh = new SoundBuffer[4];
    public SoundBuffer beer;
    public SoundBuffer gas;
    public SoundBuffer magic;

    @Override
    public void load(GameResources gameResources) {
        this.magic = gameResources.createSoundBuffer(new JGemsPath("/assets/horror/sounds/magic.ogg"), AL10.AL_FORMAT_STEREO16);
        this.beer = gameResources.createSoundBuffer(new JGemsPath("/assets/horror/sounds/beer.ogg"), AL10.AL_FORMAT_STEREO16);
        this.gas = gameResources.createSoundBuffer(new JGemsPath("/assets/horror/sounds/gas.ogg"), AL10.AL_FORMAT_STEREO16);
        for (int i = 0; i < 4; i++) {
            this.slosh[i] = gameResources.createSoundBuffer(new JGemsPath("/assets/horror/sounds/pl_slosh" + (i + 1) + ".ogg"), AL10.AL_FORMAT_STEREO16);
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

