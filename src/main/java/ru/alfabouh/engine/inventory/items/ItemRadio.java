package ru.alfabouh.engine.inventory.items;

import ru.alfabouh.engine.audio.sound.GameSound;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.physics.world.IWorld;

public class ItemRadio extends InventoryItem {
    private final GameSound music;
    private int openCd;
    private boolean isOpened;

    public ItemRadio() {
        super("radio");
        this.openCd = 0;
        this.music = Game.getGame().getSoundManager().createSound(ResourceManager.soundAssetsLoader.music, SoundType.BACKGROUND_AMBIENT_SOUND, 2.0f, 1.0f, 1.0f);
        this.setDescription("[Calms the mind. Be careful]");
    }

    @Override
    public void onLeftClick(IWorld world) {
        if (this.openCd <= 0) {
            if (this.isOpened) {
                this.close();
            } else {
                this.open();
            }
            this.openCd = 20;
        }
    }

    @Override
    public void onRightClick(IWorld world) {
    }

    @Override
    public void onUpdate(IWorld world, boolean isCurrent) {
        this.openCd -= 1;
        if (!isCurrent) {
            if (this.isOpened()) {
                this.close();
            }
        }
    }

    private void close() {
        Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.turn, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
        this.music.pauseSound();
        this.isOpened = false;
    }

    private void open() {
        Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.turn, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
        this.music.playSound();
        this.isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }
}
