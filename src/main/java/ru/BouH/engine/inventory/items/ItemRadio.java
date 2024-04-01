package ru.BouH.engine.inventory.items;

import ru.BouH.engine.audio.sound.GameSound;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.world.IWorld;

public class ItemRadio extends InventoryItem {
    private final GameSound music;
    private int openCd;
    private boolean isOpened;

    public ItemRadio() {
        super("radio");
        this.openCd = 0;
        this.music = Game.getGame().getSoundManager().createSound(ResourceManager.soundAssetsLoader.music, SoundType.BACKGROUND_AMBIENT_SOUND, 2.0f, 1.0f, 1.0f);
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
