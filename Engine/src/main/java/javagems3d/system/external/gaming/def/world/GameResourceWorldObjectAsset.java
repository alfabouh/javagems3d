package javagems3d.system.external.gaming.def.world;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.gaming.def.IAsset;

public abstract class GameResourceWorldObjectAsset implements IAsset {
    private final String ID;
    private String modelAssetRelativePath;
    private TagsContainer tagsContainer;
    private TranslationConstraints axisConstraints;
    private RenderProperties renderProperties;

    public GameResourceWorldObjectAsset(String ID, String modelAssetRelativePath, TagsContainer tagsContainer, RenderProperties renderProperties, TranslationConstraints axisConstraints) {
        this.ID = ID;
        this.modelAssetRelativePath = modelAssetRelativePath;
        this.tagsContainer = tagsContainer;
        this.renderProperties = renderProperties;
        this.axisConstraints = axisConstraints;
    }

    public String getID() {
        return this.ID;
    }

    public String getModelAssetRelativePath() {
        return this.modelAssetRelativePath;
    }

    public GameResourceWorldObjectAsset setModelAssetRelativePath(String modelAssetRelativePath) {
        this.modelAssetRelativePath = modelAssetRelativePath;
        return this;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public GameResourceWorldObjectAsset setTagsContainer(TagsContainer tagsContainer) {
        this.tagsContainer = tagsContainer;
        return this;
    }

    public TranslationConstraints getAxisConstraints() {
        return this.axisConstraints;
    }

    public GameResourceWorldObjectAsset setAxisConstraints(TranslationConstraints axisConstraints) {
        this.axisConstraints = axisConstraints;
        return this;
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }

    public GameResourceWorldObjectAsset setRenderProperties(RenderProperties renderProperties) {
        this.renderProperties = renderProperties;
        return this;
    }

    @Override
    public String toString() {
        return this.getID();
    }

    @Override
    public String name() {
        return this.getID();
    }
}