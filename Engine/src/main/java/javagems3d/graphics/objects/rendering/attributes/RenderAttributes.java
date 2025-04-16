package javagems3d.graphics.objects.rendering.attributes;

import javagems3d.graphics.objects.rendering.attributes.base.IRenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("all")
public class RenderAttributes implements IRenderAttributes, ICopyable<RenderAttributes> {
    private RenderTable renderTable;
    private RenderProperties renderProperties;

    public RenderAttributes(@NotNull RenderTable renderTable, @NotNull RenderProperties renderProperties) {
        this.renderTable = renderTable;
        this.renderProperties = renderProperties;
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


    public static RenderAttributes getDefaultDirect() {
        return new RenderAttributes(RenderTable.getDirect(), JGemsRenderProperties.getDefault());
    }

    public static RenderAttributes getDefaultIndirect() {
        return new RenderAttributes(RenderTable.getIndirect(), JGemsRenderProperties.getDefault());
    }

    public static RenderAttributes getDefaultDirect(@NotNull RenderProperties renderProperties) {
        return new RenderAttributes(RenderTable.getDirect(), renderProperties);
    }

    public static RenderAttributes getDefaultIndirect(@NotNull RenderProperties renderProperties) {
        return new RenderAttributes(RenderTable.getIndirect(), renderProperties);
    }

    @Override
    public @NotNull RenderAttributes copy() {
        return new RenderAttributes(this.getRenderTable().copy(), this.getProperties().copy());
    }

    public @NotNull CullingRules getCullingRules() {
        return this.getProperties().getCullingRules();
    }
}
