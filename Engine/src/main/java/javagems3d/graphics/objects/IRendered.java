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
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;

import java.util.Set;

public interface IRendered {
    RenderAttributes getRenderAttributes();
    
    default boolean canBeRendered() {
        return this.getRenderAttributes() != null;
    }

    default boolean canBeRendered(Pipeline pipeline) {
        return this.canBeRendered() && this.getRenderingTable().getRenderingData(pipeline).getRenderFabric() != null;
    }

    default Set<IRenderFabric> getRenderFabricsSet() {
        return this.getRenderingTable().getRenderFabricsSet();
    }

    default IRenderFabric getRenderFabric(Pipeline pipeline) {
        return this.getRenderingTable().getRenderFabric(pipeline);
    }

    default RenderTable getRenderingTable() {
        return this.getRenderAttributes().getRenderingTable();
    }
}