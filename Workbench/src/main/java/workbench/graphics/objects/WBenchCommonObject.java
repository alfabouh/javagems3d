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

public class WBenchCommonObject extends WBenchObject {
    public WBenchCommonObject(@NotNull WBenchObject.ID objectId, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints) {
        super(objectId, wBenchWorld, meshStructure3D, renderAttributes, tagsContainer, translationConstraints);
    }

    public WBenchCommonObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate, @Nullable TagsContainer tagsContainer) {
        super(wBenchWorld, objectTemplate, tagsContainer);
    }

    @Override
    public WBenchCommonObject clone() {
        WBenchCommonObject commonObject = new WBenchCommonObject(this.getObjectId(), (WBenchWorld) this.getWorld(), this.getModel().getMeshStructure(), this.getRenderAttributes().copy(), this.getTagsContainer().copy(), this.getTranslationConstraints());
        commonObject.setPosition(this.getPosition());
        commonObject.setRotation(this.getRotation());
        commonObject.setScaling(this.getScaling());
        return commonObject;
    }

    @Override
    public Vector3f textInMenuColor() {
        return new Vector3f(1.0f);
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
}
