/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.inventory.items;

import org.joml.Vector3f;
import javagems3d.engine.JGems3D;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.audio.sound.data.SoundType;
import javagems3d.engine.graphics.opengl.environment.light.PointLight;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.system.inventory.IInventoryOwner;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;

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
            this.openCd = this.openCdTicks();
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
        pointLight.setBrightness(this.zippoBrightness());
        JGemsHelper.WORLD.addPointLight((WorldItem) this.itemOwner(), pointLight, 0);
        this.pointLight.setEnabled(false);
    }

    public String getDescription() {
        return JGems3D.get().I18n("item.description.zippo");
    }

    protected float zippoBrightness() {
        return 5.25f;
    }

    protected int openCdTicks() {
        return 40;
    }

    protected void close() {
        JGems3D.get().getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssets.zippo_c, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(false);
        this.isOpened = false;
    }

    protected void open() {
        JGems3D.get().getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssets.zippo_o, SoundType.BACKGROUND_SOUND, 1.5f, 0.5f);
        this.pointLight.setEnabled(true);
        this.isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }
}
