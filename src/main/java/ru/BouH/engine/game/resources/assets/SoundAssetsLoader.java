package ru.BouH.engine.game.resources.assets;

import org.lwjgl.openal.AL10;
import ru.BouH.engine.audio.sound.SoundBuffer;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.cache.GameCache;

public class SoundAssetsLoader implements IAssetsLoader {
    public SoundBuffer zippo_o;
    public SoundBuffer zippo_c;
    public SoundBuffer map_ambience1;
    public SoundBuffer saw;
    public SoundBuffer beep;
    public SoundBuffer pick;
    public SoundBuffer music;
    public SoundBuffer turn;
    public SoundBuffer[] pl_step;

    @Override
    public void load(GameCache gameCache) {
        this.pl_step = new SoundBuffer[4];
        this.map_ambience1 = ResourceManager.createSoundBuffer("valley_night.wav", AL10.AL_FORMAT_MONO16);
        this.saw = ResourceManager.createSoundBuffer("saw.wav", AL10.AL_FORMAT_MONO16);
        this.zippo_o = ResourceManager.createSoundBuffer("zippo_o.wav", AL10.AL_FORMAT_MONO16);
        this.zippo_c = ResourceManager.createSoundBuffer("zippo_c.wav", AL10.AL_FORMAT_MONO16);
        this.beep = ResourceManager.createSoundBuffer("beep.wav", AL10.AL_FORMAT_MONO16);
        this.pick = ResourceManager.createSoundBuffer("pick.wav", AL10.AL_FORMAT_MONO16);
        this.turn = ResourceManager.createSoundBuffer("turn.wav", AL10.AL_FORMAT_MONO16);
        this.music = ResourceManager.createSoundBuffer("music.wav", AL10.AL_FORMAT_MONO16);

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
