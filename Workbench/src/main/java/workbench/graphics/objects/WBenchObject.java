package workbench.graphics.objects;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.map.editor.utils.GlobalWBenchSceneRenderingVars;
import workbench.graphics.scene.world.WBenchWorld;

import java.util.Objects;

public abstract class WBenchObject <E extends ISnapshotCompatible.SnapshotData> extends SceneProp implements ISnapshotCompatible<E> {
    private int id;
    private final WBenchObject.ID objectId;
    private TagsContainer tagsContainer;
    private final TranslationConstraints translationConstraints;
    private boolean forceConstraints;

    public WBenchObject(@NotNull WBenchObject.ID objectId, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints) {
        super(objectId.nameId(), wBenchWorld, new PropRenderData(renderAttributes, meshStructure3D));
        this.id = -1;
        this.objectId = objectId;
        this.tagsContainer = tagsContainer.copy();
        this.translationConstraints = translationConstraints;
        this.forceConstraints = false;
    }

    public WBenchObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate, @Nullable TagsContainer tagsContainer) {
        this(objectTemplate.getObjectId(), wBenchWorld, objectTemplate.getMeshGroup(), objectTemplate.getRenderAttributes().copy(), tagsContainer == null ? objectTemplate.getTagsContainer() : tagsContainer, objectTemplate.getTranslationConstraints());
    }

    public void setPosition(Vector3f newPos) {
        if (this.isDead()) {
            return;
        }
        if (this.isForceConstraints()) {
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().positionConstraints().getFlag(), AxisConstraints.AXIS_X)) {
                newPos.mul(0.0f, 1.0f, 1.0f);
            }
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().positionConstraints().getFlag(), AxisConstraints.AXIS_Y)) {
                newPos.mul(1.0f, 0.0f, 1.0f);
            }
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().positionConstraints().getFlag(), AxisConstraints.AXIS_Z)) {
                newPos.mul(1.0f, 1.0f, 0.0f);
            }
        }
        this.getModel().getPose().setPosition(newPos);
        this.onTranslate(newPos);
    }

    public void setRotation(Vector3f newRot) {
        if (this.isDead()) {
            return;
        }
        if (this.isForceConstraints()) {
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().rotationConstraints().getFlag(), AxisConstraints.AXIS_X)) {
                newRot.mul(0.0f, 1.0f, 1.0f);
            }
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().rotationConstraints().getFlag(), AxisConstraints.AXIS_Y)) {
                newRot.mul(1.0f, 0.0f, 1.0f);
            }
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().rotationConstraints().getFlag(), AxisConstraints.AXIS_Z)) {
                newRot.mul(1.0f, 1.0f, 0.0f);
            }
        }
        this.getModel().getPose().setRotation(
                new Vector3f(
                        (float) JGemsHelper.math().clamp(newRot.x, -Math.PI * 8.0f, Math.PI * 8.0f),
                        (float) JGemsHelper.math().clamp(newRot.y, -Math.PI * 8.0f, Math.PI * 8.0f),
                        (float) JGemsHelper.math().clamp(newRot.z, -Math.PI * 8.0f, Math.PI * 8.0f)
                )
        );
        this.onRotate(newRot);
    }

    public void setScaling(Vector3f newScale) {
        if (this.isDead()) {
            return;
        }
        if (this.isForceConstraints()) {
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().scalingConstraints().getFlag(), AxisConstraints.AXIS_X)) {
                newScale.mul(1.0f, this.getScaling().y, this.getScaling().z);
            }
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().scalingConstraints().getFlag(), AxisConstraints.AXIS_Y)) {
                newScale.mul(this.getScaling().x, 1.0f, this.getScaling().z);
            }
            if (!AxisConstraints.CHECK(this.getTranslationConstraints().scalingConstraints().getFlag(), AxisConstraints.AXIS_Z)) {
                newScale.mul(this.getScaling().x, this.getScaling().y, 1.0f);
            }
        }
        this.getModel().getPose().setScaling(newScale);
        this.onScale(newScale);
    }

    @Override
    public AnimationData getAnimationData() {
        return !GlobalWBenchSceneRenderingVars.ANIMATIONS ? null : super.getAnimationData();
    }

    public abstract WBenchObject<E> clone();

    protected abstract void onTranslate(Vector3f position);
    protected abstract void onRotate(Vector3f rotation);
    protected abstract void onScale(Vector3f scaling);
    public abstract Vector3f textInMenuColor();
    public abstract int orderInList();

    public synchronized Vector3f getPosition() {
        return this.getModel().getPose().getPosition();
    }

    public synchronized Vector3f getRotation() {
        return this.getModel().getPose().getRotation();
    }

    public synchronized Vector3f getScaling() {
        return this.getModel().getPose().getScaling();
    }

    public synchronized TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public synchronized WBenchObject<E> setTagsContainer(TagsContainer tagsContainer) {
        this.tagsContainer = tagsContainer;
        return this;
    }

    public WBenchObject<?> setId(int id) {
        this.id = id;
        return this;
    }

    public boolean hasTranslationConstraints() {
        return this.getTranslationConstraints().positionConstraints().getFlag() + this.getTranslationConstraints().rotationConstraints().getFlag() + this.getTranslationConstraints().scalingConstraints().getFlag() > 0;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }

    @SuppressWarnings("all")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WBenchObject)) {
            return false;
        }
        WBenchObject<E> that = (WBenchObject<E>) o;
        return this.id == that.id;
    }

    public boolean isForceConstraints() {
        return this.forceConstraints;
    }

    public WBenchObject<E> setForceConstraints(boolean forceConstraints) {
        this.forceConstraints = forceConstraints;
        return this;
    }

    public String toString(boolean textPosition) {
        return "[" + this.getListID() + "] " + this.getObjectNameId().toString() + (textPosition ? (" {" + this.getPosition().x + ", " + this.getPosition().y + ", " + this.getPosition().z + "}") : "");
    }

    @Override
    public String toString() {
        return this.toString(true);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public ID getObjectNameId() {
        return this.objectId;
    }

    public int getListID() {
        return this.id;
    }

    public record ID(String nameId, String objectPath) {
            public ID(@NotNull String nameId) {
                this(nameId, null);
            }

            public ID(@NotNull String nameId, @Nullable String objectPath) {
                this.nameId = nameId;
                this.objectPath = objectPath == null ? "" : objectPath;
            }

            @Override
            public String toString() {
                return this.objectPath() + "/" + this.nameId();
            }
        }

    public abstract static class WBenchObjectSnapshotData implements ISnapshotCompatible.SnapshotData {
        public final TagsContainer tagsContainer;
        public final TranslationConstraints translationConstraints;
        public final RenderProperties renderProperties;
        public final boolean isVisible;
        public final boolean isDead;

        public final Vector3f pos;
        public final Vector3f rot;
        public final Vector3f scale;

        public WBenchObjectSnapshotData(RenderProperties renderProperties, TagsContainer tagsContainer, TranslationConstraints translationConstraints, boolean isVisible, boolean isDead, Vector3f pos, Vector3f rot, Vector3f scale) {
            this.renderProperties = renderProperties;
            this.tagsContainer = tagsContainer;
            this.translationConstraints = translationConstraints;
            this.isVisible = isVisible;
            this.isDead = isDead;
            this.pos = pos;
            this.rot = rot;
            this.scale = scale;
        }
    }
}
