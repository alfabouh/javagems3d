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

package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render;

import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject;

public abstract class RenderWorldItem implements IRenderObjectFabric {
    @Override
    public void onPreRender(IRenderObject renderItem) {
    }

    @Override
    public void onPostRender(IRenderObject renderItem) {
    }
}
