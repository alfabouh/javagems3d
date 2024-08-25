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

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.inventory.items.ItemZippo;

public class ItemZippoModded extends ItemZippo {
    private final float startFogDensity;

    public ItemZippoModded() {
        super();
        this.startFogDensity = JGemsHelper.getSceneWorld().getEnvironment().getFog().getDensity();
    }

    protected void close() {
        super.close();
        JGemsHelper.getSceneWorld().getEnvironment().getFog().setDensity(this.startFogDensity);
    }

    protected void open() {
        super.open();
        JGemsHelper.getSceneWorld().getEnvironment().getFog().setDensity(this.startFogDensity / 2.0f);
    }
}
