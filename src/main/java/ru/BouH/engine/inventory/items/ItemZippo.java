package ru.BouH.engine.inventory.items;

import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.inventory.IHasInventory;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.environment.light.PointLight;

public class ItemZippo extends InventoryItem {
    private int openCd;
    private boolean isOpened;
    private PointLight pointLight;

    public ItemZippo() {
        super("zippo");
        this.openCd = 0;
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
                this.close();
            }
        }
    }

    public void onAddInInventory(IHasInventory hasInventory) {
        super.onAddInInventory(hasInventory);
        this.pointLight = (PointLight) new PointLight().setLightColor(new Vector3d(1.0d, 0.475f, 0.375f));
        pointLight.setBrightness(5.0f);
        Game.getGame().getProxy().addPointLight((WorldItem) this.itemOwner(), pointLight, 0);
        this.pointLight.setEnabled(false);
    }

    private void close() {
        Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.zippo_c, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(false);
        this.isOpened = false;
    }

    private void open() {
        Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.zippo_o, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(true);
        this.isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }
}
