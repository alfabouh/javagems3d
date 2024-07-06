package ru.alfabouh.jgems3d.engine.inventory.items;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.GameSound;
import ru.alfabouh.jgems3d.engine.audio.sound.data.SoundType;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class ItemRadio extends InventoryItem {
    private final GameSound music;
    private int openCd;
    private boolean isOpened;

    public ItemRadio() {
        super("radio");
        this.openCd = 0;
        this.music = JGems.get().getSoundManager().createSound(JGemsResourceManager.soundAssetsLoader.music, SoundType.BACKGROUND_AMBIENT_SOUND, 2.0f, 1.0f, 1.0f);
        this.setDescription(JGems.get().I18n("item.description.radio"));
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
        JGems.get().getSoundManager().playLocalSound(JGemsResourceManager.soundAssetsLoader.turn, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
        this.music.pauseSound();
        this.isOpened = false;
    }

    private void open() {
        JGems.get().getSoundManager().playLocalSound(JGemsResourceManager.soundAssetsLoader.turn, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
        this.music.playSound();
        this.isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }
}
