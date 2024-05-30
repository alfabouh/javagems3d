package ru.alfabouh.engine.inventory.items;

import ru.alfabouh.engine.audio.sound.GameSound;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.physics.world.IWorld;

public class ItemRadio extends InventoryItem {
    private final GameSound music;
    private int openCd;
    private boolean isOpened;

    public ItemRadio() {
        super("radio");
        this.openCd = 0;
        this.music = JGems.get().getSoundManager().createSound(ResourceManager.soundAssetsLoader.music, SoundType.BACKGROUND_AMBIENT_SOUND, 2.0f, 1.0f, 1.0f);
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
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.turn, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
        this.music.pauseSound();
        this.isOpened = false;
    }

    private void open() {
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.turn, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
        this.music.playSound();
        this.isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }
}
