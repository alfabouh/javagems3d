package ru.alfabouh.jgems3d.engine.system.resources.assets.loaders;

import org.lwjgl.openal.AL10;
import ru.alfabouh.jgems3d.engine.audio.sound.SoundBuffer;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.alfabouh.jgems3d.engine.system.resources.manager.objects.GameResources;

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
    public void load(GameResources gameResources) {
        this.pl_step = new SoundBuffer[4];
        this.pl_slosh = new SoundBuffer[4];

        this.glitch = gameResources.createSoundBuffer("fx/glitch.ogg", AL10.AL_FORMAT_MONO16);
        this.map_ambience1 = gameResources.createSoundBuffer("ambient/valley_night.ogg", AL10.AL_FORMAT_MONO16);
        this.saw = gameResources.createSoundBuffer("enemy/saw.ogg", AL10.AL_FORMAT_MONO16);
        this.zippo_o = gameResources.createSoundBuffer("ui/zippo_o.ogg", AL10.AL_FORMAT_MONO16);
        this.zippo_c = gameResources.createSoundBuffer("ui/zippo_c.ogg", AL10.AL_FORMAT_MONO16);
        this.beep = gameResources.createSoundBuffer("ui/beep.ogg", AL10.AL_FORMAT_MONO16);
        this.pick = gameResources.createSoundBuffer("player/pick.ogg", AL10.AL_FORMAT_MONO16);
        this.turn = gameResources.createSoundBuffer("ui/turn.ogg", AL10.AL_FORMAT_MONO16);
        this.music = gameResources.createSoundBuffer("ambient/music.ogg", AL10.AL_FORMAT_MONO16);
        this.soda = gameResources.createSoundBuffer("player/soda.ogg", AL10.AL_FORMAT_MONO16);
        this.door = gameResources.createSoundBuffer("environment/door.ogg", AL10.AL_FORMAT_MONO16);
        this.meat = gameResources.createSoundBuffer("fx/meat.ogg", AL10.AL_FORMAT_MONO16);
        this.horror = gameResources.createSoundBuffer("ambient/horror.ogg", AL10.AL_FORMAT_MONO16);
        this.horror2 = gameResources.createSoundBuffer("ambient/horror2.ogg", AL10.AL_FORMAT_MONO16);
        this.victory = gameResources.createSoundBuffer("fx/victory.ogg", AL10.AL_FORMAT_MONO16);
        this.crackling = gameResources.createSoundBuffer("fx/crackling.ogg", AL10.AL_FORMAT_MONO16);
        this.wood_break = gameResources.createSoundBuffer("environment/wood_break.ogg", AL10.AL_FORMAT_MONO16);
        this.drips = gameResources.createSoundBuffer("ambient/drips.ogg", AL10.AL_FORMAT_MONO16);
        this.button = gameResources.createSoundBuffer("ui/button.ogg", AL10.AL_FORMAT_MONO16);
        this.menu = gameResources.createSoundBuffer("ui/menu.ogg", AL10.AL_FORMAT_MONO16);
        this.en_steps = gameResources.createSoundBuffer("enemy/en_steps.ogg", AL10.AL_FORMAT_MONO16);

        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = gameResources.createSoundBuffer("player/pl_step" + (i + 1) + ".ogg", AL10.AL_FORMAT_STEREO16);
            this.pl_slosh[i] = gameResources.createSoundBuffer("player/pl_slosh" + (i + 1) + ".ogg", AL10.AL_FORMAT_STEREO16);
        }
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.PARALLEL;
    }

    @Override
    public int loadOrder() {
        return -1;
    }
}
