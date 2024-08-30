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

package javagems3d.engine_api.app.tbox.containers;

import org.jetbrains.annotations.NotNull;
import javagems3d.toolbox.map_table.object.AbstractObjectData;

public final class TObjectData {
    private final AbstractObjectData abstractObjectData;

    public TObjectData(@NotNull AbstractObjectData abstractObjectData) {
        this.abstractObjectData = abstractObjectData;
    }

    public AbstractObjectData getAbstractObjectData() {
        return this.abstractObjectData;
    }
}
