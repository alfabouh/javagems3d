package workbench.graphics.objects;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchMarkerObject extends WBenchObject {
    private final Vector3f color;
    private final boolean transparent;

    public WBenchMarkerObject(@NotNull WBenchObject.ID objectId, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints, @NotNull Vector3f color, boolean transparent) {
        super(objectId, wBenchWorld, meshStructure3D, renderAttributes, tagsContainer, translationConstraints);
        this.color = color;
        this.transparent = transparent;
    }

    public WBenchMarkerObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate, @Nullable TagsContainer tagsContainer, @NotNull Vector3f color, boolean transparent) {
        super(wBenchWorld, objectTemplate, tagsContainer);
        this.color = color;
        this.transparent = transparent;
    }

    @Override
    public WBenchMarkerObject clone() {
        return new WBenchMarkerObject(this.getObjectId(), (WBenchWorld) this.getWorld(), this.getModel().getMeshStructure(), this.getRenderAttributes().copy(), this.getTagsContainer().copy(), this.getTranslationConstraints(), new Vector3f(this.getColor()), this.isTransparent());
    }

    @Override
    protected void onTranslate(Vector3f position) {

    }

    @Override
    protected void onRotate(Vector3f rotation) {

    }

    @Override
    protected void onScale(Vector3f scaling) {

    }

    public boolean isLighted() {
        return true;
    }

    @Override
    public Vector3f textInMenuColor() {
        return new Vector3f(0.0f, 1.0f, 1.0f);
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public Vector3f getColor() {
        return this.color;
    }
}
