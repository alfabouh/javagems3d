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

package ru.jgems3d.toolbox.render.scene.items.renderers;

import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;

public interface ITBoxObjectRenderer {
    void onRender(MapProperties properties, TBoxScene3DObject tBoxScene3DObject, float deltaTime);

    void preRender(TBoxScene3DObject tBoxScene3DObject);

    void postRender(TBoxScene3DObject tBoxScene3DObject);
}
