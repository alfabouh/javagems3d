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

package javagems3d.toolbox.render.scene.items.renderers;

import javagems3d.toolbox.map_sys.save.objects.MapProperties;
import javagems3d.toolbox.render.scene.items.objects.base.TBoxAbstractObject;

public interface ITBoxObjectRenderer {
    void onRender(MapProperties properties, TBoxAbstractObject tBoxAbstractObject, float deltaTime);

    void preRender(TBoxAbstractObject tBoxAbstractObject);

    void postRender(TBoxAbstractObject tBoxAbstractObject);
}
