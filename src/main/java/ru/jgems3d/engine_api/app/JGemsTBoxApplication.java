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

package ru.jgems3d.engine_api.app;

import ru.jgems3d.engine_api.app.tbox.IAppTBoxObjectsContainer;

/**
 * The entry point for a JavaGems3D-ToolBox map editor program.
 */
public interface JGemsTBoxApplication {
    /**
     * Using this method, you can add new objects placed on the map to the map editing program (ToolBox).
     *
     * @param tBoxObjectsContainer your TBox object data container.
     */
    void fillTBoxObjectsContainer(IAppTBoxObjectsContainer tBoxObjectsContainer);
}
