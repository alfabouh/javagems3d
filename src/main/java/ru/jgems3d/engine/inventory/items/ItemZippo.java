package ru.jgems3d.engine.inventory.items;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.audio.sound.data.SoundType;
import ru.jgems3d.engine.inventory.IInventoryOwner;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.environment.light.PointLight;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class ItemZippo extends InventoryItem {
    private int openCd;
    private boolean isOpened;
    private PointLight pointLight;

    public ItemZippo() {
        super("zippo");
        this.openCd = 0;
        this.setDescription(JGems.get().I18n("item.description.zippo"));
    }

    @Override
    public void onLeftClick(IWorld world) {
        if (this.openCd <= 0) {
            if (this.isOpened) {
                this.close();
            } else {
                this.open();
            }
            this.openCd = 40;
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
                this.pointLight.setEnabled(false);
            }
        } else {
            if (this.isOpened()) {
                this.pointLight.setEnabled(true);
            }
        }
    }

    public void onAddInInventory(IInventoryOwner hasInventory) {
        super.onAddInInventory(hasInventory);
        this.pointLight = (PointLight) new PointLight().setLightColor(new Vector3f(1.0f, 0.475f, 0.375f));
        pointLight.setBrightness(4.0f);
        JGemsHelper.addPointLight((WorldItem) this.itemOwner(), pointLight, 0);
        this.pointLight.setEnabled(false);
    }

    private void close() {
        JGems.get().getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssetsLoader.zippo_c, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(false);
        this.isOpened = false;
    }

    private void open() {
        JGems.get().getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssetsLoader.zippo_o, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(true);
        this.isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }
}
