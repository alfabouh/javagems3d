package javagems3d.graphics.objects;

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IRendered extends ICulled {
    RenderAttributes getRenderAttributes();

    @Override
    default @NotNull CullingRules getCullingRules() {
        return this.getRenderAttributes().getCullingRules();
    }

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