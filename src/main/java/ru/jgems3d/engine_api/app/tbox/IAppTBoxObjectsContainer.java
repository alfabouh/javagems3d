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

package ru.jgems3d.engine_api.app.tbox;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;

public interface IAppTBoxObjectsContainer {
    /**
     * @param id object's id
     * @param tEntityContainer This is necessary to configure the object information inside the
     *                         ToolBox system (contains information about the render and attributes
     *                         of the object)
     *                         <br><br>
     * @param tRenderContainer Contains information about the rendering of an object inside
     *                         the main JavaGems3D engine
     */
    void addObject(@NotNull String id, @NotNull TEntityContainer tEntityContainer, @Nullable TRenderContainer tRenderContainer);
}
