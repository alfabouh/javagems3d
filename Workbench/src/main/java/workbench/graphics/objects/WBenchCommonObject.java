package workbench.graphics.objects;

import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchData;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchCommonObject extends WBenchObject<WBenchCommonObject.WBenchCommonObjectSnapshotData> {
    public WBenchCommonObject(@NotNull WBenchObject.ID objectId, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints) {
        super(objectId, wBenchWorld, meshStructure3D, renderAttributes, tagsContainer, translationConstraints);
    }

    public WBenchCommonObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate, @Nullable TagsContainer tagsContainer) {
        super(wBenchWorld, objectTemplate, tagsContainer);
    }

    @Override
    public WBenchCommonObject clone() {
        TagsContainer tagsContainer = this.getTagsContainer().copy();
        if (this.getObjectInstanceExtension() != null) {
            this.getObjectInstanceExtension().onObjectCloned(tagsContainer, this);
        }
        WBenchCommonObject commonObject = new WBenchCommonObject(this.getObjectNameId(), (WBenchWorld) this.getWorld(), this.getModel().getMeshStructure(), this.getRenderAttributes().copy(), tagsContainer, this.getTranslationConstraints());
        commonObject.setPosition(this.getPosition());
        commonObject.setRotation(this.getRotation());
        commonObject.setScaling(this.getScaling());
        return commonObject;
    }

    @Override
    public Vector3f textInMenuColor() {
        if (this.getObjectInstanceExtension() != null) {
            return this.getObjectInstanceExtension().textInMenuColor();
        }
        return new Vector3f(1.0f);
    }

    @Override
    public int orderInList() {
        if (this.getObjectInstanceExtension() != null) {
            return this.getObjectInstanceExtension().orderInItemsList();
        }
        return 0;
    }

    @Override
    public WBenchData.ObjectType objectType() {
        return this.getObjectNameId().nameId().startsWith(MapObjectsIdentifiers.ENTITY) ? WBenchData.ObjectType.ENTITY : WBenchData.ObjectType.PROP;
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

    @Override
    public WBenchCommonObject.WBenchCommonObjectSnapshotData takeSnapshot() {
        return new WBenchCommonObject.WBenchCommonObjectSnapshotData(this.getRenderAttributes().getProperties().copy(), this.getTagsContainer().copy(), this.getTranslationConstraints(), this.isVisible, this.isDead, new Vector3f(this.getPosition()), new Vector3f(this.getRotation()), new Vector3f(this.getScaling()));
    }

    @Override
    public void fixSnapshot(WBenchCommonObjectSnapshotData wBenchCommonObjectSnapshotData) {
        this.getRenderAttributes().setRenderProperties(wBenchCommonObjectSnapshotData.renderProperties);
        this.setTagsContainer(wBenchCommonObjectSnapshotData.tagsContainer);
        this.setVisible(wBenchCommonObjectSnapshotData.isVisible);
        this.isDead = wBenchCommonObjectSnapshotData.isDead;
        this.setPosition(wBenchCommonObjectSnapshotData.pos);
        this.setRotation(wBenchCommonObjectSnapshotData.rot);
        this.setScaling(wBenchCommonObjectSnapshotData.scale);

        if (this.getObjectInstanceExtension() != null) {
            this.getObjectInstanceExtension().onApplySnapshot(wBenchCommonObjectSnapshotData.tagsContainer, this);
        }
    }

    public static class WBenchCommonObjectSnapshotData extends WBenchObject.WBenchObjectSnapshotData {
        public WBenchCommonObjectSnapshotData(RenderProperties renderProperties, TagsContainer tagsContainer, TranslationConstraints translationConstraints, boolean isVisible, boolean isDead, Vector3f pos, Vector3f rot, Vector3f scale) {
            super(renderProperties, tagsContainer, translationConstraints, isVisible, isDead, pos, rot, scale);
        }
    }
}
