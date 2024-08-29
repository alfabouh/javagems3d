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

package jgems_api.horror.items;

import jgems_api.horror.HorrorGamePlayerState;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.system.inventory.items.ItemZippo;

public class ItemZippoModded extends ItemZippo {
    private final float startFogDensity;

    public ItemZippoModded() {
        super();
        this.startFogDensity = JGemsHelper.getSceneWorld().getEnvironment().getFog().getDensity();
    }

    protected float zippoBrightness() {
        return 7.5f;
    }

    @Override
    public void onUpdate(IWorld world, boolean isCurrent) {
        super.onUpdate(world, isCurrent);
        if (!this.isOpened() || !isCurrent) {
            JGemsHelper.getSceneWorld().getEnvironment().getFog().setDensity(this.startFogDensity);
        } else {
            if (HorrorGamePlayerState.zippoFluid > 0) {
                if (world.getTicks() % 30 == 0) {
                    HorrorGamePlayerState.zippoFluid -= 0.01f;
                }
                JGemsHelper.getSceneWorld().getEnvironment().getFog().setDensity(this.startFogDensity / 2.0f);
            } else {
                if (this.isOpened()) {
                    this.close();
                }
            }
        }
    }

    protected int openCdTicks() {
        return 20;
    }

    protected void close() {
        super.close();
    }

    protected void open() {
        if (HorrorGamePlayerState.zippoFluid > 0) {
            super.open();
        }
    }
}
