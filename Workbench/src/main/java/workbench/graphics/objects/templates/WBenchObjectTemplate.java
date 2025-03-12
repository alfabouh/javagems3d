package workbench.graphics.objects.templates;

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

public final class WBenchObjectTemplate {
    private final String id;
    private final Type type;
    private final MeshGroup meshGroup;
    private final RenderAttributes renderAttributes;

    public WBenchObjectTemplate(String id, Type type, MeshGroup meshGroup, RenderAttributes renderAttributes) {
        this.id = id;
        this.type = type;
        this.meshGroup = meshGroup;
        this.renderAttributes = renderAttributes;
    }

    public String getId() {
        return this.id;
    }

    public Type getType() {
        return this.type;
    }

    public MeshGroup getMeshGroup() {
        return this.meshGroup;
    }

    public RenderAttributes getRenderAttributes() {
        return this.renderAttributes;
    }
}