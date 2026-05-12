package workbench.graphics.objects;

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

public class WBenchMarkerObject extends WBenchObject<WBenchMarkerObject.WBenchMarkerObjectSnapshotData> {
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
        TagsContainer tagsContainer = this.getTagsContainer().copy();
        if (this.getObjectInstanceExtension() != null) {
            this.getObjectInstanceExtension().onObjectCloned(tagsContainer, this);
        }
        WBenchMarkerObject wBenchMarkerObject = new WBenchMarkerObject(this.getObjectNameId(), (WBenchWorld) this.getWorld(), this.getModel().getMeshStructure(), this.getRenderAttributes().copy(), tagsContainer, this.getTranslationConstraints(), new Vector3f(this.getColor()), this.isTransparent());
        wBenchMarkerObject.setPosition(this.getPosition());
        wBenchMarkerObject.setRotation(this.getRotation());
        wBenchMarkerObject.setScaling(this.getScaling());
        return wBenchMarkerObject;
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
        if (this.getObjectInstanceExtension() != null) {
            return this.getObjectInstanceExtension().shouldMarkerBeFullLighted();
        }
        return false;
    }

    @Override
    public Vector3f textInMenuColor() {
        if (this.getObjectInstanceExtension() != null) {
            return this.getObjectInstanceExtension().textInMenuColor();
        }
        return new Vector3f(0.0f, 1.0f, 1.0f);
    }

    @Override
    public int orderInList() {
        if (this.getObjectInstanceExtension() != null) {
            return this.getObjectInstanceExtension().orderInItemsList();
        }
        return 1;
    }

    @Override
    public WBenchData.ObjectType objectType() {
        return WBenchData.ObjectType.MARKER;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public Vector3f getColor() {
        if (this.getObjectInstanceExtension() != null && this.getObjectInstanceExtension().returnNewMarkerColor() != null) {
            return this.getObjectInstanceExtension().returnNewMarkerColor();
        }
        return this.color;
    }

    @Override
    public WBenchMarkerObject.WBenchMarkerObjectSnapshotData takeSnapshot() {
        return new WBenchMarkerObjectSnapshotData(this.getRenderAttributes().getProperties().copy(), new Vector3f(this.color), this.transparent, this.getTagsContainer().copy(), this.getTranslationConstraints(), this.isVisible, this.isDead, new Vector3f(this.getPosition()), new Vector3f(this.getRotation()), new Vector3f(this.getScaling()));
    }

    @Override
    public void fixSnapshot(WBenchMarkerObject.WBenchMarkerObjectSnapshotData wBenchMarkerObjectSnapshotData) {
        this.getRenderAttributes().setRenderProperties(wBenchMarkerObjectSnapshotData.renderProperties);
        this.color.set(wBenchMarkerObjectSnapshotData.color);
        //this.transparent = wBenchMarkerObjectSnapshotData.transparent;
        this.setTagsContainer(wBenchMarkerObjectSnapshotData.tagsContainer);
        this.setVisible(wBenchMarkerObjectSnapshotData.isVisible);
        this.isDead = wBenchMarkerObjectSnapshotData.isDead;
        this.setPosition(wBenchMarkerObjectSnapshotData.pos);
        this.setRotation(wBenchMarkerObjectSnapshotData.rot);
        this.setScaling(wBenchMarkerObjectSnapshotData.scale);

        if (this.getObjectInstanceExtension() != null) {
            this.getObjectInstanceExtension().onApplySnapshot(wBenchMarkerObjectSnapshotData.tagsContainer, this);
        }
    }

    public static class WBenchMarkerObjectSnapshotData extends WBenchObject.WBenchObjectSnapshotData {
        public final Vector3f color;
        public final boolean transparent;

        public WBenchMarkerObjectSnapshotData(RenderProperties renderProperties, Vector3f color, boolean transparent, TagsContainer tagsContainer, TranslationConstraints translationConstraints, boolean isVisible, boolean isDead, Vector3f pos, Vector3f rot, Vector3f scale) {
            super(renderProperties, tagsContainer, translationConstraints, isVisible, isDead, pos, rot, scale);
            this.color = color;
            this.transparent = transparent;
        }
    }
}
