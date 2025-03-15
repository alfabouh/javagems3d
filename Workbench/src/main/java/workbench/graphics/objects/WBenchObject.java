package workbench.graphics.objects;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.help.JGemsMathHelper;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.world.WBenchWorld;

import java.util.Objects;

public class WBenchObject extends SceneProp {
    private int id;
    private final String name;
    private final TagsContainer tagsContainer;
    private final TranslationConstraints translationConstraints;

    public WBenchObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate) {
        super(wBenchWorld, new Model3D(new Pose3D(), objectTemplate.getMeshGroup()), objectTemplate.getRenderAttributes());
        this.name = objectTemplate.getId();
        this.tagsContainer = new TagsContainer(objectTemplate.getTagsContainer());
        this.id = -1;

        this.translationConstraints = objectTemplate.getTranslationConstraints();
    }

    public void setPosition(Vector3f vector3f) {
        float clampedX = JGemsMathHelper.clamp(vector3f.x, -WBench.MAP_SIZE, WBench.MAP_SIZE);
        float clampedY = JGemsMathHelper.clamp(vector3f.y, -WBench.MAP_SIZE, WBench.MAP_SIZE);
        float clampedZ = JGemsMathHelper.clamp(vector3f.z, -WBench.MAP_SIZE, WBench.MAP_SIZE);

        this.getModel().getPose().setPosition(new Vector3f(clampedX, clampedY, clampedZ));
    }

    public void setRotation(Vector3f vector3f) {
        this.getModel().getPose().setRotation(vector3f);
    }

    public void setScaling(Vector3f vector3f) {
        this.getModel().getPose().setScaling(vector3f);
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
