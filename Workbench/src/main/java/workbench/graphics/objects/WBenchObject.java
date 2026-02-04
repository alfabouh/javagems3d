package workbench.graphics.objects;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.map.editor.utils.GlobalWBenchSceneRenderingVars;
import workbench.graphics.scene.world.WBenchWorld;

import java.util.Objects;

public abstract class WBenchObject extends SceneProp {
    private int id;
    private final WBenchObject.ID objectId;
    private final TagsContainer tagsContainer;
    private final TranslationConstraints translationConstraints;

    public WBenchObject(@NotNull WBenchObject.ID objectId, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints) {
        super(objectId.getNameId(), wBenchWorld, new PropRenderData(renderAttributes, meshStructure3D));
        this.id = -1;
        this.objectId = objectId;
        this.tagsContainer = tagsContainer.copy();
        this.translationConstraints = translationConstraints;
    }

    public WBenchObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate, @Nullable TagsContainer tagsContainer) {
        this(objectTemplate.getObjectId(), wBenchWorld, objectTemplate.getMeshGroup(), objectTemplate.getRenderAttributes(), tagsContainer == null ? objectTemplate.getTagsContainer() : tagsContainer, objectTemplate.getTranslationConstraints());
    }

    public void setPosition(Vector3f newPos) {
        if (this.isDead()) {
            return;
        }
        this.getModel().getPose().setPosition(newPos);
        this.onTranslate(newPos);
    }

    public void setRotation(Vector3f newRot) {
        if (this.isDead()) {
            return;
        }
        this.getModel().getPose().setRotation(newRot);
        this.onRotate(newRot);
    }

    public void setScaling(Vector3f newScale) {
        if (this.isDead()) {
            return;
        }
        this.getModel().getPose().setScaling(newScale);
        this.onScale(newScale);
    }

    @Override
    public AnimationData getAnimationData() {
        return !GlobalWBenchSceneRenderingVars.ANIMATIONS ? null : super.getAnimationData();
    }

    public abstract WBenchObject clone();

    protected abstract void onTranslate(Vector3f position);
    protected abstract void onRotate(Vector3f rotation);
    protected abstract void onScale(Vector3f scaling);
    public abstract Vector3f textInMenuColor();

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

    public WBenchObject setId(int id) {
        this.id = id;
        return this;
    }

    public boolean hasTranslationConstraints() {
        return this.getTranslationConstraints().getPositionConstraints().getFlag() + this.getTranslationConstraints().getRotationConstraints().getFlag() + this.getTranslationConstraints().getScalingConstraints().getFlag() > 0;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WBenchObject)) {
            return false;
        }
        WBenchObject that = (WBenchObject) o;
        return this.id == that.id;
    }

    public String toString(boolean textPosition) {
        return "[" + this.getId() + "] " + this.getObjectId().toString() + (textPosition ? (" {" + this.getPosition().x + ", " + this.getPosition().y + ", " + this.getPosition().z + "}") : "");
    }

    @Override
    public String toString() {
        return this.toString(true);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public ID getObjectId() {
        return this.objectId;
    }

    public int getId() {
        return this.id;
    }

    public static final class ID {
        private final String nameId;
        private final String objectPath;

        public ID(@NotNull String nameId) {
            this(nameId, null);
        }

        public ID(@NotNull String nameId, @Nullable String objectPath) {
            this.nameId = nameId;
            this.objectPath = objectPath == null ? "" : objectPath;
        }

        public String getNameId() {
            return this.nameId;
        }

        public String getObjectPath() {
            return this.objectPath;
        }

        @Override
        public String toString() {
            return this.getObjectPath() + "/" + this.getNameId();
        }
    }
}
