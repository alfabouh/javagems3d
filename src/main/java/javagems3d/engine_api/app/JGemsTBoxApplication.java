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

package javagems3d.engine_api.app;

import javagems3d.engine.system.resources.manager.JGemsResourceManager;
import javagems3d.engine_api.app.tbox.ITBoxEntitiesObjectData;
import javagems3d.engine_api.app.tbox.TBoxEntitiesUserData;
import javagems3d.toolbox.resources.TBoxResourceManager;

/**
 * The entry point for a JavaGems3D-ToolBox map editor program.
 */
public interface JGemsTBoxApplication {
    /**
     * Using this method, you can add objects to the ToolBox World Editor program
     *
     * @param tBoxResourceManager    resource manager. You can use it to create model and texture objects
     * @param tBoxEntitiesObjectData Use this object to add an entity to the array
     */
    void initEntitiesObjectData(TBoxResourceManager tBoxResourceManager, ITBoxEntitiesObjectData tBoxEntitiesObjectData);

    /**
     * With this method, you can set your information for a ToolBox entity with a specific ID.
     * For example, you can specify information for the render
     *
     * @param tBoxEntitiesUserData Use this object to add any data to the array
     */
    void initEntitiesUserData(JGemsResourceManager jGemsResourceManager, TBoxEntitiesUserData tBoxEntitiesUserData);
}