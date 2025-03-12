package javagems3d.graphics.objects.rendering.configuration;

import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("all")
public class RenderAttributes implements IRenderConfiguration, ICopyable<RenderAttributes> {
    private RenderTable renderTable;
    private RenderProperties renderProperties;

    public RenderAttributes(@NotNull RenderTable renderTable) {
        this(renderTable, RenderProperties.get());
    }

    public RenderAttributes(@NotNull RenderTable renderTable, @NotNull RenderProperties renderProperties) {
        this.renderTable = renderTable;
        this.renderProperties = renderProperties;
    }

    public static @NotNull RenderAttributes get(@Nullable RenderTable renderTable) {
        return renderTable == null ? null : new RenderAttributes(renderTable);
    }

    public static @NotNull RenderAttributes get(@Nullable RenderTable renderTable, @NotNull RenderProperties renderProperties) {
        return (renderTable == null || renderProperties == null) ? null : new RenderAttributes(renderTable, renderProperties);
    }

    public RenderAttributes setRenderTable(@NotNull RenderTable renderTable) {
        this.renderTable = renderTable;
        return this;
    }

    public RenderAttributes setRenderProperties(@NotNull RenderProperties renderProperties) {
        this.renderProperties = renderProperties;
        return this;
    }

    public RenderTable getRenderTable() {
        return this.renderTable;
    }

    public RenderProperties getProperties() {
        return this.renderProperties;
    }


    @Override
    public @NotNull RenderAttributes copy() {
        return new RenderAttributes(this.getRenderTable().copy(), this.getProperties().copy());
    }

    public @NotNull CullingRules getCullingRules() {
        return this.getProperties().getCullingRules();
    }
}
