package ru.BouH.engine.game.resources.assets;

import org.lwjgl.openal.AL10;
import ru.BouH.engine.audio.sound.SoundBuffer;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.cache.GameCache;

public class SoundAssetsLoader implements IAssetsLoader {
    public SoundBuffer map_ambience1;
    public SoundBuffer[] pl_step;

    @Override
    public void load(GameCache gameCache) {
        this.pl_step = new SoundBuffer[4];
        this.map_ambience1 = ResourceManager.createSoundBuffer("valley_night.wav", AL10.AL_FORMAT_MONO16);
        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = ResourceManager.createSoundBuffer("pl_step" + (i + 1) + ".wav", AL10.AL_FORMAT_STEREO16);
        }
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.PARALLEL;
    }

    @Override
    public int loadOrder() {
        return 0;
    }
}
