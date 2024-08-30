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

package ru.jgems3d.engine.physics.world.ai;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public interface IEntityAI<T extends WorldItem> {
    @NotNull
    State getState();

    int priority();

    T getAIOwner();

    void onStartAI(WorldItem worldItem);

    void onUpdateAI(WorldItem worldItem);

    void onEndAI(WorldItem worldItem);

    enum State {
        ENABLED,
        DISABLED
    }
}
