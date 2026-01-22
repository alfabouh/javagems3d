package workbench.project.managing.instances;

import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;

public class GameResourcePropObjectAsset implements IAsset {
    private final String ID;
    private String modelAssetRelativePath;
    private TagsContainer tagsContainer;
    private TranslationConstraints axisConstraints;

    public GameResourcePropObjectAsset(String ID, String modelAssetRelativePath, TagsContainer tagsContainer, TranslationConstraints axisConstraints) {
        this.ID = ID;
        this.modelAssetRelativePath = modelAssetRelativePath;
        this.tagsContainer = tagsContainer;
        this.axisConstraints = axisConstraints;
    }

    public String getID() {
        return this.ID;
    }

    public String getModelAssetRelativePath() {
        return this.modelAssetRelativePath;
    }

    public GameResourcePropObjectAsset setModelAssetRelativePath(String modelAssetRelativePath) {
        this.modelAssetRelativePath = modelAssetRelativePath;
        return this;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public GameResourcePropObjectAsset setTagsContainer(TagsContainer tagsContainer) {
        this.tagsContainer = tagsContainer;
        return this;
    }

    public TranslationConstraints getAxisConstraints() {
        return this.axisConstraints;
    }

    public GameResourcePropObjectAsset setAxisConstraints(TranslationConstraints axisConstraints) {
        this.axisConstraints = axisConstraints;
        return this;
    }

    @Override
    public String toString() {
        return this.getID();
    }

    @Override
    public String getName() {
        return this.getID();
    }
}
