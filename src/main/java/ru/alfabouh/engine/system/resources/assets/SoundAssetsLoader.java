package ru.alfabouh.engine.system.resources.assets;

import org.lwjgl.openal.AL10;
import ru.alfabouh.engine.audio.sound.SoundBuffer;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.cache.GameCache;

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
    public SoundBuffer crackling;
    public SoundBuffer glitch;
    public SoundBuffer drips;
    public SoundBuffer meat;
    public SoundBuffer victory;
    public SoundBuffer menu;
    public SoundBuffer en_steps;
    public SoundBuffer[] pl_step;
    public SoundBuffer[] pl_slosh;

    @Override
    public void load(GameCache gameCache) {
        this.pl_step = new SoundBuffer[4];
        this.pl_slosh = new SoundBuffer[4];

        this.glitch = ResourceManager.createSoundBuffer("fx/glitch.ogg", AL10.AL_FORMAT_MONO16);
        this.map_ambience1 = ResourceManager.createSoundBuffer("ambient/valley_night.ogg", AL10.AL_FORMAT_MONO16);
        this.saw = ResourceManager.createSoundBuffer("enemy/saw.ogg", AL10.AL_FORMAT_MONO16);
        this.zippo_o = ResourceManager.createSoundBuffer("ui/zippo_o.ogg", AL10.AL_FORMAT_MONO16);
        this.zippo_c = ResourceManager.createSoundBuffer("ui/zippo_c.ogg", AL10.AL_FORMAT_MONO16);
        this.beep = ResourceManager.createSoundBuffer("ui/beep.ogg", AL10.AL_FORMAT_MONO16);
        this.pick = ResourceManager.createSoundBuffer("player/pick.ogg", AL10.AL_FORMAT_MONO16);
        this.turn = ResourceManager.createSoundBuffer("ui/turn.ogg", AL10.AL_FORMAT_MONO16);
        this.music = ResourceManager.createSoundBuffer("ambient/music.ogg", AL10.AL_FORMAT_MONO16);
        this.soda = ResourceManager.createSoundBuffer("player/soda.ogg", AL10.AL_FORMAT_MONO16);
        this.door = ResourceManager.createSoundBuffer("environment/door.ogg", AL10.AL_FORMAT_MONO16);
        this.meat = ResourceManager.createSoundBuffer("fx/meat.ogg", AL10.AL_FORMAT_MONO16);
        this.horror = ResourceManager.createSoundBuffer("ambient/horror.ogg", AL10.AL_FORMAT_MONO16);
        this.horror2 = ResourceManager.createSoundBuffer("ambient/horror2.ogg", AL10.AL_FORMAT_MONO16);
        this.victory = ResourceManager.createSoundBuffer("fx/victory.ogg", AL10.AL_FORMAT_MONO16);
        this.crackling = ResourceManager.createSoundBuffer("fx/crackling.ogg", AL10.AL_FORMAT_MONO16);
        this.wood_break = ResourceManager.createSoundBuffer("environment/wood_break.ogg", AL10.AL_FORMAT_MONO16);
        this.drips = ResourceManager.createSoundBuffer("ambient/drips.ogg", AL10.AL_FORMAT_MONO16);
        this.button = ResourceManager.createSoundBuffer("ui/button.ogg", AL10.AL_FORMAT_MONO16);
        this.menu = ResourceManager.createSoundBuffer("ui/menu.ogg", AL10.AL_FORMAT_MONO16);
        this.en_steps = ResourceManager.createSoundBuffer("enemy/en_steps.ogg", AL10.AL_FORMAT_MONO16);

        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = ResourceManager.createSoundBuffer("player/pl_step" + (i + 1) + ".ogg", AL10.AL_FORMAT_STEREO16);
            this.pl_slosh[i] = ResourceManager.createSoundBuffer("player/pl_slosh" + (i + 1) + ".ogg", AL10.AL_FORMAT_STEREO16);
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
