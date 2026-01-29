package workbench.project.managing.instances.misc;
import javagems3d.mapping.tags.TagsContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.project.managing.instances.IAsset;

public class GameResourceObjectTagData implements IAsset {
    private final String name;
    private TagsContainer tagsContainer;

    public GameResourceObjectTagData(@NotNull String name, @Nullable TagsContainer tagsContainer) {
        this.tagsContainer = tagsContainer;
        this.name = name;
    }

    public GameResourceObjectTagData setTagContainer(TagsContainer tagItem) {
        this.tagsContainer = tagItem;
        return this;
    }

    public TagsContainer getTagContainer() {
        return this.tagsContainer;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
