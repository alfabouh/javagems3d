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

package javagems3d.graphics.objects;

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.configuration.ShadingTable;
import javagems3d.graphics.objects.rendering.fabric.IRenderFabric;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public interface IRendered {
    IRenderFabric getRenderFabric();
    RenderAttributes getRenderAttributes();
    
    default boolean hasRender() {
        return this.getRenderFabric() != null;
    }

    default ShadingTable getShadingTable() {
        return this.getRenderAttributes().getShadingTable();
    }
}
