package ru.alfabouh.engine.game.resources.assets;

import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.audio.sound.SoundBuffer;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.cache.GameCache;

public class SoundAssetsLoader implements IAssetsLoader {
    public SoundBuffer zippo_o;
    public SoundBuffer zippo_c;
    public SoundBuffer map_ambience1;
    public SoundBuffer saw;
    public SoundBuffer beep;
    public SoundBuffer pick;
    public SoundBuffer music;
    public SoundBuffer door;
    public SoundBuffer wood_break;
    public SoundBuffer turn;
    public SoundBuffer soda;
    public SoundBuffer horror;
    public SoundBuffer horror2;
    public SoundBuffer button;
    public SoundBuffer noise;
    public SoundBuffer crackling;
    public SoundBuffer creepy;
    public SoundBuffer drips;
    public SoundBuffer meat;
    public SoundBuffer victory;
    public SoundBuffer menu;
    public SoundBuffer[] pl_step;
    public SoundBuffer[] pl_slosh;

    @Override
    public void load(GameCache gameCache) {
        this.pl_step = new SoundBuffer[4];
        this.pl_slosh = new SoundBuffer[4];

        this.map_ambience1 = ResourceManager.createSoundBuffer("valley_night.wav", AL10.AL_FORMAT_MONO16);
        this.saw = ResourceManager.createSoundBuffer("saw.wav", AL10.AL_FORMAT_MONO16);
        this.zippo_o = ResourceManager.createSoundBuffer("zippo_o.wav", AL10.AL_FORMAT_MONO16);
        this.zippo_c = ResourceManager.createSoundBuffer("zippo_c.wav", AL10.AL_FORMAT_MONO16);
        this.beep = ResourceManager.createSoundBuffer("beep.wav", AL10.AL_FORMAT_MONO16);
        this.pick = ResourceManager.createSoundBuffer("pick.wav", AL10.AL_FORMAT_MONO16);
        this.turn = ResourceManager.createSoundBuffer("turn.wav", AL10.AL_FORMAT_MONO16);
        this.music = ResourceManager.createSoundBuffer("music.wav", AL10.AL_FORMAT_MONO16);
        this.soda = ResourceManager.createSoundBuffer("soda.wav", AL10.AL_FORMAT_MONO16);
        this.door = ResourceManager.createSoundBuffer("door.wav", AL10.AL_FORMAT_MONO16);
        this.meat = ResourceManager.createSoundBuffer("meat.wav", AL10.AL_FORMAT_MONO16);
        this.noise = ResourceManager.createSoundBuffer("noise.wav", AL10.AL_FORMAT_MONO16);
        this.horror = ResourceManager.createSoundBuffer("horror.wav", AL10.AL_FORMAT_MONO16);
        this.horror2 = ResourceManager.createSoundBuffer("horror2.wav", AL10.AL_FORMAT_MONO16);
        this.victory = ResourceManager.createSoundBuffer("victory.wav", AL10.AL_FORMAT_MONO16);
        this.crackling = ResourceManager.createSoundBuffer("crackling.wav", AL10.AL_FORMAT_MONO16);
        this.creepy = ResourceManager.createSoundBuffer("creepy.wav", AL10.AL_FORMAT_MONO16);
        this.wood_break = ResourceManager.createSoundBuffer("wood_break.wav", AL10.AL_FORMAT_MONO16);
        this.drips = ResourceManager.createSoundBuffer("drips.wav", AL10.AL_FORMAT_MONO16);
        this.button = ResourceManager.createSoundBuffer("button.wav", AL10.AL_FORMAT_MONO16);
        this.menu = ResourceManager.createSoundBuffer("menu.wav", AL10.AL_FORMAT_MONO16);

        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = ResourceManager.createSoundBuffer("pl_step" + (i + 1) + ".wav", AL10.AL_FORMAT_STEREO16);
            this.pl_slosh[i] = ResourceManager.createSoundBuffer("pl_slosh" + (i + 1) + ".wav", AL10.AL_FORMAT_STEREO16);
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
