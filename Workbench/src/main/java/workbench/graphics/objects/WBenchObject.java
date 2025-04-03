package workbench.graphics.objects;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.help.JGemsMathHelper;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.world.WBenchWorld;

import java.util.Objects;

public abstract class WBenchObject extends SceneProp {
    private int id;
    private final String name;
    private final TagsContainer tagsContainer;
    private final TranslationConstraints translationConstraints;

    public WBenchObject(@NotNull String name, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints) {
        super(wBenchWorld, new Model3D(new Pose3D(), meshStructure3D), renderAttributes);
        this.id = -1;
        this.name = name;
        this.tagsContainer = tagsContainer;
        this.translationConstraints = translationConstraints;
    }

    public WBenchObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate) {
        this(objectTemplate.getId(), wBenchWorld, objectTemplate.getMeshGroup(), objectTemplate.getRenderAttributes(), objectTemplate.getTagsContainer(), objectTemplate.getTranslationConstraints());
    }

    public void setPosition(Vector3f vector3f) {
        this.getModel().getPose().setPosition(vector3f);
        this.normalizePosition();
        this.onTranslate(this.getPosition());
    }

    public void setRotation(Vector3f vector3f) {
        this.getModel().getPose().setRotation(vector3f);
        this.normalizePosition();
        this.onRotate(this.getRotation());
    }

    public void setScaling(Vector3f vector3f) {
        this.getModel().getPose().setScaling(vector3f);
        this.normalizePosition();
        this.onScale(this.getScaling());
    }

    protected abstract void onTranslate(Vector3f position);
    protected abstract void onRotate(Vector3f rotation);
    protected abstract void onScale(Vector3f scaling);
    public abstract Vector3f textInMenuColor();

    public void normalizePosition() {
        if (this.isDead()) {
            return;
        }
        CullingAABB cullingAABB = this.getCullingData();
        if (cullingAABB != null) {
            Vector3f aabbMin = cullingAABB.getAabbMin();
            Vector3f aabbMax = cullingAABB.getAabbMax();
            Vector3f currentPos = this.getModel().getPose().getPosition();
            Vector3f worldSize = new Vector3f(aabbMax).sub(aabbMin);

            float halfWidth = worldSize.x / 2.0f;
            float halfHeight = worldSize.y / 2.0f;
            float halfDepth = worldSize.z / 2.0f;

            float clampedX = JGemsMathHelper.clamp(currentPos.x, -WBench.MAP_SIZE + halfWidth, WBench.MAP_SIZE - halfWidth);
            float clampedY = JGemsMathHelper.clamp(currentPos.y, -WBench.MAP_SIZE + halfHeight, WBench.MAP_SIZE - halfHeight);
            float clampedZ = JGemsMathHelper.clamp(currentPos.z, -WBench.MAP_SIZE + halfDepth, WBench.MAP_SIZE - halfDepth);

            if (worldSize.x > WBench.MAP_SIZE || worldSize.y > WBench.MAP_SIZE || worldSize.z > WBench.MAP_SIZE) {
                Log.get().warn("Object " + this + " was removed due to it's size!");
                this.setDead();
                return;
            }
            this.getModel().getPose().setPosition(new Vector3f(clampedX, clampedY, clampedZ));
        } else {
            Vector3f currentPos = this.getModel().getPose().getPosition();
            float clampedX = JGemsMathHelper.clamp(currentPos.x, -WBench.MAP_SIZE, WBench.MAP_SIZE);
            float clampedY = JGemsMathHelper.clamp(currentPos.y, -WBench.MAP_SIZE, WBench.MAP_SIZE);
            float clampedZ = JGemsMathHelper.clamp(currentPos.z, -WBench.MAP_SIZE, WBench.MAP_SIZE);
            this.getModel().getPose().setPosition(new Vector3f(clampedX, clampedY, clampedZ));
        }
    }

    public Vector3f getPosition() {
        return this.getModel().getPose().getPosition();
    }

    public Vector3f getRotation() {
        return this.getModel().getPose().getRotation();
    }

    public Vector3f getScaling() {
        return this.getModel().getPose().getScaling();
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

    @Override
    public String toString() {
        return this.getName() + " " + this.getPosition();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }
}
