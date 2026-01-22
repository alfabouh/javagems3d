package workbench.project.managing.instances;
import javagems3d.mapping.tags.items.TagItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameResourceObjectTagData implements IAsset {
    private final String name;
    private TagItem tagItem;

    public GameResourceObjectTagData(@NotNull String name, @Nullable TagItem tagItem) {
        this.tagItem = tagItem;
        this.name = name;
    }

    public GameResourceObjectTagData setTagItem(TagItem tagItem) {
        this.tagItem = tagItem;
        return this;
    }

    public TagItem getTagItem() {
        return this.tagItem;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
