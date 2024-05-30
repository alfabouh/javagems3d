package ru.alfabouh.engine.inventory.items;

import org.joml.Vector3d;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.inventory.IHasInventory;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.environment.light.PointLight;

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

    public void onAddInInventory(IHasInventory hasInventory) {
        super.onAddInInventory(hasInventory);
        this.pointLight = (PointLight) new PointLight().setLightColor(new Vector3d(1.0d, 0.475f, 0.375f));
        pointLight.setBrightness(4.0f);
        JGems.get().getProxy().addPointLight((WorldItem) this.itemOwner(), pointLight, 0);
        this.pointLight.setEnabled(false);
    }

    private void close() {
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.zippo_c, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(false);
        this.isOpened = false;
    }

    private void open() {
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.zippo_o, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(true);
        this.isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }
}
