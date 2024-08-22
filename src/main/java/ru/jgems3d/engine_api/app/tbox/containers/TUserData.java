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

package ru.jgems3d.engine_api.app.tbox.containers;

import org.jetbrains.annotations.NotNull;

public final class TUserData {
    private final Object userData;

    public TUserData(@NotNull Object userData) {
        this.userData = userData;
    }

    @SuppressWarnings("unchecked")
    public <T> T tryCastObject(Class<T> tClass) {
        if (!this.getUserData().getClass().isAssignableFrom(tClass)) {
            return null;
        }
        return (T) this.getUserData();
    }

    public Object getUserData() {
        return this.userData;
    }
}
